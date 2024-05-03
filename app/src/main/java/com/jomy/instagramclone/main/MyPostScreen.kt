package com.jomy.instagramclone.main

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jomy.instagramclone.R
import com.jomy.instagramclone.ui.DestinationScreen
import com.jomy.instagramclone.viewmodel.IgViewModel


@Composable
fun MyPostScreen(navController: NavController, viewModel: IgViewModel) {

    val newPostImageLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                val encoded = Uri.encode(uri.toString())
                val route = DestinationScreen.NewPost.createPostUri(encoded)
                navController.navigate(route)
            }
        }
    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.padding(20.dp)) {
                ProfileImage(imageUrl = viewModel.userData.value?.imageUrl) {
                    newPostImageLauncher.launch("image/*")
                }
                Text(
                    text = "15\n ${stringResource(id = R.string.posts)}",
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "23\n ${stringResource(R.string.followers)})",
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "25\n ${stringResource(id = R.string.following)}",
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center
                )
            }
            Column {
                Text(
                    text = viewModel.userData.value?.name ?: "Mike",
                    modifier = Modifier.padding(8.dp)
                )
                Text(
                    text = viewModel.userData.value?.userName ?: "Fella",
                    modifier = Modifier.padding(8.dp)
                )
                Text(
                    text = viewModel.userData.value?.bio ?: "Bio",
                    modifier = Modifier.padding(8.dp)
                )
            }
            OutlinedButton(
                onClick = { navigateTo(navController, DestinationScreen.Profile.route) },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(Color.Transparent),
                elevation = ButtonDefaults.buttonElevation(),
                shape = RoundedCornerShape(10)
            ) {
                Text(text = stringResource(R.string.edit_profile), color = Color.Black)
            }

            PostList(
                isContentLoading = viewModel.inProgress.value,
                isPostLoading = viewModel.refreshPostProgress.value,
                posts = viewModel.posts.value,
                modifier = Modifier
                    .weight(1f)
                    .padding(1.dp)
                    .fillMaxSize(),
            ) { post ->
                navigateTo(
                    navController,
                    DestinationScreen.SinglePost.route,
                    NavParam("post", post)
                )
            }
        }
        BottomNavigationMenu(
            selectedItem = BottomNavigationItem.POSTS,
            navController = navController
        )

    }
    if (viewModel.inProgress.value) {
        ProgressSpinner()
    }
}

