package com.example.googlelogindemo.presentation.ui.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.googlelogindemo.R
import com.example.googlelogindemo.corestuff.*
import com.example.googlelogindemo.network.models.Carmodel
import com.example.googlelogindemo.presentation.PostFilter
import com.example.googlelogindemo.presentation.components.*
import com.example.googlelogindemo.presentation.theme.AppTheme
import com.example.googlelogindemo.presentation.theme.ColoronButton
import com.example.googlelogindemo.presentation.ui.search.savedsearch.SavedSearchActivity
import com.example.googlelogindemo.presentation.ui.search.savedsearch.SavedSearchViewModel
import compose.icons.FeatherIcons
import compose.icons.feathericons.Archive
import compose.icons.feathericons.Trash2
import compose.icons.feathericons.X
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: SearchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)

        val bundle = intent.getBundleExtra("bundle")
        var postFilter = PostFilter()
        if (bundle != null) {
            Log.i("searchintent", "has bundle")
            bundle.getParcelable<PostFilter>("inpostFilter")?.let {
                Log.i("searchintent", "got postfilter")
                postFilter = it
            }
        }
        viewModel.setPostFilter(postFilter = postFilter)


        setContent {
            val context = LocalContext.current

            val savedSearchIntent = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()){
                if (it.resultCode == Activity.RESULT_OK){
                    it.data?.let { intent ->
                        if (intent.hasExtra("archivepostFilter")){
                            intent.getParcelableExtra<PostFilter>("archivepostFilter")?.let{ pf ->
                                viewModel.setPostFilter(pf)
                            }
                        }
                    }
                }
            }

            AppTheme {

                val focusManager = LocalFocusManager.current
                Box {
                    //Header
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                backgroundColor = MaterialTheme.colors.background,
                                title = {
                                    Text(text = stringResource(R.string.search))
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
                                            val intent = Intent(context, SavedSearchActivity::class.java)
                                            savedSearchIntent.launch(intent)
                                        } //do something
                                    ) {
                                        Icon(FeatherIcons.Archive, null)
                                    }
                                    IconButton(
                                        onClick = {
                                            focusManager.clearFocus()
                                            viewModel.clearPostFilter()
                                        } //do something
                                    ) {
                                        Icon(FeatherIcons.Trash2, null)
                                    }

                                }
                            )
                        }
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (viewModel.isLoading.value) {
                                progressDialog()
                            }

                            val scrollState = rememberScrollState()
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(scrollState)
                                    .padding(16.dp)
                                    .padding(bottom = 100.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(MaterialTheme.colors.surface),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Row(Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (viewModel.type.value == null) MaterialTheme.colors.primary else Color.Unspecified)
                                        .clickable {
                                            viewModel.clearPostFilter()
                                            viewModel.type.value = null
                                        }
                                        .padding(10.dp),
                                        horizontalArrangement = Arrangement.Center) {
                                        Text(
                                            text = stringResource(R.string.all),
                                            style = MaterialTheme.typography.h3,
                                            color = if (viewModel.type.value == null) ColoronButton else MaterialTheme.colors.onSurface.copy(
                                                ContentAlpha.disabled
                                            )
                                        )
                                    }
                                    Row(Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (viewModel.type.value == 0) MaterialTheme.colors.primary else Color.Unspecified)
                                        .clickable {
                                            viewModel.clearPostFilter()
                                            viewModel.type.value = 0
                                        }
                                        .padding(10.dp),
                                        horizontalArrangement = Arrangement.Center) {
                                        Text(
                                            text = stringResource(R.string.cars),
                                            style = MaterialTheme.typography.h3,
                                            color = if (viewModel.type.value == 0) ColoronButton else MaterialTheme.colors.onSurface.copy(
                                                ContentAlpha.disabled
                                            )
                                        )
                                    }
                                    Row(Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (viewModel.type.value == 1) MaterialTheme.colors.primary else Color.Unspecified)
                                        .clickable {
                                            viewModel.clearPostFilter()
                                            viewModel.type.value = 1
                                        }
                                        .padding(10.dp),
                                        horizontalArrangement = Arrangement.Center) {
                                        Text(
                                            text = stringResource(R.string.bikes),
                                            style = MaterialTheme.typography.h3,
                                            color = if (viewModel.type.value == 1) ColoronButton else MaterialTheme.colors.onSurface.copy(
                                                ContentAlpha.disabled
                                            )
                                        )
                                    }
                                }

                                //Form

                                //Colorpicker
                                ChoiceDialog(showDialog = viewModel.colorDialog.value,
                                    items = colorList,
                                    onItemClick = { it, index ->
                                        viewModel.color.value = it
                                        viewModel.colorDialog.value = false
                                    },
                                    onDismissRequest = {
                                        viewModel.colorDialog.value = false
                                    }
                                )
                                //Transmission
                                ChoiceDialog(showDialog = viewModel.transmissionDialog.value,
                                    items = transmissionList,
                                    onItemClick = { it, index ->
                                        viewModel.transmission.value = it
                                        viewModel.transmissionDialog.value = false
                                    },
                                    onDismissRequest = {
                                        viewModel.transmissionDialog.value = false
                                    }
                                )

                                //Make
                                ChoiceDialog(showDialog = viewModel.makeDialog.value,
                                    items = makeList,
                                    onItemClick = { it, index ->
                                        viewModel.make.value = it
                                        viewModel.makeDialog.value = false
                                        viewModel.model.value = ""
                                        viewModel.modelList.clear()
                                        viewModel.modelList.add(Carmodel(model = getString(R.string.all)))
                                        viewModel.loadModels(it)
                                    },
                                    onDismissRequest = {
                                        viewModel.makeDialog.value = false
                                    }
                                )
                                ModelChoiceDialog(showDialog = viewModel.modelDialog.value,
                                    items = viewModel.modelList,
                                    onItemClick = { it, index ->
                                        if (it.model == getString(R.string.all)) {
                                            viewModel.model.value = ""
                                        } else {
                                            viewModel.model.value = it.model
                                        }
                                        viewModel.modelDialog.value = false
                                    },
                                    onDismissRequest = {
                                        viewModel.modelDialog.value = false
                                    }
                                )
                                //Location
                                ChoiceDialog(showDialog = viewModel.locationDialog.value,
                                    items = locationList,
                                    onItemClick = { it, index ->
                                        viewModel.location.value = it
                                        viewModel.locationDialog.value = false
                                    },
                                    onDismissRequest = {
                                        viewModel.locationDialog.value = false
                                    }
                                )

                                //fuel type
                                ChoiceDialog(showDialog = viewModel.fueltypeDialog.value,
                                    items = fueltypeList,
                                    onItemClick = { it, index ->
                                        viewModel.fuel_type.value = it
                                        viewModel.fueltypeDialog.value = false
                                    },
                                    onDismissRequest = {
                                        viewModel.fueltypeDialog.value = false
                                    }
                                )

                                //body type
                                ChoiceDialog(showDialog = viewModel.bodytypeDialog.value,
                                    items = bodytypeList,
                                    onItemClick = { it, index ->
                                        viewModel.body_type.value = it
                                        viewModel.bodytypeDialog.value = false
                                    },
                                    onDismissRequest = {
                                        viewModel.bodytypeDialog.value = false
                                    }
                                )
                                SearchField(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                    value = viewModel.search.value,
                                    placeholder = stringResource(R.string.search),
                                    onValueChange = {
                                        viewModel.search.value = it
                                    }
                                )

                                DropDownField(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp),
                                    value = viewModel.color.value,
                                    placeholder = stringResource(R.string.color),
                                    onClick = {
                                        viewModel.colorDialog.value = true
                                    }
                                )
                                DropDownField(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                    value = viewModel.transmission.value,
                                    placeholder = stringResource(R.string.transmission),
                                    onClick = {
                                        viewModel.transmissionDialog.value = true
                                    }
                                )

                                if (viewModel.type.value != 1) {
                                    DropDownField(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp),
                                        value = viewModel.make.value,
                                        placeholder = stringResource(R.string.make),
                                        onClick = {
                                            viewModel.makeDialog.value = true
                                        }
                                    )
                                    DropDownField(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp),
                                        value = viewModel.model.value,
                                        placeholder = stringResource(R.string.model),
                                        onClick = {
                                            if (viewModel.modelList.isNotEmpty()) {
                                                viewModel.modelDialog.value = true
                                            }
                                        }
                                    )
                                }
                                DropDownField(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                    value = viewModel.location.value,
                                    placeholder = stringResource(R.string.location),
                                    onClick = {
                                        viewModel.locationDialog.value = true
                                    }
                                )

                                if (viewModel.type.value != 1) {
                                    DropDownField(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp),
                                        value = viewModel.fuel_type.value,
                                        placeholder = stringResource(R.string.fuel_type),
                                        onClick = {
                                            viewModel.fueltypeDialog.value = true
                                        }
                                    )
                                    DropDownField(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp),
                                        value = viewModel.body_type.value,
                                        placeholder = stringResource(R.string.body_type),
                                        onClick = {
                                            viewModel.bodytypeDialog.value = true
                                        }
                                    )
                                }
                                //price
                                ValueSlider(
                                    onValueChange = { gt, lt ->
                                        viewModel.price_gt.value = gt
                                        viewModel.price_lt.value = lt
                                    },
                                    label = stringResource(R.string.price),
                                    labelGt = when (viewModel.price_gt.value) {
                                        null -> "0"
                                        else -> "${viewModel.price_gt.value} " + stringResource(R.string.dt)
                                    },
                                    labelLt = when (viewModel.price_lt.value) {
                                        null -> stringResource(R.string.all_prices)
                                        else -> "${viewModel.price_lt.value} " + stringResource(R.string.dt)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp),
                                    min = 0f,
                                    max = when (viewModel.type.value) {
                                        1 -> 50f
                                        else -> 200f
                                    },
                                    converter = 1000
                                )

                                MileageSlider(
                                    onValueChange = { max ->
                                        viewModel.mileage_lt.value = max
                                    },
                                    label = stringResource(R.string.mileage),
                                    labelVal = when (viewModel.mileage_lt.value) {
                                        null -> stringResource(R.string.all)
                                        else -> "${viewModel.mileage_lt.value} " + stringResource(R.string.km)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp),
                                )
                                //displacement
                                if (viewModel.type.value != 0) {
                                    ValueSlider(
                                        onValueChange = { gt, lt ->
                                            viewModel.displacement_gt.value = gt
                                            viewModel.displacement_lt.value = lt
                                        },
                                        label = stringResource(R.string.displacement),
                                        labelGt = when (viewModel.displacement_gt.value) {
                                            null -> "0"
                                            else -> "${viewModel.displacement_gt.value} " + stringResource(
                                                R.string.cc
                                            )
                                        },
                                        labelLt = when (viewModel.displacement_lt.value) {
                                            null -> stringResource(R.string.any)
                                            else -> "${viewModel.displacement_lt.value} " + stringResource(
                                                R.string.cc
                                            )
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 16.dp),
                                        min = 0f,
                                        max = 1500f,
                                        converter = 1,
                                        steps = 29
                                    )
                                }

                            }
                            Button(
                                onClick = {
                                    val resultIntent: Intent = Intent()
                                    resultIntent.putExtra(
                                        "outpostFilter",
                                        viewModel.getPostFilter()
                                    )
                                    setResult(RESULT_OK, resultIntent)
                                    finish()
                                },
                                modifier = Modifier
                                    .height(100.dp)
                                    .fillMaxWidth()
                                    .padding(26.dp)
                                    .align(Alignment.BottomCenter)
                            ) {
                                Text(
                                    text = stringResource(R.string.search),
                                    style = MaterialTheme.typography.button
                                )
                            }
                        }
                    }
                }
            }

        }
    }
}