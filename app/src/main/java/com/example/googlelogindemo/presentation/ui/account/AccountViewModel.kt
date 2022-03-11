package com.example.googlelogindemo.presentation.ui.account

import android.content.Context
import android.content.SharedPreferences
import android.util.Patterns
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.googlelogindemo.corestuff.*
import com.example.googlelogindemo.network.models.User
import com.example.googlelogindemo.repository.Repository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val repository: Repository,
    @Named("preferences_inject") private val sharedPreferences: SharedPreferences
) : ViewModel() {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    val s = (1..10)
        .map { allowedChars.random() }
        .joinToString("")

    val isAuthenticated = mutableStateOf(!repository.token.isNullOrBlank())

    val showSnackbar = mutableStateOf(false)
    val messageSnackbar = mutableStateOf(0)

    val user = mutableStateOf(User())

    val showErrorHeader = mutableStateOf(false)

    val isLoading = mutableStateOf(false)
    val isUpdating = mutableStateOf(false)

    //changeEmail
    val currentPassword = mutableStateOf("")
    val newEmail = mutableStateOf("")

    //changePassword
    val oldPassword = mutableStateOf("")
    val newPassword = mutableStateOf("")
    val newPasswordConfirmation = mutableStateOf("")

    val firstName = mutableStateOf("")
    val lastName = mutableStateOf("")
//    val phone = mutableStateOf("")


    val token = mutableStateOf(repository.token)

    //changeEmail
    val errorNewEmail = mutableStateOf(false)
    val errorCurrentPassword = mutableStateOf(false)

    //changePassword
    val errorOldPassword = mutableStateOf(false)
    val errorNewPassword = mutableStateOf(false)
    val errorNewPasswordConfirmation = mutableStateOf(false)

    //updateProfile
    val errorFirstName = mutableStateOf(false)
    val errorLastName = mutableStateOf(false)
//    val errorPhone = mutableStateOf(false)


    init {
        //No IO dispatcher
        viewModelScope.launch {
            try {
                isAuthenticated.value = !repository.token.isNullOrBlank() //recalculate
                if (isAuthenticated.value) {
                    isLoading.value = true
                    delay(2000)
                    val response = repository.getProfile()
                    if (response.isSuccessful) {
                        response.body()?.let {
                            user.value = it
                            setEditValues(it)
                        }
                    } else {
                        showErrorHeader.value = true
                    }
                }

            } catch (e: Exception) {
                showErrorHeader.value = true
            }
            isLoading.value = false
        }

    }

    fun getProfile() {
        viewModelScope.launch(Dispatchers.IO + accountExceptionHandler) {
            isLoading.value = true
            delay(2000)
            isAuthenticated.value = !repository.token.isNullOrBlank() //recalculate
            if (isAuthenticated.value) {
                val response = repository.getProfile()
                if (response.isSuccessful) {
                    response.body()?.let {
                        user.value = it
                        setEditValues(it)
                    }
                } else {
                    showErrorHeader.value = true
                }
            }
            isLoading.value = false
        }
    }

    fun logout(context: Context) {
        viewModelScope.launch(Dispatchers.IO + accountExceptionHandler) {
            isLoading.value = true
            delay(2000)
            val response = repository.logout() //204 No content
            removeToken()
            isLoading.value = false
            val gso = GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build()
            val mGoogleSignInClient = GoogleSignIn.getClient(context, gso)
            mGoogleSignInClient.signOut()
        }
    }

    private fun setEditValues(user: User) {
        currentPassword.value = ""
        newEmail.value = ""
        oldPassword.value = ""
        newPassword.value = ""
        newPasswordConfirmation.value = ""
        firstName.value = user.firstName
        lastName.value = user.lastName
//        phone.value = user.phone
    }


    private val accountExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        isLoading.value = false
        isUpdating.value = false
        showErrorHeader.value = true
    }

    private val editExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        isLoading.value = false
        isUpdating.value = false
        messageSnackbar.value = CODE_ERROR_CONNECTION
        showSnackbar.value = true
    }

    private fun removeToken() {
        sharedPreferences.edit().remove("TOKEN").apply()
        repository.token = null
        isAuthenticated.value = !repository.token.isNullOrBlank()
    }

    fun changePassword() {
        if (oldPassword.value.isBlank()) {
            errorOldPassword.value = true
        } else if (newPassword.value.isBlank() || !newPassword.value.isValidPassword()) {
            errorNewPassword.value = true
        } else if (newPassword.value != newPasswordConfirmation.value) {
            errorNewPasswordConfirmation.value = true
        } else {
            viewModelScope.launch(Dispatchers.IO + editExceptionHandler) {
                isUpdating.value = true
                delay(2000)
                val response = repository.changePassword(
                    password = oldPassword.value,
                    newPassword = newPassword.value
                )
                if (response.isSuccessful) {
                    setEditValues(user.value)
                    messageSnackbar.value = CODE_SUCCESS
                    showSnackbar.value = true
                } else {
                    if (response.code() == 400) {
                        errorCurrentPassword.value = true
                        oldPassword.value = ""
                        newPassword.value = ""
                    } else {
                        messageSnackbar.value = CODE_ERROR_CONNECTION
                        showSnackbar.value = true
                    }
                }
                isUpdating.value = false
            }
        }
    }

    fun changeEmail() {
        if (!newEmail.value.isValidEmail()) {
            errorNewEmail.value = true
        } else if (currentPassword.value.isBlank()) {
            errorCurrentPassword.value = true
        } else {
            viewModelScope.launch(Dispatchers.IO + editExceptionHandler) {
                isUpdating.value = true
                delay(2000)
                val response = repository.changeEmail(
                    password = currentPassword.value,
                    newEmail = newEmail.value
                )
                if (response.isSuccessful) {
                    user.value.email = newEmail.value
                    setEditValues(user.value)
                    messageSnackbar.value = CODE_SUCCESS
                    showSnackbar.value = true
                } else {
                    if (response.code() == 400) {
                        errorNewEmail.value = true
                    } else {
                        messageSnackbar.value = CODE_ERROR_CONNECTION
                        showSnackbar.value = true
                    }
                }
                isUpdating.value = false

            }
        }
    }

    fun changeInfo() {
        when {
            firstName.value.isBlank() -> {
                errorFirstName.value = true
            }
            lastName.value.isBlank() -> {
                errorLastName.value = true
            }
//            phone.value.isBlank() -> {
//                errorPhone.value = true
//            }
            else -> {
                viewModelScope.launch(Dispatchers.IO + editExceptionHandler) {
                    isUpdating.value = true
                    delay(2000)
                    val response = repository.changeInfos(
                        firstName = firstName.value,
                        lastName = lastName.value
                    )
                    if (response.isSuccessful) {
                        setEditValues(user.value)
                        messageSnackbar.value = CODE_SUCCESS
                        showSnackbar.value = true
                    } else {
                        if (response.code() == 400) {
                            messageSnackbar.value = CODE_UNKNOWN_ERROR
                        } else {
                            messageSnackbar.value = CODE_ERROR_CONNECTION
                            showSnackbar.value = true
                        }
                    }
                    isUpdating.value = false
                }
            }
        }
    }
}