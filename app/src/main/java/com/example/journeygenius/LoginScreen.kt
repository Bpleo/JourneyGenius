package com.example.journeygenius

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.journeygenius.personal.PersonalViewModel
import com.example.journeygenius.ui.theme.JourneyGeniusTheme

@Composable
fun JourneyGenius() {
    val navController = rememberNavController()
    val windowSize = rememberWindowSize()
    val viewModel: PersonalViewModel = viewModel()
    NavHost(navController = navController, startDestination = "Login") {
        composable("Login") { LoginScreen(navController, windowSize, viewModel) }
        composable("SignUp") { SignUpScreen(navController, windowSize, viewModel) }
        composable("Main"){ MainScreen() }
        // add more screens here
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordTextField(
    password: String,
    onPasswordChange: (String) -> Unit,
    placeholder: String = "Enter Your Password",
    label: String = "Password: "
) {
    var passwordVisibility by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        placeholder = { Text(text = placeholder)},
        trailingIcon = {
            IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                val image = if (passwordVisibility) {
                    Icons.Filled.VisibilityOff
                } else {
                    Icons.Filled.Visibility
                }
                Icon(imageVector = image, contentDescription = "Toggle password visibility")
            }
        },
        label = {
            Text(text = label)
        },
        visualTransformation = if (passwordVisibility) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        maxLines = 1,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Password)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginTextField(email: MutableState<TextFieldValue>, pwd: MutableState<String>, viewModel: PersonalViewModel) {
    Row{
        Column{
            Text(
                text = "Email: ",
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(13.dp))
            Text(
                text = "Password: ",
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        Column{
            TextField(
                value = email.value,
                onValueChange = {newEmail ->
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
            PasswordTextField(
                password = pwd.value,
                onPasswordChange = { newPwd ->
                    viewModel.updatePwd(newPwd)
                },
                placeholder = "Enter your Password"
            )
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun LoginTextPreview(){
    LoginTextField(
        email = mutableStateOf(TextFieldValue()),
        pwd = mutableStateOf(String()),
        viewModel = PersonalViewModel()
    )
}

@Composable
fun LoginScreen(
    navController: NavHostController,
    windowSize: WindowSize,
    viewModel: PersonalViewModel = viewModel()
){
    val email by remember {
        mutableStateOf(viewModel.email)
    }
    val pwd by remember {
        mutableStateOf(viewModel.pwd)
    }
    JourneyGeniusTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (windowSize.height) {
                WindowType.Medium -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(5.dp)
                    ) {
                        LoginTextField(email = email, pwd = pwd, viewModel)
                        Spacer(modifier = Modifier.height(40.dp))
                        Button(onClick = {}) {
                            Text(text = "Login In")
                        }
                        Spacer(modifier = Modifier.height(40.dp))
                        Text(
                            text = "Don't have an account yet? Sign up",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.clickable {
                                navController.navigate("SignUp")
                            }
                        )
                    }
                }
                else -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LoginTextField(email = email, pwd = pwd, viewModel)
                        Spacer(modifier = Modifier.width(40.dp))
                        Column {
                            Button(onClick = {}) {
                                Text(text = "Login In")
                            }
                            Spacer(modifier = Modifier.height(40.dp))
                            Text(
                                text = "Don't have an account yet? Sign up",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.clickable {
                                    navController.navigate("SignUp")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}