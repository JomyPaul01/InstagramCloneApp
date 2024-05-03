package com.jomy.instagramclone.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jomy.instagramclone.ui.DestinationScreen
import com.jomy.instagramclone.R

enum class BottomNavigationItem(val icon: Int, val navDestinationScreen: DestinationScreen) {
    FEED(R.drawable.ic_home, DestinationScreen.Feed),
    SEARCH(R.drawable.ic_search, DestinationScreen.Search),
    POSTS(R.drawable.ic_post, DestinationScreen.MyPost)
}

@Composable
fun BottomNavigationMenu(selectedItem: BottomNavigationItem, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 4.dp)
            .background(Color.White),
        verticalAlignment = Alignment.Bottom

    ) {

        for (item in BottomNavigationItem.values()) {
            Image(painter = painterResource(item.icon),
                null,
                modifier = Modifier
                    .padding(4.dp)
                    .size(40.dp)
                    .weight(1f)
                    .clickable { navigateTo(navController, item.navDestinationScreen.route)},
                colorFilter = if(item==selectedItem) ColorFilter.tint(Color.Black) else ColorFilter.tint(Color.Gray))

        }
    }
}