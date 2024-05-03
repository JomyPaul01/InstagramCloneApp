package com.jomy.instagramclone.ui

import com.jomy.instagramclone.auth.SignupScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jomy.instagramclone.R
import com.jomy.instagramclone.auth.LoginScreen
import com.jomy.instagramclone.viewmodel.IgViewModel
import com.jomy.instagramclone.data.PostData
import com.jomy.instagramclone.main.FeedScreen
import com.jomy.instagramclone.main.MyPostScreen
import com.jomy.instagramclone.main.NewPostScreen
import com.jomy.instagramclone.main.NotificationMessage
import com.jomy.instagramclone.main.ProfileScreen
import com.jomy.instagramclone.main.SearchScreen
import com.jomy.instagramclone.main.SinglePostScreen
import com.jomy.instagramclone.ui.theme.InstagramCloneTheme
import com.jomy.instagramclone.viewmodel.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InstagramCloneTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    InstagramApp()
                }
            }
        }
    }
}

sealed class DestinationScreen(var route: String) {
    object Signup : DestinationScreen(Constants.SIGNUP)
    object Login : DestinationScreen(Constants.LOGIN)
    object Feed : DestinationScreen(Constants.FEED)
    object Search : DestinationScreen(Constants.SEARCH)
    object MyPost : DestinationScreen(Constants.MYPOST)
    object Profile : DestinationScreen(Constants.PROFILE)
    object NewPost : DestinationScreen("${Constants.NEWPOST}/{imageuri}") {
        fun createPostUri(uri: String) = "${Constants.NEWPOST}/${uri}"
    }

    object SinglePost : DestinationScreen(Constants.SINGLEPOST)
}

@Composable
fun InstagramApp() {
    val vm = hiltViewModel<IgViewModel>()
    val navController = rememberNavController()

    NotificationMessage(vm = vm)

    NavHost(navController = navController, startDestination = DestinationScreen.Signup.route) {
        composable(DestinationScreen.Signup.route) {
            SignupScreen(navController = navController, vm = vm)
        }
        composable(DestinationScreen.Login.route) {
            LoginScreen(navController = navController, vm = vm)
        }
        composable(DestinationScreen.Feed.route) {
            vm.getPersonalisedFeed()
            FeedScreen(navController = navController, viewModel = vm)
        }
        composable(DestinationScreen.Search.route) {
            SearchScreen(navController = navController, viewModel = vm)
        }
        composable(DestinationScreen.MyPost.route) {
            MyPostScreen(navController = navController, viewModel = vm)
        }
        composable(DestinationScreen.Profile.route) {
            ProfileScreen(navController = navController, viewModel = vm)
        }
        composable(DestinationScreen.NewPost.route) {
            val uri = it.arguments?.getString(stringResource(R.string.imageuri))
            uri?.let {
                NewPostScreen(viewModel = vm, navController = navController, imageUri = uri)
            }
        }
        composable(DestinationScreen.SinglePost.route) {
            val postData =
                navController.previousBackStackEntry?.savedStateHandle?.get<PostData>(Constants.POST)
            postData?.let {
                SinglePostScreen(navController = navController, vm = vm, post = postData)
            }
        }

    }


}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    InstagramCloneTheme {
        InstagramApp()
    }
}