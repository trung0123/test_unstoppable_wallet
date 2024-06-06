package com.example.bankwallet.modules.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bankwallet.core.App

object IntroModule {

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return IntroViewModel(App.localStorage) as T
        }
    }

    data class IntroSliderData(
        val title: Int,
        val subTitle: Int,
        val imageLight: Int,
        val imageDark: Int
    )
}