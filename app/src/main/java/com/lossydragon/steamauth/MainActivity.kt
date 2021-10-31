package com.lossydragon.steamauth

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.WindowManager.LayoutParams
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.compose.runtime.livedata.observeAsState
import com.lossydragon.steamauth.steamauth.MaFileLoader
import com.lossydragon.steamauth.ui.AppTheme
import com.lossydragon.steamauth.ui.DialogMessage
import com.lossydragon.steamauth.ui.DialogRemoveAccount
import com.lossydragon.steamauth.utils.PrefsManager
import com.lossydragon.steamauth.utils.removeAccount
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val permissions = registerForActivityResult(RequestPermission()) { perm ->
        if (perm) {
            if (PrefsManager.firstTime) {
                viewModel.showInfoDialog(true)
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
                    val jsonString: String = readFileContent(currentUri) ?: return@let
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

        setContent {
            val hasSecret = PrefsManager.sharedSecret.isNotEmpty()
            val showTotp = viewModel.showTotpScreen.observeAsState(hasSecret)

            /* Show Info Dialog */
            val showInfoDialog = viewModel.showInfoDialog.observeAsState()
            if (showInfoDialog.value == true) {
                DialogMessage(
                    onConfirm = {
                        if (PrefsManager.firstTime)
                            addAccount()

                        viewModel.showInfoDialog(false)
                    },
                    onDismiss = {
                        viewModel.showInfoDialog(false)
                    }
                )
            }

            /* Show Delete Account Dialog */
            val showAccountDialog = viewModel.showAccountDialog.observeAsState()
            if (showAccountDialog.value == true) {
                DialogRemoveAccount(
                    onConfirm = {
                        removeAccount {
                            finishAffinity()
                        }
                        viewModel.showAccountDialog(false)
                    },
                    onDismiss = { viewModel.showAccountDialog(false) }
                )
            }

            if (!showTotp.value) {
                AppTheme {
                    WelcomeScreen(
                        onCleared = { finishAffinity() },
                        onShowDialog = { viewModel.showInfoDialog(true) },
                        onFabClick = { checkPermissions() }
                    )
                }
            } else {
                // Block screenshots and overview preview
                if (!BuildConfig.DEBUG)
                    window.setFlags(LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE)

                AppTheme {
                    TotpScreen(
                        name = PrefsManager.accountName,
                        revocation = PrefsManager.revocationCode,
                        onShowDialog = { viewModel.showInfoDialog(true) },
                        onCleared = { viewModel.showAccountDialog(true) }
                    )
                }
            }
        }
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

    @Throws(FileNotFoundException::class)
    private fun readFileContent(uri: Uri): String? {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        return inputStream?.bufferedReader()?.readText()
    }
}
