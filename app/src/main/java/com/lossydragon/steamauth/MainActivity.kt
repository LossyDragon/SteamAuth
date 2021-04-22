package com.lossydragon.steamauth

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.compose.runtime.livedata.observeAsState
import com.lossydragon.steamauth.steamauth.MaFileLoader
import com.lossydragon.steamauth.ui.AppTheme
import com.lossydragon.steamauth.utils.PrefsManager
import com.lossydragon.steamauth.utils.showInfo
import java.io.IOException
import java.io.InputStream

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private var clipboard: ClipboardManager? = null

    private val permissions = registerForActivityResult(RequestPermission()) { perm ->
        if (perm) {
            if (PrefsManager.firstTime) {
                showInfo(
                    onAccept = { addAccount() },
                    onCleared = { finishAffinity() }
                )
            } else {
                addAccount()
            }
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    private val requestDocument = registerForActivityResult(StartActivityForResult()) { result ->
        val currentUri: Uri
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let {
                currentUri = it.data!!

                try {
                    val jsonString: String = readFileContent(currentUri)
                    val wasImported = MaFileLoader.importMaFile(jsonString)

                    if (wasImported) {
                        viewModel.showTotpScreen(true)
                    }

                } catch (e: IOException) {
                    Log.w(this::class.java.simpleName, e.message.toString())
                    return@let
                }
            }
        }
    }


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

    private fun checkPermissions() {
        permissions.launch(READ_EXTERNAL_STORAGE)
    }

    private fun addAccount() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*" // This is just dumb
        }
        requestDocument.launch(intent)
    }

    private fun readFileContent(uri: Uri): String {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        return inputStream!!.bufferedReader().readText()
    }
}
