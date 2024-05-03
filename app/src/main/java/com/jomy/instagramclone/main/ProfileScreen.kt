package com.jomy.instagramclone.main

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jomy.instagramclone.R
import com.jomy.instagramclone.ui.DestinationScreen
import com.jomy.instagramclone.data.UserData
import com.jomy.instagramclone.viewmodel.IgViewModel

@Composable
fun ProfileScreen(navController: NavController, viewModel: IgViewModel) {

    if (viewModel.inProgress.value) {
        ProgressSpinner()
    } else {
        val userData = viewModel.userData.value
        var name by rememberSaveable {
            mutableStateOf(userData?.name ?: "")
        }
        var username by rememberSaveable {
            mutableStateOf(userData?.userName ?: "")
        }
        var bio by rememberSaveable {
            mutableStateOf(userData?.bio ?: "")
        }
        ProfileContent(
            viewModel,
            name,
            username,
            bio,
            onNameChange = { name = it },
            onUserNameChange = { username = it },
            onBioChange = { bio = it },
            onSave = {
                val userData = UserData(name = name, userName = username, bio = bio, imageUrl = viewModel.userData.value?.imageUrl)
                viewModel.updateProfileData(userData)
            },
            onBack = { navigateTo(navController, DestinationScreen.MyPost.route) },
            onLogOut = {viewModel.onLogOut()})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    viewModel: IgViewModel,
    name: String,
    username: String,
    bio: String,
    onNameChange: (String) -> Unit,
    onUserNameChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    onLogOut: () -> Unit
) {

    Column(
        modifier = Modifier
            .padding(8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(text = stringResource(id = R.string.back), modifier = Modifier.clickable { onBack.invoke() })
            Text(text = stringResource(R.string.save), modifier = Modifier.clickable { onSave.invoke() })
        }
        CommonDivider()

        ProfileImageEdit(imageUrl = viewModel.userData.value?.imageUrl, vm = viewModel)

        CommonDivider()

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(R.string.name), Modifier.width(100.dp))
            TextField(
                value = name,
                onValueChange = onNameChange,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    textColor = Color.Black
                )
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(R.string.username), Modifier.width(100.dp))
            TextField(
                value = username,
                onValueChange = onUserNameChange,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    textColor = Color.Black
                )
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(R.string.bio), Modifier.width(100.dp))
            TextField(
                value = bio,
                onValueChange = onBioChange,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    textColor = Color.Black
                ),
                modifier = Modifier.height(150.dp),
                singleLine = false
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = stringResource(R.string.log_out), modifier = Modifier.clickable { onLogOut.invoke() })
        }


    }


}


@Composable
fun ProfileImageEdit(imageUrl: String?, vm: IgViewModel) {
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()){
        uri: Uri? ->uri?.let{
        vm.uploadProfile(uri)
    }
    }
    Box(modifier = Modifier.height(IntrinsicSize.Min)) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable { launcher.launch("image/*") },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = CircleShape, modifier = Modifier
                    .padding(8.dp)
                    .size(100.dp)
            ) {
                CommonImage(data = imageUrl)
            }
            Text(text = stringResource(R.string.change_profile_picture))
        }

        if (vm.inProgress.value) {
            ProgressSpinner()
        }
    }
}


