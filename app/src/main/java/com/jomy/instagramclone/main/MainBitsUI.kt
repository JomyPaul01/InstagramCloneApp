package com.jomy.instagramclone.main

import android.os.Parcelable
import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.ImagePainter.State.Loading
import coil.compose.rememberImagePainter
import com.jomy.instagramclone.ui.DestinationScreen
import com.jomy.instagramclone.R
import com.jomy.instagramclone.data.PostData
import com.jomy.instagramclone.viewmodel.IgViewModel

@Composable
fun NotificationMessage(vm: IgViewModel) {

    val notifMessage = vm.popUpNotification.value?.getContentOrNull()


    if (notifMessage != null) {
        Toast.makeText(LocalContext.current, notifMessage, Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun ProgressSpinner() {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .alpha(0.5f)
            .background(Color.LightGray)
            .clickable(enabled = false) {},
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator()
    }
}

data class NavParam(
    val name: String,
    val value: Parcelable
)

fun navigateTo(navController: NavController, route: String, vararg params: NavParam) {

    for (param in params) {
        navController.currentBackStackEntry?.savedStateHandle?.set(param.name,param.value)
    }

    navController.navigate(route) {
        popUpTo(route)
        launchSingleTop = true
    }
}

@Composable
fun checkSignedIn(vm: IgViewModel, navController: NavController):Boolean{
    val alreadyLoggedIn = remember { mutableStateOf(false) }
    val signedIn = vm.signedIn.value
    if (signedIn && !alreadyLoggedIn.value) {
        alreadyLoggedIn.value = true
        navController.navigate(DestinationScreen.Feed.route) {
            popUpTo(0)
        }
        return true
    }
    return false
}

@Composable
fun CommonImage(
    data: String?,
    modifier: Modifier = Modifier.wrapContentSize(),
    contentScale: ContentScale = ContentScale.Crop
) {

    val painter = rememberImagePainter(data = data)
    Image(
        painter = painter, contentDescription = null, modifier = modifier,
        contentScale = contentScale
    )
    if (painter.state == Loading(painter)) {
        ProgressSpinner()
    }

}

@Composable
fun UserImageCard(
    userImage: String?, modifier: Modifier = Modifier
        .padding(8.dp)
        .size(80.dp)
) {

    Card(shape = CircleShape, modifier = modifier) {
        if (userImage.isNullOrEmpty()) {
            Image(
                painter = painterResource(id = R.drawable.ic_post), contentDescription = null,
                colorFilter = ColorFilter.tint(Color.Gray), modifier = Modifier.size(75.dp)
            )
        } else {
            CommonImage(data = userImage)
        }
    }
}

@Composable
fun CommonDivider() {
    Divider(
        modifier = Modifier
            .padding(top = 8.dp, bottom = 8.dp)
            .alpha(0.3f),
        thickness = 1.dp,
        color = Color.Gray,

        )
}


data class PostRow(
    var post1: PostData? = null,
    var post2: PostData? = null,
    var post3: PostData? = null
) {
    fun isPostFull(): Boolean = post1 != null && post2 != null && post3 != null
    fun addPost(post: PostData) {
        if (post1 == null) {
            post1 = post
        } else if (post2 == null) {
            post2 = post
        } else if (post3 == null) {
            post3 = post
        }
    }
}

@Composable
fun ProfileImage(imageUrl: String?, onClick: () -> Unit) {

    Box(modifier = Modifier
        .padding(top = 16.dp)
        .clickable { onClick.invoke() }) {

        UserImageCard(
            userImage = imageUrl, modifier = Modifier
                .size(80.dp)
        )
        Card(
            shape = CircleShape,
            border = BorderStroke(width = 2.dp, color = Color.White),
            modifier = Modifier
                .padding(bottom = 8.dp, end = 8.dp)
                .size(32.dp)
                .align(Alignment.BottomEnd)
        ) {

            Image(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = null,

                Modifier
                    .background(Color.Blue)
                    .size(30.dp),
                contentScale = ContentScale.FillBounds,
            )
        }
    }
}


@Composable
fun PostImage(postImage: String?, modifier: Modifier) {
    Box(modifier = modifier) {
        var modifier = Modifier
            .padding(1.dp)
            .fillMaxSize()
        if (postImage == null) {
            modifier = modifier.clickable(enabled = false) { }
        }
        CommonImage(data = postImage, modifier = modifier, contentScale = ContentScale.Crop)
    }
}

@Composable
fun PostsRow(item: PostRow, onPostClick: (PostData) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        PostImage(item.post1?.postImage, modifier = Modifier
            .weight(1f)
            .clickable {
                item.post1?.let { post ->
                    onPostClick(post)
                }
            })
        PostImage(item.post2?.postImage, modifier = Modifier
            .weight(1f)
            .clickable {
                item.post2?.let { post ->
                    onPostClick(post)
                }
            })
        PostImage(item.post3?.postImage, modifier = Modifier
            .weight(1f)
            .clickable {
                item.post3?.let { post ->
                    onPostClick(post)
                }
            })
    }

}


@Composable
fun PostList(
    isContentLoading: Boolean,
    isPostLoading: Boolean,
    posts: List<PostData>,
    modifier: Modifier = Modifier,
    onPostClick: (PostData) -> Unit
) {
    if (isPostLoading) {
        ProgressSpinner()
    } else if (posts.isEmpty()) {
        Column(
            modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!isContentLoading) {
                Text(text = stringResource(R.string.no_posts_available), style = MaterialTheme.typography.bodySmall)
            }
        }
    } else {
        LazyColumn(modifier = Modifier) {
            val rows = arrayListOf<PostRow>()
            var currentRow = PostRow()
            rows.add(currentRow)
            for (post in posts) {
                if (currentRow.isPostFull()) {
                    currentRow = PostRow()
                    rows.add(currentRow)
                }
                currentRow.addPost(post = post)
            }

            items(items = rows) { row ->
                PostsRow(item = row, onPostClick = onPostClick)
            }
        }
    }

}

private enum class LikeIconSize {
    SMALL,
    LARGE
}

@Composable
fun LikeAnimation(like: Boolean = true) {
    var sizeState by remember { mutableStateOf(LikeIconSize.LARGE) }
    val transition = updateTransition(targetState = sizeState, label = "")
    val size by transition.animateDp(label = "", transitionSpec = {
        spring(
            stiffness = Spring.StiffnessLow,
            dampingRatio = Spring.DampingRatioMediumBouncy
        )
    }) { state ->
        when (state) {
            LikeIconSize.SMALL -> 0.dp
            LikeIconSize.LARGE -> 150.dp
        }
    }
    Image(
        painter = painterResource(id = if (like) R.drawable.ic_like else R.drawable.ic_dislike),
        contentDescription = stringResource(R.string.like_or_dislike_post),
        modifier = Modifier.size(size = size),
        colorFilter = ColorFilter.tint(if (like) Color.Red else Color.Gray))
    sizeState = LikeIconSize.SMALL
}






