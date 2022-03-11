package com.example.googlelogindemo.presentation.ui.account

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.googlelogindemo.R
import com.example.googlelogindemo.corestuff.CODE_SUCCESS
import com.example.googlelogindemo.corestuff.CODE_UNKNOWN_ERROR
import com.example.googlelogindemo.presentation.Screens
import com.example.googlelogindemo.presentation.components.*
import com.example.googlelogindemo.presentation.ui.authentication.AuthActivity
import compose.icons.FeatherIcons
import compose.icons.feathericons.LogOut
import compose.icons.feathericons.RefreshCw

@Composable
fun AccountScreen(navController: NavController, viewModel: AccountViewModel) {
/*
    Logout goes here if user press logout we call the endpoint then clear token from sp and recalculate isAuthenticated
    if any other endpoint gets 401 then it clears the token and delete this screen from bs
 */

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

    val context = LocalContext.current

    val loginIntent =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                //viewModel.getProfile()
                navController.popBackStack(route = Screens.AccountScreen.route, inclusive = true)
            }
        }
    val scaffoldState = rememberScaffoldState()

    if (viewModel.showSnackbar.value) {
        val snackBarMessage = (
                when(viewModel.messageSnackbar.value){
                    CODE_SUCCESS -> "Profile updated successfully"
                    CODE_UNKNOWN_ERROR -> "Error while updating profile"
                    else -> stringResource(R.string.error_connection)
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

    Scaffold (scaffoldState = scaffoldState,
        snackbarHost = {
            scaffoldState.snackbarHostState
        },
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.background,
                title = {
                    Icon(
                        painterResource(id = R.mipmap.ic_launcher_foreground), contentDescription = stringResource(R.string.nav_account),
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(35.dp)
                            .padding(end = 10.dp)
                    )
                    Text(text = stringResource(R.string.nav_account))
                }
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (viewModel.isUpdating.value) {
                progressDialog()
            }
            if (viewModel.showErrorHeader.value) {
                Box {
                    EmptyStatePlaceholder(
                        message = stringResource(R.string.error_connection),
                        actionMessage = stringResource(R.string.try_again),
                        actionIcon = FeatherIcons.RefreshCw
                    ) {
                        viewModel.showErrorHeader.value = false
                        viewModel.getProfile()
                    }
                }
            } else {
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .padding(16.dp)
                ) {
                    //Authenticated
                    Log.i("placeholderstuff", "isLoading : ${viewModel.isLoading.value}")

                    if (viewModel.isLoading.value) {
                        AccountPlaceholder()
                    } else {
                        if (viewModel.isAuthenticated.value) {
                            AccountHeader(
                                user = viewModel.user.value,
                                modifier = Modifier.fillMaxWidth()
                            )
                            if (viewModel.user.value.googleAcc == null) { //Google users cant change email or set a passsword
                                AccountEmail(
                                    viewModel = viewModel, modifier = Modifier
                                        .padding(top = 16.dp)
                                        .fillMaxWidth()
                                )
                                AccountPassword(
                                    viewModel = viewModel, modifier = Modifier
                                        .padding(top = 16.dp)
                                        .fillMaxWidth()
                                )
                            }
                            AccountInfo(
                                viewModel = viewModel, modifier = Modifier
                                    .padding(top = 16.dp)
                                    .fillMaxWidth()
                            )
                            AccountLogout(title = stringResource(R.string.logout),
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .fillMaxWidth()
                                    .padding(bottom = 46.dp),
                                icon = {
                                    Icon(
                                        imageVector = FeatherIcons.LogOut,
                                        contentDescription = stringResource(R.string.logout),
                                        modifier = Modifier.size(22.dp)
                                    )
                                },
                                onClick = {
                                    viewModel.logout(context)
                                })

                            //Not authenticated
                        } else {
                            AccountNotLoggedIn(Modifier) {
                                val intent = Intent(context, AuthActivity::class.java)
                                loginIntent.launch(intent)
                            }
                        }
                    }
                }
            }
            DefaultSnackbar(snackbarHostState = scaffoldState.snackbarHostState, onDismiss = {
                scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
            },
                modifier = Modifier.align(Alignment.BottomCenter)
                    .padding(bottom = 46.dp))
        }
    }
}

