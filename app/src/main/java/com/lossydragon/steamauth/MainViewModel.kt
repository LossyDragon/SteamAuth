package com.lossydragon.steamauth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private var _showAccountDialog = MutableLiveData<Boolean>()
    val showAccountDialog: LiveData<Boolean> = _showAccountDialog

    private var _showInfoDialog = MutableLiveData<Boolean>()
    val showInfoDialog: LiveData<Boolean> = _showInfoDialog

    private var _showTotpScreen = MutableLiveData<Boolean>()
    val showTotpScreen: LiveData<Boolean> = _showTotpScreen

    fun showTotpScreen(value: Boolean) {
        _showTotpScreen.value = value
    }

    fun showInfoDialog(value: Boolean) {
        _showInfoDialog.value = value
    }

    fun showAccountDialog(value: Boolean) {
        _showAccountDialog.value = value
    }
}
