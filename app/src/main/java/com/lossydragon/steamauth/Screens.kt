package com.lossydragon.steamauth

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.lossydragon.steamauth.ui.AppBar
import com.lossydragon.steamauth.utils.toast
import com.lossydragon.steamauth.utils.totpFlow
import com.lossydragon.steamauth.utils.upperCase
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(
    onCleared: () -> Unit,
    onShowDialog: () -> Unit,
    onFabClick: () -> Unit
) {
    Scaffold(
        topBar = {
            AppBar(onCleared = onCleared, onShowDialog = onShowDialog)
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(id = R.string.fab_import_account),
                    )
                },
                text = {
                    Text(text = stringResource(id = R.string.fab_import_account).upperCase())
                },
                onClick = { onFabClick() },
            )
        },
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(id = R.string.frag_welcome_text),
                fontSize = 24.sp,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TwoFactorScreen(
    name: String,
    revocation: String,
    onShowDialog: () -> Unit,
    onCleared: () -> Unit,
) {
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val accountName by remember { mutableStateOf(name) }
    val revocationCode by remember { mutableStateOf(revocation) }

    Scaffold(
        topBar = {
            AppBar(
                onCleared = onCleared,
                onShowDialog = onShowDialog
            )
        },
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (info, totp, button) = createRefs()

            Column(
                modifier = Modifier.constrainAs(info) {
                    width = Dimension.fillToConstraints
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 25.dp),
                    textAlign = TextAlign.Center,
                    text = stringResource(id = R.string.totp_title),
                    fontSize = 28.sp,
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 25.dp),
                    textAlign = TextAlign.Center,
                    text = accountName,
                    fontSize = 20.sp,
                )
            }

            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .constrainAs(totp) {
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                        top.linkTo(info.bottom)
                        bottom.linkTo(button.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                contentAlignment = Alignment.Center
            ) {
                val flow: Flow<Pair<Float, String>> = totpFlow()
                val progress = flow.collectAsState(initial = Pair(.5f, "STEAM"))
                val string = stringResource(id = R.string.toast_copied)

                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    progress = progress.value.first,
                    modifier = Modifier.size(300.dp),
                    strokeWidth = 8.dp
                )
                Text(
                    modifier = Modifier.combinedClickable(
                        onClick = { /* no-op */ },
                        onLongClick = {
                            val value = buildAnnotatedString { append(progress.value.second) }
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            clipboard.setText(value)
                            context.toast(string)
                        }
                    ),
                    text = progress.value.second,
                    fontWeight = FontWeight.Medium,
                    fontSize = 64.sp
                )
            }

            val revocationText = stringResource(id = R.string.toast_revocation, revocationCode)
            Button(
                modifier = Modifier.constrainAs(button) {
                    bottom.linkTo(parent.bottom, 48.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                onClick = {
                    context.toast(revocationText)
                }
            ) {
                Text(
                    text = stringResource(id = R.string.button_revocation).upperCase(),
                    color = Color.White
                )
            }
        }
    }
}

/************
 * Previews *
 ************/

@Preview(apiLevel = 31, device = Devices.PIXEL_4_XL, showSystemUi = true)
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen({}, {}, {})
}

@Preview(apiLevel = 31, device = Devices.PIXEL_4_XL, showSystemUi = true)
@Composable
fun TotpScreenPreview() {
    TwoFactorScreen(name = "N/A", revocation = "", onShowDialog = {}, onCleared = {})
}
