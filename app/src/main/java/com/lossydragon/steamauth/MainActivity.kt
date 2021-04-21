package com.lossydragon.steamauth

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.livedata.observeAsState
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lossydragon.steamauth.steamauth.MaFileLoader
import com.lossydragon.steamauth.ui.AppTheme
import com.lossydragon.steamauth.utils.PrefsManager
import com.lossydragon.steamauth.utils.requestPermissionsReasoning
import com.lossydragon.steamauth.utils.showInfo
import java.io.IOException
import java.io.InputStream

// TODO: Nav/Status bar color
// TODO: onResult deprecation
class MainViewModel : ViewModel() {

    private var _showTotpScreen = MutableLiveData<Boolean>()
    val showTotpScreen: LiveData<Boolean> = _showTotpScreen

    fun showTotpScreen(value: Boolean) {
        _showTotpScreen.value = value
    }
}

class MainActivity : ComponentActivity() {

    companion object {
        private const val REQUEST_CODE = 447
    }

    private val viewModel: MainViewModel by viewModels()
    private var clipboard: ClipboardManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        setContent {
            val hasSecret = PrefsManager.sharedSecret.isNotEmpty()
            val showTotp = viewModel.showTotpScreen.observeAsState(hasSecret)
            if (!showTotp.value) {
                AppTheme {
                    WelcomeScreen(
                        onCleared = { finishAffinity() },
                        onFabClick = { checkPermissions() })
                }
            } else {
                // Block screenshots and overview preview
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE
                )
                AppTheme {
                    TotpScreen(
                        name = PrefsManager.accountName,
                        revocation = PrefsManager.revocationCode,
                        onCleared = { finishAffinity() },
                        onLongClick = {
                            val clip = ClipData.newPlainText(getString(R.string.clip_label), it)
                            clipboard?.setPrimaryClip(clip)
                            Toast.makeText(
                                this,
                                getString(R.string.toast_copied),
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        clipboard = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 42) {
            if (PrefsManager.firstTime) {
                showInfo { finishAffinity() }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val currentUri: Uri

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            if (data != null) {
                currentUri = data.data!!

                try {
                    val jsonString: String = readFileContent(currentUri)
                    val result = MaFileLoader.importMaFile(jsonString)

                    if (result) {
                        viewModel.showTotpScreen(true)
                    }

                } catch (e: IOException) {
                    // Handle error here
                }
            }
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) ==
            PermissionChecker.PERMISSION_GRANTED
        ) {
            //Permission already Granted, go to file chooser dialog
            addAccount()
        } else {
            //Ask for permissions.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)) {
                //Ask for permissions with reasoning.
                requestPermissionsReasoning()
            } else {
                //Ask for permissions, normal
                ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE), 42)
            }
        }
    }

    private fun addAccount() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*" // This is just dumb
        }
        startActivityForResult(intent, REQUEST_CODE)
    }

    private fun readFileContent(uri: Uri): String {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        return inputStream!!.bufferedReader().readText()
    }
}
