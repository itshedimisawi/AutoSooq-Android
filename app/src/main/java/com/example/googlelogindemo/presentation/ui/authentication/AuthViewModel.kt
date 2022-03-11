package com.example.googlelogindemo.presentation.ui.authentication

import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.googlelogindemo.corestuff.isValidEmail
import com.example.googlelogindemo.corestuff.isValidPassword
import com.example.googlelogindemo.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: Repository,
    //@Named("token_inject") private val token_inject: String?,
    @Named("preferences_inject") private val sharedPreferences: SharedPreferences
) : ViewModel() {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    val s = (1..10)
        .map { allowedChars.random() }
        .joinToString("")

    val email = mutableStateOf("")
    val password = mutableStateOf("")
    val passwordConfirmation = mutableStateOf("")
    val firstName = mutableStateOf("")
    val lastName = mutableStateOf("")
    val phone = mutableStateOf("")

    val showSnackbar = mutableStateOf(false)
    val messageSnackbar = mutableStateOf(0)

    val token = mutableStateOf(repository.token)
    val errorEmail = mutableStateOf(false)
    val errorPassword = mutableStateOf(false)
    val errorPasswordConfirmation = mutableStateOf(false)
    val errorFirstName = mutableStateOf(false)
    val errorLastName = mutableStateOf(false)
    val errorPhone = mutableStateOf(false)

    val isLoading = mutableStateOf(false)
    val isSignup = mutableStateOf(false)

    val isLoggedIn = mutableStateOf(false)

    fun googleAuth(google_token: String) {
        viewModelScope.launch(Dispatchers.IO + authExceptionHandler) {
            isLoading.value = true
            delay(2000)
            val response = repository.google_login(google_token = google_token)

            if (response.isSuccessful) {
                response.body()?.let {
                    token.value = it.authToken
                    isLoggedIn.value = true
                }
            } else {
                messageSnackbar.value = 0
                showSnackbar.value = true
            }
            isLoading.value = false
        }
    }



    fun login(email: String, password: String) {
        if (!email.isValidEmail()) {
            errorEmail.value = true
        } else if (password.isBlank()) {
            errorPassword.value = true
        } else {
            viewModelScope.launch(Dispatchers.IO + authExceptionHandler) {
                isLoading.value = true
                delay(2000)
                val login = repository.login(email = email, password = password)
                if (login.isSuccessful) {
                    login.body()?.let {
                        token.value = it.authToken
                        isLoggedIn.value = true
                    }
                } else {
                    if (login.code() == 400) {
                        messageSnackbar.value = 1
                    } else {
                        messageSnackbar.value = 0
                    }
                    showSnackbar.value = true
                }
                isLoading.value = false
            }
        }

    }

    fun signup(
        email: String,
        password: String,
        passwordConfirmation: String,
        firstName: String,
        lastName: String,
//        phone: String
    ) {
        if (!email.isValidEmail()) {
            errorEmail.value = true
        } else if (password.isBlank() || !password.isValidPassword()) {
            errorPassword.value = true
        } else if (passwordConfirmation != password) {
            errorPasswordConfirmation.value = true
        } else if (firstName.isBlank()) {
            errorFirstName.value = true
        } else if (lastName.isBlank()) {
            errorLastName.value = true
//        }else if(phone.isBlank() || !phone.isDigitsOnly()){
//            errorPhone.value = true
        } else {
            viewModelScope.launch(Dispatchers.IO + authExceptionHandler) {
                isLoading.value = true
                delay(2000)
                val login = repository.signup(
                    email = email,
                    password = password,
                    firstName = firstName,
                    lastName = lastName,
                )
                if (login.isSuccessful) {
                    login.body()?.let {
                        token.value = it.authToken
                        isLoggedIn.value = true
                    }
                } else {
                    if (login.code() == 409) {
                        messageSnackbar.value = 2
                    } else {
                        messageSnackbar.value = 0
                    }
                    showSnackbar.value = true
                }
                isLoading.value = false
            }
        }

    }

    private val authExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        token.value = throwable.message.toString()
        errorEmail.value = true
        errorPassword.value = true

        isLoading.value = false

        messageSnackbar.value = 0
        showSnackbar.value = true
    }

    fun saveToken() {
        token.value?.let {
            repository.token = it
            sharedPreferences.edit().putString("TOKEN", it).apply()
        }
    }

}