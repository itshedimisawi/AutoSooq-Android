package com.example.googlelogindemo.presentation.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.googlelogindemo.R
import com.example.googlelogindemo.presentation.PostFilter
import com.example.googlelogindemo.presentation.components.*
import com.example.googlelogindemo.presentation.ui.post.DetailActivity
import com.example.googlelogindemo.presentation.ui.search.SearchActivity
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import compose.icons.FeatherIcons
import compose.icons.feathericons.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel) {

    Log.i("messi", viewModel.toString() + " : " + viewModel.s)
    Log.i("swipeRefresh", viewModel.isLoading.value.toString())
    val context = LocalContext.current
    val posts = viewModel.posts.value
    val page = viewModel.page.value

    BackHandler(enabled = viewModel.isSearch.value) {
        viewModel.clearSearch()
        viewModel.newSearch()
    }

    val searchIntent =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                Log.i("intentstuff", "result code ok")
                it.data?.let { intent ->
                    Log.i("intentstuff", "has data")
                    intent.getParcelableExtra<PostFilter>("outpostFilter")?.let { postFilter ->
                        Log.i("intentstuff", "got the filter")
                        viewModel.newSearch(postFilter)
                    }
                }
            }
        }

    Box(modifier = Modifier.fillMaxSize()) {
        val filterList: List<String> =
            listOf(
                stringResource(R.string.relevance),
                stringResource(R.string.price_ascending),
                stringResource(R.string.price_descending),
            )
        val filterIcons: List<ImageVector> =
            listOf(
                FeatherIcons.ThumbsUp,
                FeatherIcons.TrendingUp,
                FeatherIcons.TrendingDown,
            )
        ChoiceDialog(showDialog = viewModel.filterDialog.value,
            items = filterList,
            icons = filterIcons,
            onItemClick = { it, index ->
                when (index) {
                    0 -> viewModel.postFilter.value.ordering = ""
                    1 -> viewModel.postFilter.value.ordering = "price"
                    2 -> viewModel.postFilter.value.ordering = "-price"
                }
                viewModel.filterDialog.value = false
                viewModel.newSearch()
            },
            onDismissRequest = {
                viewModel.filterDialog.value = false
            }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            HomeTopBar(label = (viewModel.postFilter.value.search.ifBlank { stringResource(R.string.search_for_cars) }),
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 10.dp,
                        bottom = 10.dp
                    )
                    .fillMaxWidth(),
                onSearchClick = {
                    val intent = Intent(context, SearchActivity::class.java)
                    val bundle = Bundle()
                    bundle.putParcelable("inpostFilter", viewModel.postFilter.value)
                    intent.putExtra("bundle", bundle)
                    searchIntent.launch(intent)
                },
                onFilterClick = {
                    viewModel.filterDialog.value = true
                },
                icon = {
                    if (viewModel.isSearch.value) {
                        Box {
                            Icon(
                                imageVector = FeatherIcons.ArrowLeft,
                                contentDescription = stringResource(R.string.go_back),
                                modifier = Modifier
                                    .size(48.dp)
                                    .clickable {
                                        viewModel.clearSearch()
                                        viewModel.newSearch()
                                    }
                                    .padding(12.dp)
                            )
                        }
                    } else {
                        Icon(
                            painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                            contentDescription = stringResource(R.string.nav_home),
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .size(48.dp)
                                .padding(12.dp)
                        )
                    }
                })
            FilterChipsBar(
                viewModel,
                Modifier.fillMaxWidth()
            )
            Divider(thickness = 1.dp)
            Box(modifier = Modifier.fillMaxSize()) {
                SwipeRefresh(
                    modifier = Modifier
                        .fillMaxSize(),
                    state = rememberSwipeRefreshState(viewModel.isLoading.value),
                    onRefresh = { viewModel.newSearch() },
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {

                        if (viewModel.showErrorHeader.value) {
                            EmptyStatePlaceholder(
                                message = stringResource(R.string.error_connection),
                                actionMessage = stringResource(R.string.try_again),
                                actionIcon = FeatherIcons.RefreshCw
                            ) {
                                viewModel.showErrorHeader.value = false
                                viewModel.newSearch()
                            }
                        }
                        if (viewModel.isLoading.value) {
                            repeat(5) {
                                PostCardPlaceholder()
                            }
                        } else {
                            if (posts.isEmpty()) {
                                EmptyStatePlaceholder(
                                    message = stringResource(R.string.no_results_found),
                                    actionMessage = stringResource(R.string.go_back),
                                    actionIcon = FeatherIcons.ArrowLeft
                                ) {
                                    viewModel.clearSearch()
                                    viewModel.newSearch()
                                }
                            } else {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    val listState = rememberLazyListState()
                                    val coroutineScope = rememberCoroutineScope()
                                    val currentFirstVisibleItemIndex =
                                        remember { mutableStateOf(listState.firstVisibleItemIndex) }
                                    val isScrollingUp =
                                        remember { mutableStateOf(false) }
                                    val scrollUpButtonVisible =
                                        remember { mutableStateOf(false) }

                                    LazyColumn(
                                        state = listState,
                                        modifier = Modifier.fillMaxSize()
                                    ) {

                                        itemsIndexed(
                                            items = posts
                                        ) { index, post ->
                                            Box {
                                                viewModel.onChangeScrollPosition(index)
                                                if ((index + 1) >= (page * PAGE_SIZE)) {
                                                    viewModel.nextPage()
                                                }

                                                PostCard(post = post, onClick = {
                                                    val intent =
                                                        Intent(context, DetailActivity::class.java)
                                                    val bundle = Bundle()
                                                    bundle.putParcelable("post", post)
                                                    intent.putExtra("bundle", bundle)
                                                    context.startActivity(intent)
                                                })
                                            }
                                        }
                                    }
                                    if (listState.isScrollInProgress) {
                                        DisposableEffect(Unit) {
                                            onDispose {
//                                            if (listState.firstVisibleItemIndex < currentFirstVisibleItemIndex.value) { //up
//                                                if (listState.firstVisibleItemIndex > 0) {
//                                                    scrollUpButtonVisible.value = true
//                                                    isScrollingUp.value = true
//                                                }
//                                            } else if (listState.firstVisibleItemIndex > currentFirstVisibleItemIndex.value) { //down
//                                                scrollUpButtonVisible.value = false
//                                                isScrollingUp.value = false
//                                            }else{
//                                                scrollUpButtonVisible.value = isScrollingUp.value==true
//                                            }
                                                scrollUpButtonVisible.value =
                                                    (listState.firstVisibleItemIndex < currentFirstVisibleItemIndex.value && listState.firstVisibleItemIndex > 0) || isScrollingUp.value == true
                                                isScrollingUp.value =
                                                    (listState.firstVisibleItemIndex < currentFirstVisibleItemIndex.value && listState.firstVisibleItemIndex > 0)
                                                currentFirstVisibleItemIndex.value =
                                                    listState.firstVisibleItemIndex
                                            }
                                        }
                                    }
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentHeight(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        AnimatedVisibility(scrollUpButtonVisible.value) {
                                            ScrollToTopButton(modifier = Modifier.padding(top = 16.dp),
                                                onClick = {
                                                    coroutineScope.launch {
                                                        scrollUpButtonVisible.value = false
                                                        listState.animateScrollToItem(index = 0)
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Column(modifier = Modifier.fillMaxWidth()
                    .padding(bottom = 50.dp)
                    .align(BottomCenter),
                horizontalAlignment = CenterHorizontally) {
                    AnimatedVisibility (visible = viewModel.isSearch.value && !viewModel.isCurrentSearchSaved.value,
                        exit = fadeOut(
                            animationSpec = tween(
                                durationMillis = 300,
                            )
                        )
                    ) {
                        SaveSearchButton(modifier = Modifier
                            .padding(16.dp), onClick = {
                            viewModel.saveCurrentSearch()
                        })
                    }
                }
            }
        }
    }
}





