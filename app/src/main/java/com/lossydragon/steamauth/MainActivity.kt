package com.lossydragon.steamauth

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.WindowManager.LayoutParams
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.systemBarsPadding
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import com.lossydragon.steamauth.ui.AppTheme
import com.lossydragon.steamauth.utils.getPermissionText
import com.lossydragon.steamauth.utils.waterfallPadding

class MainActivity : ComponentActivity() {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Block screenshots and overview preview
        if (!BuildConfig.DEBUG)
            window.setFlags(LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE)

        Log.d(TAG, "onCreate: ")
        setContent {
            val permissions = rememberPermissionState(READ_EXTERNAL_STORAGE)

            ProvideWindowInsets {
                AppTheme {
                    when {
                        permissions.hasPermission -> {
                            // Permission Granted
                            NavigationScreen()
                        }
                        permissions.shouldShowRationale || !permissions.permissionRequested -> {
                            // Need Permission
                            NeedPermissionsScreen(permissions)
                        }
                        else -> {
                            // Permissions most-likely permanently denied.
                            PermissionsDeniedScreen(permissions)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun NeedPermissionsScreen(permission: PermissionState) {
    val appName = stringResource(id = R.string.app_name)
    PermissionsScreen(
        message = "The following permissions is needed for\n$appName to run properly: \n",
        permissionsList = getPermissionText(permission),
        buttonText = "Request permission"
    ) {
        permission.launchPermissionRequest()
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun PermissionsDeniedScreen(permission: PermissionState) {
    val context = LocalContext.current
    PermissionsScreen(
        message = "Permissions denied.\nPlease manually grant permissions in Settings.\n",
        permissionsList = getPermissionText(permission),
        buttonText = "Open Settings"
    ) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", context.packageName, null)
        context.startActivity(intent)
    }
}

@Composable
private fun PermissionsScreen(
    message: String,
    permissionsList: AnnotatedString,
    buttonText: String,
    onClicked: () -> Unit
) {
    AppTheme {
        Surface {
            Column(
                modifier = Modifier
                    .waterfallPadding()
                    .fillMaxSize()
                    .systemBarsPadding(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = message)
                Text(text = permissionsList)
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = onClicked) {
                    Text(
                        text = buttonText,
                        color = Color.White
                    )
                }
            }
        }
    }
}
