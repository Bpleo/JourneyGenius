package com.example.journeygenius.personal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.journeygenius.*
import com.example.journeygenius.R
import com.example.journeygenius.ui.theme.JourneyGeniusTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun PersonalAccountScreen(
    viewModel: JourneyGeniusViewModel,
    db: FirebaseFirestore,
    auth: FirebaseAuth,
    navController: NavHostController,
    windowSize: WindowSize
){
    JourneyGeniusTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PersonalTopBar(
                context = LocalContext.current.applicationContext,
                title = stringResource(R.string.details),
                navController = navController
            )
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                when (windowSize.height) {
                    WindowType.Medium -> {
                        Column(
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            UserNameField(
                                viewModel, db, auth
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            EmailField(
                                viewModel, db, auth
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            PwdField(
                                viewModel, db, auth
                            )
                        }
                    }
                    else -> {
                        Column(
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            UserNameField(
                                viewModel, db, auth
                            )
                            Spacer(modifier = Modifier.height(32.dp))
                            EmailField(
                                viewModel, db, auth
                            )
                            Spacer(modifier = Modifier.height(32.dp))
                            PwdField(
                                viewModel, db, auth
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailField(viewModel: JourneyGeniusViewModel, db: FirebaseFirestore, auth: FirebaseAuth) {
    Column(){
        Text(
            text = stringResource(R.string.email),
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            color = MaterialTheme.colorScheme.primary,
        )
        Row{
            TextField(
                value = viewModel.email.value,
                onValueChange = { newEmail ->
                    viewModel.updateEmail(newEmail)
                } ,
                label = {
                    Text(stringResource(R.string.email))
                },
                placeholder = {
                    Text(text = stringResource(R.string.enter_ur_email))
                },
                maxLines = 1,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    viewModel.resetEmail(viewModel.email.value)
                },
                contentPadding = PaddingValues(0.dp, 8.dp),
                modifier = Modifier.fillMaxWidth().padding(16.dp,0.dp)
            ){
                Text(
                    text = stringResource(R.string.reset_email),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserNameField(
    viewModel: JourneyGeniusViewModel,
    db: FirebaseFirestore,
    auth: FirebaseAuth
) {
    val uid = auth.currentUser?.uid
    Column(){
        Text(
            text = stringResource(R.string.user_name),
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            color = MaterialTheme.colorScheme.primary,
        )
        Row{
            TextField(
                value = viewModel.userName.value,
                onValueChange = { newUserName ->
                    viewModel.updateUserName(newUserName)
                } ,
                label = {
                    Text(stringResource(R.string.user_name))
                },
                placeholder = {
                    Text(text = stringResource(R.string.enter_ur_email))
                },
                maxLines = 1,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                          viewModel.resetUserName(viewModel.userName.value)
                },
                contentPadding = PaddingValues(0.dp, 8.dp),
                modifier = Modifier.fillMaxWidth().padding(16.dp,0.dp)
            ){
                Text(
                    text = stringResource(R.string.reset_username),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun PwdField(viewModel: JourneyGeniusViewModel, db: FirebaseFirestore, auth: FirebaseAuth) {
    val context = LocalContext.current
    Button(
        onClick = {
            viewModel.resetPwd(context)
        },
        contentPadding = PaddingValues(0.dp,8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 8.dp)
    ){
        Text(
            text = stringResource(R.string.reset_pswd),
            textAlign = TextAlign.Center
        )
    }
}
