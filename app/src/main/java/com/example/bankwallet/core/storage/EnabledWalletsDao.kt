package com.example.bankwallet.core.storage

import androidx.room.*
import com.example.bankwallet.entities.EnabledWallet
//import io.horizontalsystems.bankwallet.entities.EnabledWallet

@Dao
interface EnabledWalletsDao {

    @Query("SELECT * FROM EnabledWallet ORDER BY `walletOrder` ASC")
    fun enabledCoins(): List<EnabledWallet>

    @Query("SELECT * FROM EnabledWallet WHERE accountId = :accountId ORDER BY `walletOrder` ASC")
    fun enabledCoins(accountId: String): List<EnabledWallet>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(enabledWallet: EnabledWallet)

    @Query("DELETE FROM EnabledWallet")
    fun deleteAll()

    @Transaction
    fun insertWallets(enabledWallets: List<EnabledWallet>) {
        enabledWallets.forEach { insert(it) }
    }

    @Delete
    fun deleteWallets(enabledWallets: List<EnabledWallet>)

}
