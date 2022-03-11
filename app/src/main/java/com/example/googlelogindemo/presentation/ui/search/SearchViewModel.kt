package com.example.googlelogindemo.presentation.ui.search

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.googlelogindemo.network.models.Carmodel
import com.example.googlelogindemo.presentation.PostFilter
import com.example.googlelogindemo.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    val s = (1..10)
        .map { allowedChars.random() }
        .joinToString("")

    val isLoading = mutableStateOf(false)
    val showErrorHeader = mutableStateOf(false)

    val makeDialog = mutableStateOf(false)
    val modelDialog = mutableStateOf(false)
    val colorDialog = mutableStateOf(false)
    val fueltypeDialog = mutableStateOf(false)
    val bodytypeDialog = mutableStateOf(false)
    val transmissionDialog = mutableStateOf(false)
    val locationDialog = mutableStateOf(false)

    //Search
    val search = mutableStateOf("")
    val ordering = mutableStateOf("")

    //Filters
    val make = mutableStateOf("")
    val model = mutableStateOf("")
    val color = mutableStateOf("")
    val fuel_type = mutableStateOf("")
    val body_type = mutableStateOf("")
    val location = mutableStateOf("")
    val mileage_gt = mutableStateOf<Int?>(null)
    val mileage_lt = mutableStateOf<Int?>(null)
    val price_gt = mutableStateOf<Int?>(null)
    val price_lt = mutableStateOf<Int?>(null)
    val displacement_lt = mutableStateOf<Int?>(null)
    val displacement_gt = mutableStateOf<Int?>(null)
    val type = mutableStateOf<Int?>(null)
    val transmission = mutableStateOf("")

    val modelList = mutableListOf<Carmodel>()

    //SEARCH
    fun setPostFilter(postFilter: PostFilter) {
        ordering.value = postFilter.ordering
        search.value = postFilter.search
        //Filters
        make.value = postFilter.make
        model.value = postFilter.model
        color.value = postFilter.color
        fuel_type.value = postFilter.fuel_type
        body_type.value = postFilter.body_type
        location.value = postFilter.location
        mileage_gt.value = postFilter.mileage_gt
        mileage_lt.value = postFilter.mileage_lt
        price_gt.value = postFilter.price_gt
        price_lt.value = postFilter.price_lt
        displacement_lt.value = postFilter.displacement_lt
        displacement_gt.value = postFilter.displacement_gt
        transmission.value = postFilter.transmission
        type.value = postFilter.type
    }

    fun getPostFilter(): PostFilter {
        return PostFilter(
            ordering = ordering.value,
            search = search.value,
            //Filters.value,
            make = make.value,
            model = model.value,
            color = color.value,
            fuel_type = fuel_type.value,
            body_type = body_type.value,
            location = location.value,
            mileage_gt = mileage_gt.value,
            mileage_lt = mileage_lt.value,
            price_gt = price_gt.value,
            price_lt = price_lt.value,
            displacement_lt = displacement_lt.value,
            displacement_gt = displacement_gt.value,
            transmission = transmission.value,
            type = type.value
        )
    }

    fun clearPostFilter() {
        ordering.value = ""
        search.value = ""
        //Filters
        make.value = ""
        model.value = ""
        color.value = ""
        fuel_type.value = ""
        body_type.value = ""
        location.value = ""
        mileage_gt.value = null
        mileage_lt.value = null
        price_gt.value = null
        price_lt.value = null
        displacement_lt.value = null
        displacement_gt.value = null
        transmission.value = ""
        //type.value = null
    }

    private val searchExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        showErrorHeader.value = true
        isLoading.value = false
    }

    fun loadModels(make: String) {
        viewModelScope.launch(Dispatchers.IO + searchExceptionHandler) {
            isLoading.value = true
            delay(1000)
            val response = repository.getModels(make = make)
            if (response.isSuccessful) {
                response.body()?.let {
                    modelList.addAll(it)
                }
            } else {
                showErrorHeader.value = true
            }
            isLoading.value = false
        }
    }
}