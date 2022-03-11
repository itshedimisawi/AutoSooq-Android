package com.example.googlelogindemo.presentation.ui.search.savedsearch

import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.googlelogindemo.presentation.PostFilter
import com.example.googlelogindemo.repository.Repository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class SavedSearchViewModel @Inject constructor(
    private val repository: Repository,
    @Named("preferences_inject") private val sharedPreferences: SharedPreferences
) : ViewModel() {

    val savedSearches = mutableStateListOf<String>()
    val deletedItem = mutableStateOf<Pair<Int,String>?>(null)
    val showSnackbar = mutableStateOf(false)
    init {
        getSavedSearches()
    }

    private fun getSavedSearches(){
        val gson = Gson()

        //get saved filter from sharedpreferences as Arraylist
        val listPostFilter = sharedPreferences.getString("SAVED_SEARCH", "[]")
        val listObjects : ArrayList<String> = gson.fromJson<ArrayList<String>>(listPostFilter,ArrayList::class.java)
        savedSearches.addAll(listObjects)
    }

    fun toPostFilter(json: String):PostFilter{
        val gson = Gson()
        return gson.fromJson(json,PostFilter::class.java)
    }

    fun clearSavedSearches(){
        savedSearches.clear()
        saveSavedSearches()
    }
    /*
    The user press delete the removeSavedSearch removes the item but doesnt save to sp
    if the user press undo saveSavedSearches is called and deletedItem is set to null
        snackbar checks if deletedItem is null if so it doesnt call saveSavedSearches again
    if the user DOESNT press undo:
        snackbar checks if deletedItem is null if not it calls saveSavedSearches
     */
    fun removeSavedSearch(index:Int){
        deletedItem.value = Pair(index, savedSearches[index])
        savedSearches.removeAt(index = index)
        showSnackbar.value = true //user decide to undo or not
    }

    fun saveSavedSearches(){
        val gson = Gson()
        val stringListObject : String = gson.toJson(savedSearches)
        sharedPreferences.edit().putString("SAVED_SEARCH", stringListObject).apply()
    }

    fun undoDelete(){
        deletedItem.value?.let{
            savedSearches.add(it.first,it.second)
        }
        deletedItem.value = null //if the snackbar finds out that deleteditem is null then it wont save
    }


}