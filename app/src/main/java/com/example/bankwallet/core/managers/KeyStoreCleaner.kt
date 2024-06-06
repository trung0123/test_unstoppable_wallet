package com.example.bankwallet.core.managers

import com.example.bankwallet.core.IAccountManager
import com.example.bankwallet.core.ILocalStorage
import com.example.bankwallet.core.IWalletManager
//import io.horizontalsystems.bankwallet.core.IAccountManager
//import io.horizontalsystems.bankwallet.core.ILocalStorage
//import io.horizontalsystems.bankwallet.core.IWalletManager
import io.horizontalsystems.core.IKeyStoreCleaner

class KeyStoreCleaner(
    private val localStorage: ILocalStorage,
    private val accountManager: IAccountManager,
    private val walletManager: IWalletManager
)
    : IKeyStoreCleaner {

    override var encryptedSampleText: String?
        get() = localStorage.encryptedSampleText
        set(value) {
            localStorage.encryptedSampleText = value
        }

    override fun cleanApp() {
        accountManager.clear()
        walletManager.clear()
        localStorage.clear()
    }
}
