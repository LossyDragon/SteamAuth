package com.lossydragon.steamauth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private var _showTotpScreen = MutableLiveData<Boolean>()
    val showTotpScreen: LiveData<Boolean> = _showTotpScreen

    fun showTotpScreen(value: Boolean) {
        _showTotpScreen.value = value
    }
}
