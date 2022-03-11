package com.example.googlelogindemo.presentation.ui.sell

import android.Manifest
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.googlelogindemo.R
import com.example.googlelogindemo.corestuff.isPermanentlyDenied
import com.example.googlelogindemo.presentation.Screens
import com.example.googlelogindemo.presentation.components.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import compose.icons.FeatherIcons
import compose.icons.feathericons.ShieldOff
import compose.icons.feathericons.Trash2

@ExperimentalPermissionsApi
@Composable
fun SellScreen(navController: NavController, viewModel: SellViewModel) {

    Log.i("messi", viewModel.toString() + " : " + viewModel.s)

    BackHandler(enabled = true) {
        navController.navigate(Screens.HomeScreen.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )
    )
    val lifecycleOwner = LocalLifecycleOwner.current

    val focusManager = LocalFocusManager.current
    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current
    if (viewModel.showSnackbar.value) {
        val snackBarMessage = (
                when (viewModel.snackBarMessage.value) {
                    1 -> stringResource(id = R.string.post_added)
                    else -> stringResource(id = R.string.error_connection)
                }
                )
        LaunchedEffect(viewModel.showSnackbar.value) {
            try {
                when (scaffoldState.snackbarHostState.showSnackbar(
                    message = snackBarMessage,
                    actionLabel = context.getString(R.string.dismiss)
                )) {
                    SnackbarResult.Dismissed -> {
                    }
                }
            } finally {
                viewModel.showSnackbar.value = false
            }
        }
    }

    Scaffold(scaffoldState = scaffoldState,
        snackbarHost = {
            scaffoldState.snackbarHostState
        },
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.background,
                title = {
                    Icon(
                        painterResource(id = R.mipmap.ic_launcher_foreground),
                        contentDescription = stringResource(R.string.nav_sellcar),
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(35.dp)
                            .padding(end = 10.dp)
                    )
                    Text(text = stringResource(R.string.add_post))
                },
                actions = {
                    IconButton(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.clearForm()
                        } //do something
                    ) {
                        Icon(FeatherIcons.Trash2, null)
                    }
                }
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (viewModel.isAuthenticated.value) {
                //permission request
                DisposableEffect(
                    key1 = lifecycleOwner,
                    effect = {
                        val observer = LifecycleEventObserver { _, event ->
                            if (event == Lifecycle.Event.ON_START) {
                                permissionsState.launchMultiplePermissionRequest()
                            }
                        }
                        lifecycleOwner.lifecycle.addObserver(observer)

                        onDispose {
                            lifecycleOwner.lifecycle.removeObserver(observer)
                        }
                    }
                )
                permissionsState.permissions.forEach { perm ->
                    when (perm.permission) {
                        Manifest.permission.READ_EXTERNAL_STORAGE -> {
                            when {
                                perm.hasPermission -> {
                                    SellScreenForm(
                                        viewModel = viewModel,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                perm.shouldShowRationale -> {
                                    EmptyStatePlaceholder(
                                        message = stringResource(R.string.permission_needed),
                                        actionMessage = stringResource(R.string.request_permission),
                                        actionIcon = FeatherIcons.ShieldOff
                                    ) {
                                        permissionsState.launchMultiplePermissionRequest()
                                    }
                                }
                                perm.isPermanentlyDenied() -> {
                                    EmptyStatePlaceholder(
                                        message = stringResource(R.string.permission_denied),
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                EmptyStatePlaceholder(
                    message = stringResource(R.string.login_to_post),
                )
            }

            DefaultSnackbar(snackbarHostState = scaffoldState.snackbarHostState, onDismiss = {
                scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
            },
                modifier = Modifier.align(Alignment.BottomCenter)
                    .padding(bottom = 46.dp))
        }
    }
}

@Composable
fun SellScreenForm(viewModel: SellViewModel, modifier: Modifier) {
    val context = LocalContext.current
    if (viewModel.isLoading.value) {
        progressDialog()
    }
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    Box(modifier = modifier) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {

            SellAddImages(viewModel = viewModel)
            SellForm(viewModel = viewModel)
        }
        Button(
            onClick = {
                focusManager.clearFocus()
                viewModel.confirmPost(context = context)
            },
            modifier = Modifier
                .padding(bottom = 46.dp)
                .height(100.dp)
                .fillMaxWidth()
                .padding(26.dp)
                .align(Alignment.BottomCenter)
        ) {
            Text(
                text = stringResource(R.string.button_continue),
                style = MaterialTheme.typography.button
            )
        }
    }
}