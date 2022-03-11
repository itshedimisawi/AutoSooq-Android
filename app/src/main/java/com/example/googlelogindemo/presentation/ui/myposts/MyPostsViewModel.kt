package com.example.googlelogindemo.presentation.ui.myposts

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.googlelogindemo.network.models.Post
import com.example.googlelogindemo.presentation.ui.home.PAGE_SIZE
import com.example.googlelogindemo.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPostsViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    val s = (1..10)
        .map { allowedChars.random() }
        .joinToString("")

    val posts = mutableStateListOf<Post>()
    val showErrorHeader = mutableStateOf(false)

    val page = mutableStateOf(1)

    private var scrollPosition = 0

    val isLoading = mutableStateOf(false)

    val deleteDialog = mutableStateOf(false)
    val deleteSelectedIndex = mutableStateOf(0)
    val deleteSelectedPost = mutableStateOf<Post?>(null)

    init {
        // in init Dispatchers.IO is null we need try catch block instead of postExceptionHandler
        viewModelScope.launch {
            isLoading.value = true
            delay(2000)

            try {
                val response = repository.getMyPosts()

                if (response.isSuccessful) {
                    response.body()?.let {
                        posts.addAll(it)
                    }
                } else {
                    showErrorHeader.value = true
                }

            } catch (e: Exception) {

                showErrorHeader.value = true
            }

            isLoading.value = false
        }
    }

    //Reload
    fun reload() {
        viewModelScope.launch(Dispatchers.IO + postExceptionHandler) {
            isLoading.value = true
            delay(2000)
            page.value = 1

            val response = repository.getMyPosts()
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

        showErrorHeader.value = true
        isLoading.value = false
    }

    //PAGINATION
    fun nextPage() {
        viewModelScope.launch(Dispatchers.IO + postExceptionHandler) {

            if ((scrollPosition + 1) >= (page.value * PAGE_SIZE)) {
                incrementPage()
                if (page.value > 1) {
                    delay(1000)
                    val response = repository.getMyPosts()
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

    fun deletePost() {
        viewModelScope.launch(Dispatchers.IO + postExceptionHandler) {
            deleteSelectedPost.value?.let {
                val response = repository.deletePost(postId = it.id)
                if (response.isSuccessful) {
                    posts.removeAt(deleteSelectedIndex.value)
                } else {
                    //Todo: Error header
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