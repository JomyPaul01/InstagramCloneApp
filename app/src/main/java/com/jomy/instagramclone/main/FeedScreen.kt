package com.jomy.instagramclone.main

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jomy.instagramclone.ui.DestinationScreen
import com.jomy.instagramclone.data.PostData
import com.jomy.instagramclone.viewmodel.Constants
import com.jomy.instagramclone.viewmodel.IgViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FeedScreen(navController: NavController, viewModel: IgViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .wrapContentHeight()
            ) {
                UserImageCard(userImage = viewModel.userData.value?.imageUrl)
            }
            PostsList(
                posts = viewModel.postsFeed.value,
                navController = navController,
                vm = viewModel
            )
        }
        BottomNavigationMenu(
            selectedItem = BottomNavigationItem.FEED,
            navController = navController
        )
    }
}

@Composable
fun PostsList(
    posts: List<PostData>,
    navController: NavController,
    vm: IgViewModel
) {
    Box(modifier = Modifier) {
        LazyColumn {
            items(items = posts) {
                Post(it, vm) {
                    navigateTo(
                        navController,
                        DestinationScreen.SinglePost.route,
                        NavParam(Constants.POST, it)
                    )
                }
            }
        }
        if (vm.postFeedProgress.value) {
            ProgressSpinner()
        }
    }

}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun Post(post: PostData, vm: IgViewModel, onPostClick: () -> Unit) {
    var likeAnimation by remember{mutableStateOf(false)}
    var dislikeAnimation by remember{ mutableStateOf(false) }


    Card(
        shape = RoundedCornerShape(corner = CornerSize(4.dp)),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 4.dp, bottom = 4.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(shape= CircleShape, modifier = Modifier
                    .padding(5.dp)
                    .height(32.dp)){
                    CommonImage(data = post.userImage, contentScale = ContentScale.Crop)
                }
                Text(text=post.username?:"",modifier = Modifier.padding(4.dp))
            }
            Box(modifier=Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                val modifier= Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 150.dp)
                    .pointerInput(Unit){
                        detectTapGestures(
                            onDoubleTap = {
                                if (post.likes?.contains(vm.auth.currentUser?.uid) == true) {
                                    dislikeAnimation = true
                                } else {
                                    likeAnimation = true
                                }
                                vm.onLikePost(post)
                            },
                            onTap = {
                                onPostClick.invoke()
                            }
                        )
                    }
                CommonImage(data = post.postImage,modifier=modifier, contentScale = ContentScale.FillWidth)
                if(likeAnimation){
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1000L)
                        likeAnimation = false
                    }
                    LikeAnimation()
                }
                if(dislikeAnimation){
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1000L)
                        dislikeAnimation = false
                    }
                    LikeAnimation(false)
                }
            }
        }
    }
}
