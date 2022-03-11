package com.example.googlelogindemo.presentation.ui.post

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.googlelogindemo.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    val s = (1..10)
        .map { allowedChars.random() }
        .joinToString("")

    val isAuthenticated = mutableStateOf(!repository.token.isNullOrBlank())

    var isLoading = mutableStateOf(false)
    var isFavoriteLoading = mutableStateOf(false)
    var isFavorite = mutableStateOf(false)

    var reportDialog = mutableStateOf(false)

    var previewImage = mutableStateOf(false)
    var previewImagePosition = mutableStateOf(0)

    fun addToFavorites(postId: Int) {
        viewModelScope.launch(Dispatchers.IO + postExceptionHandler) {
            isFavoriteLoading.value = true
            delay(1000)
            val response = repository.addToFavorite(postId)
            if (response.isSuccessful) {
                isFavorite.value = true
            } else {
                //todo: error network
            }
            isFavoriteLoading.value = false
        }
    }

    fun reportPost(postId: Int) {
        viewModelScope.launch(Dispatchers.IO + postExceptionHandler) {
            isLoading.value = true
            delay(1000)
            val response = repository.reportPost(postId = postId)
            if (response.isSuccessful) {
                //success
            } else {
                //error
            }
            isLoading.value = false
        }
    }

    private val postExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        isFavoriteLoading.value = false
        isLoading.value = false
    }


}