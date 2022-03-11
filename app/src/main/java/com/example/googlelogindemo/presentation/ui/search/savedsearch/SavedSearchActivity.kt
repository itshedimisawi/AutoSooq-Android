package com.example.googlelogindemo.presentation.ui.search.savedsearch

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.googlelogindemo.R
import com.example.googlelogindemo.presentation.components.DefaultSnackbar
import com.example.googlelogindemo.presentation.components.SavedSearchItem
import com.example.googlelogindemo.presentation.theme.AppTheme
import compose.icons.FeatherIcons
import compose.icons.feathericons.Trash2
import compose.icons.feathericons.X
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavedSearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: SavedSearchViewModel =
            ViewModelProvider(this).get(SavedSearchViewModel::class.java)
        setContent {

            AppTheme {
                val scaffoldState = rememberScaffoldState()
                if (viewModel.showSnackbar.value) {
                    val snackBarMessage = "Saved search removed"
                    LaunchedEffect(viewModel.showSnackbar.value) {
                        try {
                            when (scaffoldState.snackbarHostState.showSnackbar(
                                message = snackBarMessage,
                                actionLabel = getString(R.string.undo)
                            )) {
                                SnackbarResult.Dismissed -> {
                                }
                            }
                        } finally {
                            viewModel.showSnackbar.value = false
                            if (viewModel.deletedItem.value!=null){ //see comment in SavedSearchViewModel
                                viewModel.saveSavedSearches()
                                Log.i("archivesearch", "saved searches")
                            }
                        }
                    }
                }
                Scaffold(topBar = {
                    TopAppBar(
                        backgroundColor = MaterialTheme.colors.background,
                        title = {
                            Text(text = getString(R.string.saved_searches))
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    finish()
                                } //go back
                            ) {
                                Icon(FeatherIcons.X, null)
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = {
                                    viewModel.clearSavedSearches()
                                } //do something
                            ) {
                                Icon(FeatherIcons.Trash2, null)
                            }
                        }
                    )
                },
                    scaffoldState = scaffoldState,
                    snackbarHost = {
                        scaffoldState.snackbarHostState
                    }) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        LazyColumn(){
                            itemsIndexed(viewModel.savedSearches) { index, item ->
                                Column(modifier = Modifier
                                    .fillMaxWidth()) {
                                    SavedSearchItem(viewModel.toPostFilter(item), modifier = Modifier.fillMaxWidth(),
                                        onClick = {
                                            val resultIntent: Intent = Intent()
                                            resultIntent.putExtra(
                                                "archivepostFilter",
                                                viewModel.toPostFilter(item)
                                            )
                                            setResult(RESULT_OK, resultIntent)
                                            finish()
                                        },
                                    onDelete = {
                                        viewModel.removeSavedSearch(index = index)
                                    })
                                    Divider(thickness = 1.dp)
                                }
                            }
                        }
                        DefaultSnackbar(
                            snackbarHostState = scaffoldState.snackbarHostState, onDismiss = {
                                scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                                Log.i("archivesearch", "undo")
                                viewModel.undoDelete()
                            },
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }
                }
            }
        }
    }
}