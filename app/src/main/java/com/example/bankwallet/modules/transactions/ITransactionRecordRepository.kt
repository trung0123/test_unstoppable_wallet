package com.example.bankwallet.modules.transactions

import com.example.bankwallet.core.Clearable
import com.example.bankwallet.entities.transactionrecords.TransactionRecord
//import io.horizontalsystems.bankwallet.core.Clearable
//import io.horizontalsystems.bankwallet.entities.transactionrecords.TransactionRecord
//import io.horizontalsystems.bankwallet.modules.contacts.model.Contact
//import io.horizontalsystems.marketkit.models.Blockchain
import io.reactivex.Observable

interface ITransactionRecordRepository : Clearable {
    val itemsObservable: Observable<List<TransactionRecord>>

    fun set(
        transactionWallets: List<TransactionWallet>,
        wallet: TransactionWallet?,
//        transactionType: FilterTransactionType,
//        blockchain: Blockchain?,
//        contact: Contact?
    )
    fun loadNext()
    fun reload()
}
