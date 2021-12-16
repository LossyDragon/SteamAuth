package com.lossydragon.steamauth

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.systemBarsPadding
import com.lossydragon.steamauth.steamauth.MaFileLoader
import com.lossydragon.steamauth.ui.DialogMessage
import com.lossydragon.steamauth.ui.DialogRemoveAccount
import com.lossydragon.steamauth.utils.PrefsManager
import com.lossydragon.steamauth.utils.removeAccount
import java.io.IOException

sealed class NavScreens(val route: String) {
    object Welcome : NavScreens("screen_welcome")
    object TwoFactor : NavScreens("screen_two_factor")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationScreen() {
    val context = LocalContext.current
    val navController = rememberNavController()

    val hasSecret by remember { mutableStateOf(PrefsManager.sharedSecret.isNotEmpty()) }
    val startDestination = if (hasSecret) NavScreens.TwoFactor.route else NavScreens.Welcome.route

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { result ->
            if (result == null) return@rememberLauncherForActivityResult

            val item = context.contentResolver.openInputStream(result)
            val stream = item?.bufferedReader()?.readText()

            try {
                val wasImported = MaFileLoader.importMaFile(stream)
                if (wasImported) {
                    navController.navigate(NavScreens.TwoFactor.route)
                }
            } catch (e: IOException) {
                Log.w("NavigationScreen", "launcher result: " + e.message.toString())
                return@rememberLauncherForActivityResult
            }
        }

    /* Show Info Dialog */
    var showInfoDialog by remember { mutableStateOf(false) }
    if (showInfoDialog) {
        DialogMessage(
            onConfirm = {
                if (PrefsManager.firstTime)
                    launcher.launch(arrayOf("*/*"))

                showInfoDialog = false
            },
            onDismiss = { showInfoDialog = false }
        )
    }

    /* Show Delete Account Dialog */
    var showAccountDialog by remember { mutableStateOf(false) }
    if (showAccountDialog) {
        DialogRemoveAccount(
            onConfirm = {
                context.removeAccount {
                    (context as Activity).finishAffinity()
                }

                showAccountDialog = false
            },
            onDismiss = { showAccountDialog = false }
        )
    }

    Scaffold(
        modifier = Modifier.systemBarsPadding()
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            startDestination = startDestination,
            navController = navController
        ) {
            composable(NavScreens.Welcome.route) {
                WelcomeScreen(
                    onCleared = { (context as Activity).finishAffinity() },
                    onShowDialog = { showInfoDialog = true },
                    onFabClick = { launcher.launch(arrayOf("*/*")) }
                )
            }
            composable(NavScreens.TwoFactor.route) {
                TwoFactorScreen(
                    name = PrefsManager.accountName,
                    revocation = PrefsManager.revocationCode,
                    onShowDialog = { showInfoDialog = true },
                    onCleared = { showAccountDialog = true }
                )
            }
        }
    }
}
