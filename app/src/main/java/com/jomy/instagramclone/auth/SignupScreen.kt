package com.jomy.instagramclone.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jomy.instagramclone.R
import com.jomy.instagramclone.main.ProgressSpinner
import com.jomy.instagramclone.main.checkSignedIn
import com.jomy.instagramclone.main.navigateTo
import com.jomy.instagramclone.ui.DestinationScreen
import com.jomy.instagramclone.viewmodel.IgViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(navController: NavController, vm: IgViewModel) {
    if (checkSignedIn(vm, navController)) {
        return
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .verticalScroll(
                    rememberScrollState()
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val usernameState = remember { mutableStateOf(TextFieldValue()) }
            val emailState = remember { mutableStateOf(TextFieldValue()) }
            val passwordState = remember { mutableStateOf(TextFieldValue()) }
            Image(
                modifier = Modifier
                    .width(250.dp)
                    .padding(top = 16.dp)
                    .padding(8.dp),
                painter = painterResource(id = R.drawable.ig_logo), contentDescription = null
            )
            Text(
                text = stringResource(R.string.sign_up),
                modifier = Modifier.padding(8.dp),
                fontFamily = FontFamily.SansSerif,
                fontSize = 30.sp
            )

            OutlinedTextField(
                value = usernameState.value,
                onValueChange = { usernameState.value = it },
                modifier = Modifier.padding(8.dp),
                label = { Text(text = stringResource(id = R.string.username)) }
            )
            OutlinedTextField(
                value = emailState.value,
                onValueChange = { emailState.value = it },
                modifier = Modifier.padding(8.dp),
                label = { Text(text = stringResource(id = R.string.email)) }
            )

            OutlinedTextField(
                value = passwordState.value,
                onValueChange = { passwordState.value = it },
                modifier = Modifier.padding(8.dp),
                label = { Text(text = stringResource(id = R.string.password)) },
                visualTransformation = PasswordVisualTransformation()
            )
            Button(onClick = {
                vm.onSignUp(
                    usernameState.value.text,
                    emailState.value.text,
                    passwordState.value.text
                )
            }, modifier = Modifier.padding(8.dp)) {
                Text(text = stringResource(id = R.string.sign_up))
            }

            Text(
                text = stringResource(R.string.already_a_user_go_to_login),
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        navigateTo(
                            navController = navController,
                            route = DestinationScreen.Login.route
                        )
                    },
                color = androidx.compose.ui.graphics.Color.Blue
            )

            val isLoading = vm.inProgress.value
            if (isLoading) {
                ProgressSpinner()
            }
        }
    }
}