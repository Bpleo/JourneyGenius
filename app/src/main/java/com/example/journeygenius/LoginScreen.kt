package com.example.journeygenius

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.journeygenius.personal.PersonalViewModel
import com.example.journeygenius.ui.theme.JourneyGeniusTheme

@Composable
fun MyApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController, rememberWindowSize(), PersonalViewModel()) }
        composable("SignUp") { SignUpScreen() }
        // add more screens here
    }
}

@Composable
fun AppContent() {
    MaterialTheme {
        MyApp()
    }
}

@Preview
@Composable
fun AppContentPreview() {
    AppContent()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordTextField(
    password: String,
    onPasswordChange: (String) -> Unit,
    placeholder: String = "Enter Your Password"
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
        visualTransformation = if (passwordVisibility) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginTextField(email: MutableState<TextFieldValue>, pwd: MutableState<String>) {
    Row{
        Column{
            Text(
               text = "Email: "
            )
            Spacer(modifier = Modifier.height(35.dp))
            Text(
                text = "Password: "
            )

        }
        Spacer(modifier = Modifier.width(20.dp))
        Column{
            TextField(
                value = email.value,
                onValueChange = {} ,
                placeholder = {
                    Text(text = "Enter your Email")
                }
            )
            PasswordTextField(
                password = pwd.value,
                onPasswordChange = {},
                placeholder = "Enter your Password"
            )
        }
    }
}

@Composable
fun LoginScreen(
    navController: NavHostController,
    windowSize: WindowSize,
    viewModel: PersonalViewModel
){
    val email = remember {
        mutableStateOf(viewModel.email)
    }
    val pwd = remember {
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
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LoginTextField(email = email.value, pwd = pwd.value)
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
                        LoginTextField(email = email.value, pwd = pwd.value)
                        Spacer(modifier = Modifier.width(40.dp))
                        Button(onClick = {}) {
                            Text(text = "Login In")
                        }
                    }
                }
            }
        }
    }

}

//@Preview(showBackground = true)
//@Composable
//fun LoginScreenPreview() {
//    LoginScreen(,rememberWindowSize(), PersonalViewModel())
//}