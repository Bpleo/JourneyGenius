package com.example.journeygenius.personal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentComposer
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.journeygenius.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PersonalDetail(
    viewModel: JourneyGeniusViewModel
){

}

@Composable
fun PersonalAccountScreen(
    viewModel: JourneyGeniusViewModel,
    db: FirebaseFirestore,
    auth: FirebaseAuth,
    navController: NavHostController,
    windowSize: WindowSize
){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PersonalTopBar(context = LocalContext.current.applicationContext, title = "Details", navController = navController)
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (windowSize.height) {
                WindowType.Medium -> {
                    Column {
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
            text = "Email: ",
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
                    Text("Email: ")
                },
                placeholder = {
                    Text(text = "Enter your Email")
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
                modifier = Modifier.fillMaxWidth()
            ){
                Text(
                    text = "Reset Email",
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
            text = "User Name: ",
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
                    Text("User Name: ")
                },
                placeholder = {
                    Text(text = "Enter your Email")
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
                modifier = Modifier.fillMaxWidth()
            ){
                Text(
                    text = "Reset Username",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun PwdField(viewModel: JourneyGeniusViewModel, db: FirebaseFirestore, auth: FirebaseAuth) {
    Column(){
        Text(
            text = "Password: ",
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            color = MaterialTheme.colorScheme.primary,
        )
        Row{
            PasswordTextField(
                password = viewModel.pwd.value,
                onPasswordChange = {newPwd ->
                    viewModel.updatePwd(newPwd)
                },
                placeholder = "Enter your Password"
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = "Verify Password: ",
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            color = MaterialTheme.colorScheme.primary,
        )
        Row{
            PasswordTextField(
                password = viewModel.verifyPwd.value,
                onPasswordChange = {newPwd ->
                    viewModel.updateVerifyPwd(newPwd)
                },
                placeholder = "Verify your Password"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                          viewModel.resetPwd()
                },
                contentPadding = PaddingValues(0.dp,8.dp),
                modifier = Modifier.fillMaxWidth().padding(0.dp,8.dp)
            ){
                Text(
                    text = "Reset Password",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewEmailField(){
//    EmailField(viewModel, db, auth)
//}

//@Composable
//fun PersonalAccount(){
//    val windowSize = rememberWindowSize()
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp),
//            contentAlignment = Alignment.Center,
//        ) {
//            when (windowSize.height) {
//                WindowType.Medium -> {
//                    Column {
//                        UserNameField(viewModel, db)
//                        Spacer(modifier = Modifier.height(16.dp))
//                        EmailField()
//                        Spacer(modifier = Modifier.height(16.dp))
//                        PwdField()
//                    }
//                }
//                else -> {
//
//                }
//            }
//        }
//    }
//}

//@Preview(showBackground = true)
//@Composable
//fun PreviewPersonalAccount(){
//    PersonalAccount()
//}