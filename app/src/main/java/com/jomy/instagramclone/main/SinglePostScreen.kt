package com.jomy.instagramclone.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jomy.instagramclone.R
import com.jomy.instagramclone.data.PostData
import com.jomy.instagramclone.viewmodel.IgViewModel

@Composable
fun SinglePostScreen(navController: NavController, vm: IgViewModel, post: PostData) {
    post.userId?.let {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Text(text = stringResource(id = R.string.back), modifier = Modifier.clickable { navController.popBackStack() })
        }
        CommonDivider()
        SinglePostDisplay(vm = vm, post = post)
    }
}

@Composable
fun SinglePostDisplay(vm: IgViewModel, post: PostData) {
    Column {


        Box(
            modifier = Modifier
                .height(48.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                UserImageCard(
                    userImage = vm.userData.value?.imageUrl, modifier = Modifier
                        .size(32.dp)
                        .padding(3.dp)
                )
//                Card(
//                    shape = CircleShape, modifier = Modifier
//                        .padding(8.dp)
//                        .size(32.dp)
//                ) {
//                    Image(
//                        painter = rememberImagePainter(data = vm.userData?.value?.imageUrl),
//                        contentDescription = null
//                    )
//                }

                Text(text = post.username ?: "")
                Text(text = ".", modifier = Modifier.padding(8.dp))

                if (vm.userData.value?.userId == post.userId) {
                    //dont show anything, current user's post
                } else if (vm.userData.value?.following?.contains(post.userId) == true) {
                    Text(text = stringResource(id = R.string.following), color = Color.Gray, modifier = Modifier.clickable {
                        vm.onFollowCick(post.userId!!)
                    })
                } else {
                    Text(text = stringResource(R.string.follow), color = Color.Blue, modifier = Modifier.clickable {
                        vm.onFollowCick(post.userId!!)
                    })
                }
            }

        }
        Box {
            val modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(150.dp)
            CommonImage(
                data = post.postImage,
                modifier = modifier,
                contentScale = ContentScale.FillWidth,
            )
        }
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.ic_like),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                colorFilter = ColorFilter.tint(Color.Red)
            )
            Text(text = " ${post.likes?.size ?: 0}    ${stringResource(id = R.string.likes)}", modifier = Modifier.padding(8.dp))

        }

        Row(modifier = Modifier.padding(8.dp)) {
            Text(text = post.username ?: "", fontWeight = FontWeight.Bold)
            Text(text = post.description ?: "", modifier = Modifier.padding(start = 8.dp))
        }

        Row(modifier = Modifier.padding(8.dp)) {
            Text(
                text = "10 ${stringResource(id = R.string.comment)}",
                color = Color.Gray,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}