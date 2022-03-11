package com.example.googlelogindemo.presentation.ui.favorites

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.googlelogindemo.network.models.Favorite
import com.example.googlelogindemo.presentation.ui.home.PAGE_SIZE
import com.example.googlelogindemo.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    val s = (1..10)
        .map { allowedChars.random() }
        .joinToString("")

    val posts = mutableStateListOf<Favorite>()
    val error = mutableStateOf("hmm")

    val search = mutableStateOf("")
    val ordering = mutableStateOf("")
    val page = mutableStateOf(1)

    private var scrollPosition = 0
    val isLoading = mutableStateOf(false)

    val showErrorHeader = mutableStateOf(false)

    val deleteDialog = mutableStateOf(false)
    val deleteSelectedIndex = mutableStateOf(0)
    val deleteSelectedPost = mutableStateOf<Favorite?>(null)

    init {
        // in init Dispatchers.IO is null we need try catch block instead of postExceptionHandler
        viewModelScope.launch {
            isLoading.value = true
            delay(2000)
            try {
                val response = repository.getFavorites()
                if (response.isSuccessful) {
                    response.body()?.let {
                        posts.addAll(it)
                    }
                } else {
                    showErrorHeader.value = true
                }
                isLoading.value = false
            } catch (e: Exception) {
                error.value = e.message.toString()
                isLoading.value = false
            }
        }
    }

    fun deleteFavorite() {
        viewModelScope.launch(Dispatchers.IO + postExceptionHandler) {
            deleteSelectedPost.value?.let {
                val response = repository.deleteFavorite(postId = it.id)
                if (response.isSuccessful) {
                    posts.removeAt(deleteSelectedIndex.value)
                } else {
                    //Todo: Error header
                }
            }
        }
    }

    //SEARCH
    fun newSearch() {
        viewModelScope.launch(Dispatchers.IO + postExceptionHandler) {
            isLoading.value = true
            delay(1000)
            page.value = 1
            val response = repository.getFavorites()
            if (response.isSuccessful) {
                response.body()?.let {
                    posts.clear()
                    posts.addAll(it)
                }
            } else {
                showErrorHeader.value = true
            }
            isLoading.value = false
        }
    }

    private val postExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        isLoading.value = false
        showErrorHeader.value = true
    }

    //PAGINATION
    fun nextPage() {
        viewModelScope.launch {
            if ((scrollPosition + 1) >= (page.value * PAGE_SIZE)) {
                incrementPage()
                if (page.value > 1) {
                    val response = repository.getFavorites()
                    if (response.isSuccessful) {
                        response.body()?.let {
                            posts.addAll(it)
                        }
                    } else {
                        showErrorHeader.value = true
                    }
                }
            }
        }
    }

    private fun incrementPage() {
        page.value = page.value + 1
    }

    fun onChangeScrollPosition(position: Int) {
        scrollPosition = position
    }
}