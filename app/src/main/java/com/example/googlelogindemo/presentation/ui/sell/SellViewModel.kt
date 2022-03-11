package com.example.googlelogindemo.presentation.ui.sell

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.googlelogindemo.corestuff.getRealPathFromUri
import com.example.googlelogindemo.network.models.Carmodel
import com.example.googlelogindemo.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@HiltViewModel
class SellViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    val s = (1..10)
        .map { allowedChars.random() }
        .joinToString("")


    val isAuthenticated = mutableStateOf(!repository.token.isNullOrBlank())

    val isLoading = mutableStateOf(false)

    val showSnackbar = mutableStateOf(false)
    val snackBarMessage = mutableStateOf(0)

    val makeDialog = mutableStateOf(false)
    val modelDialog = mutableStateOf(false)
    val colorDialog = mutableStateOf(false)
    val fueltypeDialog = mutableStateOf(false)
    val bodytypeDialog = mutableStateOf(false)
    val transmissionDialog = mutableStateOf(false)
    val locationDialog = mutableStateOf(false)
    val typeDialog = mutableStateOf(false)

    val make = mutableStateOf("")
    val model = mutableStateOf("")
    val modelId = mutableStateOf<Int?>(null)
    val color = mutableStateOf("")
    val fuel_type = mutableStateOf("")
    val location = mutableStateOf("")
    val mileage = mutableStateOf<Int?>(null)
    val price = mutableStateOf<Int?>(null)
    val displacement = mutableStateOf<Int?>(null)
    val type = mutableStateOf<Int?>(0)
    val transmission = mutableStateOf("")

    val phone = mutableStateOf("")
    val body_type = mutableStateOf("")

    val errorPhone = mutableStateOf(false)
    val errorPrice = mutableStateOf(false)
    val errorPhoto = mutableStateOf(false)
    val errorTitle = mutableStateOf(false)

    val title = mutableStateOf("")

    val description = mutableStateOf("")

    val images = mutableStateListOf<Uri>()

    val modelList = mutableListOf<Carmodel>()

    val mainPhoto = mutableStateOf(0)


    fun confirmPost(context: Context) {
        clearErrors()
        if (images.isEmpty()) {
            errorPhoto.value = true
        } else if (title.value.isBlank()) {
            errorTitle.value = true
        } else if (phone.value.isBlank() || !phone.value.isDigitsOnly()) {
            errorPhone.value = true
        } else if (price.value == null) {
            errorPrice.value = true
        } else {
            isLoading.value = true
            val imagesData = ArrayList<File>()
            for (i in images) {
                imagesData.add(File(getRealPathFromUri(i, context).toString()))
            }
            val aux = imagesData[0]
            imagesData[0] = imagesData[mainPhoto.value]
            imagesData[mainPhoto.value] = aux

            viewModelScope.launch (Dispatchers.IO + sellExceptionHandler){
                delay(1000)
                val response = repository.addPost(
                    title = title.value,
                    description = description.value,
                    model = modelId.value,
                    color = color.value,
                    fuel_type = fuel_type.value,
                    body_type = body_type.value,
                    location = location.value,
                    mileage = mileage.value,
                    price = price.value,
                    displacement = displacement.value,
                    transmission = transmission.value,
                    type = type.value,
                    images = imagesData,
                    phone = phone.value
                )
                if (response.isSuccessful) {
                    response.body()?.let {
                        snackBarMessage.value = 1
                        showSnackbar.value = true
                        clearForm()
                        images.clear()
                    }
                } else {
                    Log.i("debuggz", response.errorBody()!!.string())
                }
                isLoading.value = false
            }
        }
    }

    private fun clearErrors() {
        errorPhone.value = false
        errorPrice.value = false
        errorPhoto.value = false
        errorTitle.value = false
    }

    fun loadModels(make: String) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO + sellExceptionHandler) {
            delay(1000)
            val response = repository.getModels(make = make)
            if (response.isSuccessful) {
                response.body()?.let {
                    modelList.addAll(it)
                }
            } else {
                snackBarMessage.value = 0
                showSnackbar.value = true
            }
            isLoading.value = false
        }
    }

    private val sellExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        isLoading.value = false
        snackBarMessage.value = 0
        showSnackbar.value = true
    }

    fun clearForm() {
        make.value = ""
        model.value = ""
        modelId.value = null
        color.value = ""
        fuel_type.value = ""
        location.value = ""
        mileage.value = null
        price.value = null
        displacement.value = null
        transmission.value = ""

        phone.value = ""
        body_type.value = ""

        errorPhone.value = false
        errorPrice.value = false
        errorPhoto.value = false
        errorTitle.value = false

        title.value = ""

        description.value = ""

        modelList.clear()
    }


}
