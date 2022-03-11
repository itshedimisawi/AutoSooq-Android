package com.example.googlelogindemo.presentation.ui.authentication

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.googlelogindemo.R
import com.example.googlelogindemo.presentation.components.*
import com.example.googlelogindemo.presentation.theme.AppTheme
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowLeft
import compose.icons.feathericons.X
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: AuthViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        val context = this
        setContent {
            AppTheme {
                val scaffoldState = rememberScaffoldState()

                if (viewModel.showSnackbar.value) {
                    val snackBarMessage = (
                                when(viewModel.messageSnackbar.value){
                                    1 -> getString(R.string.invalid_email_password)
                                    2 -> getString(R.string.email_already_used)
                                    3 -> getString(R.string.error_google_login)
                                    else -> getString(R.string.error_connection)
                                }
                            )
                    LaunchedEffect(viewModel.showSnackbar.value) {
                        try {
                            when (scaffoldState.snackbarHostState.showSnackbar(
                                message = snackBarMessage,
                                actionLabel = getString(R.string.dismiss)
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
                }){
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (!viewModel.isLoggedIn.value) {
                            var showDialog = viewModel.isLoading.value

                            if (showDialog) {
                                progressDialog()
                            }

                            val scrollState = rememberScrollState()
                            Column(
                                Modifier
                                    .fillMaxSize()
                                    .verticalScroll(scrollState)
                            ) {

                                Box(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxSize()
                                ) {

                                    Column(modifier = Modifier.fillMaxSize()) {
                                        Box(modifier = Modifier.fillMaxWidth()) {
                                            Icon(
                                                imageVector = FeatherIcons.X,
                                                contentDescription = getString(R.string.close),
                                                tint = MaterialTheme.colors.onBackground.copy(
                                                    ContentAlpha.medium
                                                ),
                                                modifier = Modifier
                                                    .align(Alignment.TopStart)
                                                    .size(48.dp)
                                                    .clickable {
                                                        finish()
                                                    }
                                                    .padding(10.dp)
                                            )
                                        }

                                        LoginHeader(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 16.dp),
                                            onNoticeClick = {

                                            }
                                        )
                                        UserAuth(
                                            viewModel = viewModel,
                                            modifier = Modifier.padding(top = 8.dp)
                                        )
                                        Divider(modifier = Modifier.padding(20.dp))
                                        GoogleLogin(
                                            viewModel,
                                            modifier = Modifier
                                        )

                                    }
                                }
                            }

                            DefaultSnackbar(snackbarHostState = scaffoldState.snackbarHostState, onDismiss = {
                                scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                            },
                            modifier = Modifier.align(Alignment.BottomCenter))
                        } else {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.TopCenter)
                                ) {
                                    Icon(
                                        imageVector = FeatherIcons.ArrowLeft,
                                        contentDescription = stringResource(R.string.go_back),
                                        tint = MaterialTheme.colors.onBackground.copy(
                                            ContentAlpha.medium
                                        ),
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .size(48.dp)
                                            .clickable {
                                                viewModel.isLoggedIn.value = false
                                            }
                                            .padding(13.dp)
                                    )
                                }
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .align(Alignment.Center)
                                        .padding(16.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                                        contentDescription = stringResource(R.string.auth_welcome),
                                        tint = Color.Unspecified,
                                        modifier = Modifier
                                            .size(80.dp)
                                            .padding(bottom = 16.dp)
                                    )
                                    Text(
                                        text = getString(R.string.auth_welcome),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.h4,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 16.dp)
                                    )
                                    Text(
                                        text = getString(R.string.auth_description),
                                        style = MaterialTheme.typography.body2,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colors.onBackground.copy(
                                            ContentAlpha.medium
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                Button(modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .height(50.dp),
                                    onClick = {
                                        viewModel.saveToken()
                                        context.setResult(RESULT_OK)
                                        finish()
                                    }) {
                                    Text(
                                        text = stringResource(R.string.button_continue),
                                        style = MaterialTheme.typography.button
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


