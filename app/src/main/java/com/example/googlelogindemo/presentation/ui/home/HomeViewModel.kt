package com.example.googlelogindemo.presentation.ui.home

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.googlelogindemo.network.models.Post
import com.example.googlelogindemo.presentation.PostFilter
import com.example.googlelogindemo.repository.Repository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Named

const val PAGE_SIZE = 30

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Repository,
    @Named("preferences_inject") private val sharedPreferences: SharedPreferences
) : ViewModel() {

    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    val s = (1..10)
        .map { allowedChars.random() }
        .joinToString("")

    val posts: MutableState<List<Post>> = mutableStateOf(listOf())
    val filterDialog = mutableStateOf(false)
    val showErrorHeader = mutableStateOf(false)

    val page = mutableStateOf(1)

    val postFilter = mutableStateOf(PostFilter())
    val isSearch = mutableStateOf(false)
    val isCurrentSearchSaved = mutableStateOf(false)
    private var scrollPosition = 0

    val isLoading = mutableStateOf(false)

    init {
        // in init Dispatchers.IO is null we need try catch block instead of postExceptionHandler
        viewModelScope.launch {
            isLoading.value = true
            delay(2000)

            try {
                val response = repository.getPosts()

                if (response.isSuccessful) {
                    response.body()?.let {
                        posts.value = it
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

    //SEARCH
    //backpress calls this with no args to clear search
    //if this is called with no parameters it will keep the same postFilter
    fun newSearch(newPostFilter: PostFilter? = null) {
        isCurrentSearchSaved.value = false
        newPostFilter?.let { //if not a backpress
            postFilter.value = newPostFilter

        }
        isSearch.value = postFilter.value.hashCode()!=PostFilter().hashCode() //if postFilter's field is changed then its a search

        viewModelScope.launch(Dispatchers.IO + postExceptionHandler) {

            isLoading.value = true
            delay(2000)
            page.value = 1

            val response = repository.getPosts(
                page = page.value,
                ordering = postFilter.value.ordering,  //price or -price
                search = postFilter.value.search,
                //Filters
                make = postFilter.value.make,
                model = postFilter.value.model,
                color = postFilter.value.color,
                fuel_type = postFilter.value.fuel_type,
                body_type = postFilter.value.body_type,
                location = postFilter.value.location,
                mileage_gt = postFilter.value.mileage_gt,
                mileage_lt = postFilter.value.mileage_lt,
                price_gt = postFilter.value.price_gt,
                price_lt = postFilter.value.price_lt,
                displacement_gt = postFilter.value.displacement_gt,
                displacement_lt = postFilter.value.displacement_lt,
                transmission = postFilter.value.transmission,
                type = postFilter.value.type,
            )
            if (response.isSuccessful) {
                response.body()?.let {
                    posts.value = it
                }
            } else {
                showErrorHeader.value = true
            }
            isLoading.value = false
        }
    }

    fun clearSearch() {
        postFilter.value = PostFilter()
        isSearch.value = false
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
                    val response = repository.getPosts(
                        page = page.value,
                        ordering = postFilter.value.ordering,  //price or -price
                        search = postFilter.value.search,
                        //Filters
                        make = postFilter.value.make,
                        model = postFilter.value.model,
                        color = postFilter.value.color,
                        fuel_type = postFilter.value.fuel_type,
                        body_type = postFilter.value.body_type,
                        location = postFilter.value.location,
                        mileage_gt = postFilter.value.mileage_gt,
                        mileage_lt = postFilter.value.mileage_lt,
                        price_gt = postFilter.value.price_gt,
                        price_lt = postFilter.value.price_lt,
                        displacement_gt = postFilter.value.displacement_gt,
                        displacement_lt = postFilter.value.displacement_lt,
                        transmission = postFilter.value.transmission,
                        type = postFilter.value.type,
                    )
                    if (response.isSuccessful) {
                        response.body()?.let {
                            appendPosts(it)
                        }
                    } else {
                        showErrorHeader.value = true
                    }
                }
            }
        }
    }

    private fun appendPosts(posts: List<Post>) {
        val current = ArrayList(this.posts.value)
        current.addAll(posts)
        this.posts.value = current
    }

    private fun incrementPage() {
        page.value = page.value + 1
    }

    fun onChangeScrollPosition(position: Int) {
        scrollPosition = position
    }

    fun saveCurrentSearch(){
        isCurrentSearchSaved.value = true
        //convert current postfilter to json
        val gson = Gson()
        val jsonPostFilter = gson.toJson(postFilter.value)

        //get saved filter from sharedpreferences as Arraylist
        val listPostFilter = sharedPreferences.getString("SAVED_SEARCH", "[]")
        val listObjects : ArrayList<String> = gson.fromJson<ArrayList<String>>(listPostFilter,ArrayList::class.java)

        //add converted current postfilter to savced postfilters
        listObjects.add(0,jsonPostFilter)

        //save new list of postfilters
        val stringListObject = gson.toJson(listObjects)
        sharedPreferences.edit().putString("SAVED_SEARCH", stringListObject).apply()

    }
}