package com.example.bankwallet.core

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.util.Log
import androidx.preference.PreferenceManager
import com.example.bankwallet.BuildConfig
import com.example.bankwallet.core.managers.AccountCleaner
import com.example.bankwallet.core.managers.AccountManager
import com.example.bankwallet.core.managers.KeyStoreCleaner
import com.example.bankwallet.core.managers.LocalStorageManager
import com.example.bankwallet.core.managers.MarketKitWrapper
import com.example.bankwallet.core.managers.SubscriptionManager
import com.example.bankwallet.core.managers.SystemInfoManager
import com.example.bankwallet.core.managers.UserManager
import com.example.bankwallet.core.managers.WalletManager
import com.example.bankwallet.core.managers.WalletStorage
import com.example.bankwallet.core.providers.AppConfigProvider
import com.example.bankwallet.core.storage.AccountsStorage
import com.example.bankwallet.core.storage.AppDatabase
import com.example.bankwallet.core.storage.EnabledWalletsStorage
import com.example.bankwallet.modules.pin.PinComponent
import com.example.bankwallet.modules.pin.core.PinDbStorage
import io.horizontalsystems.core.BackgroundManager
import io.horizontalsystems.core.CoreApp
import io.horizontalsystems.core.ICoreApp
import io.horizontalsystems.core.security.EncryptionManager
import io.horizontalsystems.core.security.KeyStoreManager
import io.reactivex.plugins.RxJavaPlugins
import java.util.logging.Level
import java.util.logging.Logger
import androidx.work.Configuration as WorkConfiguration

class App : CoreApp(), WorkConfiguration.Provider {

    companion object : ICoreApp by CoreApp {
        lateinit var preferences: SharedPreferences
//        lateinit var feeRateProvider: FeeRateProvider
        lateinit var localStorage: ILocalStorage
        lateinit var marketStorage: IMarketStorage
        lateinit var torKitManager: ITorManager
        lateinit var restoreSettingsStorage: IRestoreSettingsStorage
//        lateinit var currencyManager: CurrencyManager
//        lateinit var languageManager: LanguageManager

//        lateinit var blockchainSettingsStorage: BlockchainSettingsStorage
//        lateinit var evmSyncSourceStorage: EvmSyncSourceStorage
//        lateinit var btcBlockchainManager: BtcBlockchainManager
//        lateinit var wordsManager: WordsManager
        lateinit var networkManager: INetworkManager
//        lateinit var backgroundStateChangeListener: BackgroundStateChangeListener
        lateinit var appConfigProvider: AppConfigProvider
        lateinit var adapterManager: IAdapterManager
//        lateinit var transactionAdapterManager: TransactionAdapterManager
        lateinit var walletManager: IWalletManager
//        lateinit var walletActivator: WalletActivator
//        lateinit var tokenAutoEnableManager: TokenAutoEnableManager
        lateinit var walletStorage: IWalletStorage
        lateinit var accountManager: IAccountManager
        lateinit var userManager: UserManager
        lateinit var accountFactory: IAccountFactory
        lateinit var backupManager: IBackupManager
//        lateinit var proFeatureAuthorizationManager: ProFeaturesAuthorizationManager
//        lateinit var zcashBirthdayProvider: ZcashBirthdayProvider

//        lateinit var connectivityManager: ConnectivityManager
        lateinit var appDatabase: AppDatabase
        lateinit var accountsStorage: IAccountsStorage
        lateinit var enabledWalletsStorage: IEnabledWalletStorage
//        lateinit var binanceKitManager: BinanceKitManager
//        lateinit var solanaKitManager: SolanaKitManager
//        lateinit var tronKitManager: TronKitManager
        lateinit var numberFormatter: IAppNumberFormatter
//        lateinit var feeCoinProvider: FeeTokenProvider
        lateinit var accountCleaner: IAccountCleaner
        lateinit var rateAppManager: IRateAppManager
        lateinit var coinManager: ICoinManager
//        lateinit var wcSessionManager: WCSessionManager
//        lateinit var wcManager: WCManager
        lateinit var termsManager: ITermsManager
//        lateinit var marketFavoritesManager: MarketFavoritesManager
        lateinit var marketKit: MarketKitWrapper
//        lateinit var releaseNotesManager: ReleaseNotesManager
//        lateinit var restoreSettingsManager: RestoreSettingsManager
//        lateinit var evmSyncSourceManager: EvmSyncSourceManager
//        lateinit var evmBlockchainManager: EvmBlockchainManager
//        lateinit var solanaRpcSourceManager: SolanaRpcSourceManager
//        lateinit var nftMetadataManager: NftMetadataManager
//        lateinit var nftAdapterManager: NftAdapterManager
//        lateinit var nftMetadataSyncer: NftMetadataSyncer
//        lateinit var evmLabelManager: EvmLabelManager
//        lateinit var baseTokenManager: BaseTokenManager
//        lateinit var balanceViewTypeManager: BalanceViewTypeManager
//        lateinit var balanceHiddenManager: BalanceHiddenManager
//        lateinit var marketWidgetManager: MarketWidgetManager
//        lateinit var marketWidgetRepository: MarketWidgetRepository
//        lateinit var contactsRepository: ContactsRepository
        lateinit var subscriptionManager: SubscriptionManager
//        lateinit var cexProviderManager: CexProviderManager
//        lateinit var cexAssetManager: CexAssetManager
//        lateinit var chartIndicatorManager: ChartIndicatorManager
//        lateinit var backupProvider: BackupProvider
//        lateinit var spamManager: SpamManager
//        lateinit var statsManager: StatsManager
    }

    override fun onCreate() {
        super.onCreate()

        if (!BuildConfig.DEBUG) {
            //Disable logging for lower levels in Release build
            Logger.getLogger("").level = Level.SEVERE
        }

        RxJavaPlugins.setErrorHandler { e: Throwable? ->
            Log.w("RxJava ErrorHandler", e)
        }

        instance = this
        preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        LocalStorageManager(preferences).apply {
            localStorage = this
            pinSettingsStorage = this
            lockoutStorage = this
            thirdKeyboardStorage = this
            marketStorage = this
        }

        val appConfig = AppConfigProvider(localStorage)
        appConfigProvider = appConfig

//        torKitManager = TorManager(instance, localStorage)
        subscriptionManager = SubscriptionManager()

        marketKit = MarketKitWrapper(
            context = this,
            hsApiBaseUrl = appConfig.marketApiBaseUrl,
            hsApiKey = appConfig.marketApiKey,
            subscriptionManager = subscriptionManager
        )

//        feeRateProvider = FeeRateProvider(appConfigProvider)
        backgroundManager = BackgroundManager(this)

        appDatabase = AppDatabase.getInstance(this)
//
//        blockchainSettingsStorage = BlockchainSettingsStorage(appDatabase)
//        evmSyncSourceStorage = EvmSyncSourceStorage(appDatabase)
//        evmSyncSourceManager = EvmSyncSourceManager(appConfigProvider, blockchainSettingsStorage, evmSyncSourceStorage)
//
//        btcBlockchainManager = BtcBlockchainManager(blockchainSettingsStorage, marketKit)
//
//        binanceKitManager = BinanceKitManager()

        accountsStorage = AccountsStorage(appDatabase)
//        restoreSettingsStorage = RestoreSettingsStorage(appDatabase)

//        AppLog.logsDao = appDatabase.logsDao()

        accountCleaner = AccountCleaner()
        accountManager = AccountManager(accountsStorage, accountCleaner)
        userManager = UserManager(accountManager)

//        val proFeaturesStorage = ProFeaturesStorage(appDatabase)
//        proFeatureAuthorizationManager = ProFeaturesAuthorizationManager(proFeaturesStorage, accountManager, appConfigProvider)

        enabledWalletsStorage = EnabledWalletsStorage(appDatabase)
        walletStorage = WalletStorage(marketKit, enabledWalletsStorage)
//
        walletManager = WalletManager(accountManager, walletStorage)
//        coinManager = CoinManager(marketKit, walletManager)

//        solanaRpcSourceManager = SolanaRpcSourceManager(blockchainSettingsStorage, marketKit)
//        val solanaWalletManager = SolanaWalletManager(walletManager, accountManager, marketKit)
//        solanaKitManager = SolanaKitManager(appConfigProvider, solanaRpcSourceManager, solanaWalletManager, backgroundManager)
//
//        tronKitManager = TronKitManager(appConfigProvider, backgroundManager)
//
//        wordsManager = WordsManager(Mnemonic())
//        networkManager = NetworkManager()
//        accountFactory = AccountFactory(accountManager, userManager)
//        backupManager = BackupManager(accountManager)


        KeyStoreManager(
            keyAlias = "MASTER_KEY",
            keyStoreCleaner = KeyStoreCleaner(localStorage, accountManager, walletManager),
            logger = AppLogger("key-store")
        ).apply {
            keyStoreManager = this
            keyProvider = this
        }

//        encryptionManager = EncryptionManager(keyProvider)

//        walletActivator = WalletActivator(walletManager, marketKit)
//        tokenAutoEnableManager = TokenAutoEnableManager(appDatabase.tokenAutoEnabledBlockchainDao())

//        val evmAccountManagerFactory = EvmAccountManagerFactory(
//            accountManager,
//            walletManager,
//            marketKit,
//            tokenAutoEnableManager
//        )
//        evmBlockchainManager = EvmBlockchainManager(
//            backgroundManager,
//            evmSyncSourceManager,
//            marketKit,
//            evmAccountManagerFactory,
//        )
//
//        val tronAccountManager = TronAccountManager(
//            accountManager,
//            walletManager,
//            marketKit,
//            tronKitManager,
//            tokenAutoEnableManager
//        )
//        tronAccountManager.start()

        systemInfoManager = SystemInfoManager(appConfigProvider)
//
//        languageManager = LanguageManager()
//        currencyManager = CurrencyManager(localStorage, appConfigProvider)
//        numberFormatter = NumberFormatter(languageManager)

//        connectivityManager = ConnectivityManager(backgroundManager)
//
//        zcashBirthdayProvider = ZcashBirthdayProvider(this)
//        restoreSettingsManager = RestoreSettingsManager(restoreSettingsStorage, zcashBirthdayProvider)
//
//        evmLabelManager = EvmLabelManager(
//            EvmLabelProvider(),
//            appDatabase.evmAddressLabelDao(),
//            appDatabase.evmMethodLabelDao(),
//            appDatabase.syncerStateDao()
//        )

//        val adapterFactory = AdapterFactory(
//            context = instance,
//            btcBlockchainManager = btcBlockchainManager,
//            evmBlockchainManager = evmBlockchainManager,
//            evmSyncSourceManager = evmSyncSourceManager,
//            binanceKitManager = binanceKitManager,
//            solanaKitManager = solanaKitManager,
//            tronKitManager = tronKitManager,
//            backgroundManager = backgroundManager,
//            restoreSettingsManager = restoreSettingsManager,
//            coinManager = coinManager,
//            evmLabelManager = evmLabelManager,
//            localStorage = localStorage
//        )
//        adapterManager = AdapterManager(walletManager, adapterFactory, btcBlockchainManager, evmBlockchainManager, binanceKitManager, solanaKitManager, tronKitManager)
//        transactionAdapterManager = TransactionAdapterManager(adapterManager, adapterFactory)
//
//        feeCoinProvider = FeeTokenProvider(marketKit)

        pinComponent = PinComponent(
            pinSettingsStorage = pinSettingsStorage,
            excludedActivityNames = listOf(
//                KeyStoreActivity::class.java.name,
//                LockScreenActivity::class.java.name,
//                LauncherActivity::class.java.name,
            ),
            userManager = userManager,
            pinDbStorage = PinDbStorage(appDatabase.pinDao())
        )

//        statsManager = StatsManager(appDatabase.statsDao(), localStorage, marketKit, appConfigProvider)
//        backgroundStateChangeListener = BackgroundStateChangeListener(systemInfoManager, keyStoreManager, pinComponent, statsManager).apply {
//            backgroundManager.registerListener(this)
//        }
//
//        rateAppManager = RateAppManager(walletManager, adapterManager, localStorage)
//
//        wcManager = WCManager(accountManager)
//
//        termsManager = TermsManager(localStorage)
//
//        marketWidgetManager = MarketWidgetManager()
//        marketFavoritesManager = MarketFavoritesManager(appDatabase, marketWidgetManager)
//
//        marketWidgetRepository = MarketWidgetRepository(
//            marketKit,
//            marketFavoritesManager,
//            MarketFavoritesMenuService(localStorage, marketWidgetManager),
//            TopNftCollectionsRepository(marketKit),
//            TopNftCollectionsViewItemFactory(numberFormatter),
//            TopPlatformsRepository(marketKit, currencyManager),
//            currencyManager
//        )
//
//        releaseNotesManager = ReleaseNotesManager(systemInfoManager, localStorage, appConfigProvider)
//
//        setAppTheme()

//        val nftStorage = NftStorage(appDatabase.nftDao(), marketKit)
//        nftMetadataManager = NftMetadataManager(marketKit, appConfigProvider, nftStorage)
//        nftAdapterManager = NftAdapterManager(walletManager, evmBlockchainManager)
//        nftMetadataSyncer = NftMetadataSyncer(nftAdapterManager, nftMetadataManager, nftStorage)
//
//        initializeWalletConnectV2(appConfig)
//
//        wcSessionManager = WCSessionManager(accountManager, WCSessionStorage(appDatabase))
//
//        baseTokenManager = BaseTokenManager(coinManager, localStorage)
//        balanceViewTypeManager = BalanceViewTypeManager(localStorage)
//        balanceHiddenManager = BalanceHiddenManager(localStorage, backgroundManager)
//
//        contactsRepository = ContactsRepository(marketKit)
//        cexProviderManager = CexProviderManager(accountManager)
//        cexAssetManager = CexAssetManager(marketKit, appDatabase.cexAssetsDao())
//        chartIndicatorManager = ChartIndicatorManager(appDatabase.chartIndicatorSettingsDao(), localStorage)

//        backupProvider = BackupProvider(
//            localStorage = localStorage,
//            languageManager = languageManager,
//            walletStorage = enabledWalletsStorage,
//            settingsManager = restoreSettingsManager,
//            accountManager = accountManager,
//            accountFactory = accountFactory,
//            walletManager = walletManager,
//            restoreSettingsManager = restoreSettingsManager,
//            blockchainSettingsStorage = blockchainSettingsStorage,
//            evmBlockchainManager = evmBlockchainManager,
//            marketFavoritesManager = marketFavoritesManager,
//            balanceViewTypeManager = balanceViewTypeManager,
//            appIconService = AppIconService(localStorage),
//            themeService = ThemeService(localStorage),
//            chartIndicatorManager = chartIndicatorManager,
//            chartIndicatorSettingsDao = appDatabase.chartIndicatorSettingsDao(),
//            balanceHiddenManager = balanceHiddenManager,
//            baseTokenManager = baseTokenManager,
//            launchScreenService = LaunchScreenService(localStorage),
//            currencyManager = currencyManager,
//            btcBlockchainManager = btcBlockchainManager,
//            evmSyncSourceManager = evmSyncSourceManager,
//            evmSyncSourceStorage = evmSyncSourceStorage,
//            solanaRpcSourceManager = solanaRpcSourceManager,
//            contactsRepository = contactsRepository
//        )
//
//        spamManager = SpamManager(localStorage)
//
//        startTasks()
    }

    override fun localizedContext(): Context {
        return localeAwareContext(this)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(localeAwareContext(base))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        localeAwareContext(this)
    }

    override val workManagerConfiguration: androidx.work.Configuration
        get() = if (BuildConfig.DEBUG) {
            WorkConfiguration.Builder()
                .setMinimumLoggingLevel(Log.DEBUG)
                .build()
        } else {
            WorkConfiguration.Builder()
                .setMinimumLoggingLevel(Log.ERROR)
                .build()
        }
}