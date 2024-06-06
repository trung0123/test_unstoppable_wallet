package com.example.bankwallet.modules.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bankwallet.core.managers.UserManager

class MainActivityViewModel(userManager: UserManager): ViewModel() {

    class Factory : ViewModelProvider.Factory {
//        @Suppress("UNCHECKED_CAST")
//        override fun <T : ViewModel> create(modelClass: Class<T>): T {
//            return MainActivityViewModel(App.userManager) as T
//        }
    }
}