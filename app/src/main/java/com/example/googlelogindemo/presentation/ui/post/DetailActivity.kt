package com.example.googlelogindemo.presentation.ui.post

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.googlelogindemo.R
import com.example.googlelogindemo.network.models.Favorite
import com.example.googlelogindemo.network.models.Post
import com.example.googlelogindemo.presentation.components.*
import com.example.googlelogindemo.presentation.theme.AccentColor
import com.example.googlelogindemo.presentation.theme.AppTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    @OptIn(ExperimentalPagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_detail)
        val bundle = intent.getBundleExtra("bundle")
        Log.i("maradona", bundle?.getInt("userId")!!.toString())
        var post: Post?

        val viewModel: DetailViewModel = ViewModelProvider(this).get(DetailViewModel::class.java)

        if (bundle.containsKey("post")) {
            post = bundle.getParcelable<Post>("post")
        } else {
            post = bundle.getParcelable<Favorite>("favorite")?.post
        }

        var title = ""
        post?.let {
            post.title?.let {
                title = it
            }
        }
        setContent {
            AppTheme {
                BackHandler(enabled = viewModel.previewImage.value) {
                    viewModel.previewImage.value = false
                }

                post?.let {
                    Scaffold(
                        topBar = {
                            DetailTopNavigationBar(title = if (viewModel.previewImage.value) post.images?.let {
                                getString(
                                    R.string.photos
                                ) + " ${viewModel.previewImagePosition.value + 1}/${it.size}"
                            }
                                ?: title else title,
                                isLoggedIn = viewModel.isAuthenticated.value,
                                isFavorite = viewModel.isFavorite.value,
                                isLoading = viewModel.isFavoriteLoading.value,
                                onActionClick = {
                                    when (it) {
                                        0 -> {
                                            if (viewModel.previewImage.value) {
                                                viewModel.previewImage.value = false
                                            } else {
                                                finish()
                                            }
                                        }
                                        1 -> {
                                            if (viewModel.isAuthenticated.value) {
                                                post.id.let { postId ->
                                                    if (!viewModel.isFavorite.value && !viewModel.isFavoriteLoading.value) {
                                                        viewModel.addToFavorites(postId)
                                                    }
                                                }
                                            } else {
                                                Log.i("detailscreen", "Not authenticated")
                                            }
                                        }
                                        2 -> {
                                            viewModel.reportDialog.value = true
                                        }
                                    }
                                })
                        }
                    ) {
                        val context = LocalContext.current
                        val scrollState = rememberScrollState()
                        if (viewModel.previewImage.value) {
                            Box(modifier = Modifier.fillMaxSize()) {

                                post.let { post ->
                                    post.images?.let {
                                        PostImagePreview(
                                            images = it,
                                            modifier = Modifier.fillMaxSize(),
                                            position = viewModel.previewImagePosition.value,
                                            onNext = {
                                                if (viewModel.previewImagePosition.value < it.size - 1) {
                                                    viewModel.previewImagePosition.value++
                                                }
                                            },
                                            onPrevious = {
                                                if (viewModel.previewImagePosition.value > 0) {
                                                    viewModel.previewImagePosition.value--
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        } else {
                            if (viewModel.isLoading.value) {
                                progressDialog()
                            }
                            Box(modifier = Modifier.fillMaxSize()) {
                                ChoiceConfirmDialog(
                                    showDialog = viewModel.reportDialog.value,
                                    message = getString(R.string.report_dialog_message),
                                    okMessage = getString(R.string.submit_report),
                                    cancelMessage = getString(R.string.cancel),
                                    onOk = {
                                        viewModel.reportDialog.value = false
                                        viewModel.reportPost(post.id)
                                    },
                                    onCancel = { viewModel.reportDialog.value = false }) {
                                }
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .verticalScroll(scrollState)
                                ) {
                                    PostImagePager(post = post, modifier = Modifier.fillMaxWidth(),
                                        onClick = { position ->
                                            viewModel.previewImagePosition.value = position
                                            viewModel.previewImage.value = true
                                        })
                                    PostDetails(post = post, modifier = Modifier.fillMaxWidth())
                                    ReportPost(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                                    ) {
                                        viewModel.reportDialog.value = true
                                    }
                                    Spacer(modifier = Modifier.padding(50.dp))
                                }
                                Button(
                                    colors = ButtonDefaults.buttonColors(backgroundColor = AccentColor),
                                    onClick = {
                                        val intentDial = Intent(
                                            Intent.ACTION_DIAL,
                                            Uri.parse("tel:" + post.ownerPhone)
                                        )
                                        context.startActivity(intentDial)
                                    },
                                    modifier = Modifier
                                        .height(100.dp)
                                        .fillMaxWidth()
                                        .padding(26.dp)
                                        .align(Alignment.BottomCenter)
                                ) {
                                    Text(
                                        text = getString(R.string.contact_seller) + " (${post.ownerPhone})",
                                        style = MaterialTheme.typography.button,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1
                                    )
                                }

                            }

                        }
                    }

                }

            }
        }
    }
}