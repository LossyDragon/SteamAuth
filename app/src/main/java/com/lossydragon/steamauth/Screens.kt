package com.lossydragon.steamauth

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.lossydragon.steamauth.ui.AppBar
import com.lossydragon.steamauth.ui.Snackbar
import com.lossydragon.steamauth.utils.totpFlow
import com.lossydragon.steamauth.utils.upperCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun WelcomeScreen(
    onCleared: () -> Unit,
    onFabClick: () -> Unit
) {
    Scaffold(
        topBar = {
            AppBar(onCleared = { onCleared() })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(id = R.string.fab_import_account),
                    )
                },
                text = { Text(text = stringResource(id = R.string.fab_import_account).upperCase()) },
                onClick = { onFabClick() },
                elevation = FloatingActionButtonDefaults.elevation(8.dp),
                contentColor = Color.White,
                backgroundColor = MaterialTheme.colors.primary,
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TotpScreen(
    name: String,
    revocation: String,
    onCleared: () -> Unit,
    onLongClick: (text: String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val accountName by remember { mutableStateOf(name) }
    val revocationCode by remember { mutableStateOf(revocation) }

    Scaffold(
        topBar = {
            AppBar(onCleared = { onCleared() })
        },
        scaffoldState = scaffoldState,
        snackbarHost = { scaffoldState.snackbarHostState }
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (info, totp, button, snack) = createRefs()

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
                // accountName = PrefsManager.accountName
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

                CircularProgressIndicator(
                    progress = progress.value.first,
                    modifier = Modifier.size(300.dp),
                    strokeWidth = 8.dp
                )
                Text(
                    modifier = Modifier.combinedClickable(
                        onClick = { /* no-op */ },
                        onLongClick = {
                            onLongClick(progress.value.second)
                        }),
                    text = progress.value.second,
                    fontWeight = FontWeight.Medium,
                    fontSize = 64.sp
                )
            }

            val revocationText = stringResource(id = R.string.toast_revocation, revocationCode)
            Button(
                modifier = Modifier.constrainAs(button) {
                    bottom.linkTo(parent.bottom, 24.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                onClick = {
                    scope.launch {
                        scaffoldState.snackbarHostState.showSnackbar(
                            message = revocationText,
                        )
                    }
                }
            ) {
                Text(
                    text = stringResource(id = R.string.button_revocation).upperCase(),
                    color = Color.White
                )
            }

            Snackbar(
                modifier = Modifier.constrainAs(snack) {
                    width = Dimension.fillToConstraints
                    bottom.linkTo(parent.bottom)
                },
                snackBarState = scaffoldState.snackbarHostState,
            )
        }
    }
}

/************
 * Previews *
 ************/

@Preview
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen({}, {})
}

@Preview
@Composable
fun TotpScreenPreview() {
    TotpScreen(name = "N/A", revocation = "", onCleared = {}, onLongClick = {})
}
