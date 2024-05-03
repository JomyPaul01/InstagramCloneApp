package com.jomy.instagramclone.main

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.jomy.instagramclone.R
import com.jomy.instagramclone.viewmodel.IgViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPostScreen(viewModel: IgViewModel, navController: NavController, imageUri: String?) {

    val encodedUri = remember { mutableStateOf(imageUri) }
    val description = remember { mutableStateOf("") }

    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = stringResource(R.string.back),
                Modifier
                    .clickable { navController.popBackStack() }
                    .padding(8.dp))
            Text(text = stringResource(R.string.post),
                Modifier
                    .padding(8.dp)
                    .clickable {
                        viewModel.onNewPost(Uri.parse(imageUri), description.value) {
                            navController.popBackStack()
                        }
                    })
        }
        CommonDivider()
        Image(
            painter = rememberImagePainter(imageUri),
            contentDescription = null,
            Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .defaultMinSize(minHeight = 150.dp),
            contentScale = ContentScale.FillWidth
        )
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                value = description.value,
                onValueChange = { description.value = it },
                modifier = Modifier
                    .padding(8.dp)
                    .height(150.dp),
                label={(Text(text= stringResource(R.string.description)))},
                colors = TextFieldDefaults.textFieldColors(textColor = Color.Black, containerColor = Color.Transparent)
            )
        }
        if(viewModel.inProgress.value){
            ProgressSpinner()
        }
    }

}
