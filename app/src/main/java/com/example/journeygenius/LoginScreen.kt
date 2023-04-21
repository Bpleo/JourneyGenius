package com.example.journeygenius

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.ComponentActivity
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.journeygenius.ui.theme.JourneyGeniusTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun JourneyGenius(
    auth: FirebaseAuth,
    mainActivity: MainActivity,
    db: FirebaseFirestore
) {
    val navController = rememberNavController()
    val windowSize = rememberWindowSize()
    val viewModel = JourneyGeniusViewModel(db, auth)
    val currentUser = auth.currentUser
    NavHost(
        navController = navController,
        startDestination = when(currentUser){
            null -> {
                "Login"
            }
            else -> {
                "Main"
            }
        }
    ) {
        composable("Login") {
            LoginScreen(
                navController,
                windowSize,
                viewModel,
                auth,
                mainActivity
            )
        }
        composable("SignUp") {
            SignUpScreen(
                navController,
                windowSize,
                viewModel,
                auth,
                mainActivity,
                db
            )
        }
        composable("Main") {
            MainScreen(
                auth,
                db,
                viewModel
            )
        }
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
        placeholder = { Text(text = placeholder) },
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
            keyboardType = KeyboardType.Password
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginTextField(
    email: MutableState<TextFieldValue>,
    pwd: MutableState<String>,
    viewModel: JourneyGeniusViewModel
) {
    Row {
        Column {
            Text(
                text = "Email: ",
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(45.dp))
            Text(
                text = "Password: ",
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        Column {
            TextField(
                value = viewModel.email.value,
                onValueChange = { newEmail ->
                    viewModel.updateEmail(newEmail)
                },
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
                password = viewModel.pwd.value,
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
fun LoginTextPreview() {
    LoginTextField(
        email = mutableStateOf(TextFieldValue()),
        pwd = mutableStateOf(String()),
        viewModel = JourneyGeniusViewModel(Firebase.firestore, Firebase.auth)
    )
}

@Composable
fun LoginScreen(
    navController: NavHostController,
    windowSize: WindowSize,
    viewModel: JourneyGeniusViewModel,
    auth: FirebaseAuth,
    mainActivity: ComponentActivity
) {
    val email by remember {
        mutableStateOf(viewModel.email)
    }
    val pwd by remember {
        mutableStateOf(viewModel.pwd)
    }
    val context = LocalContext.current
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
                        Button(onClick = {
                            auth.signInWithEmailAndPassword(
                                viewModel.email.value.text,
                                viewModel.pwd.value
                            )
                                .addOnCompleteListener(mainActivity) { task ->
                                    if (task.isSuccessful) {
                                        viewModel.signIn()
                                        navController.navigate("Main") {
                                            popUpTo(0)
                                            launchSingleTop = true
                                        }
                                    } else {
                                        if (task.exception != null) {
                                            when ((task.exception as FirebaseAuthException).errorCode){
                                                "ERROR_WRONG_PASSWORD","ERROR_INVALID_EMAIL" -> {
                                                    Toast.makeText(
                                                        context,
                                                        "Wrong Credentials!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    viewModel.updateEmail(TextFieldValue(""))
                                                    viewModel.updatePwd("")
                                                }
                                                "ERROR_USER_NOT_FOUND" -> {
                                                    Toast.makeText(
                                                        context, "User Not Found. \nPlease Sign Up",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    navController.navigate("SignUp")
                                                }
                                                else -> {

                                                }
                                            }
                                        }
                                    }
                                }
                        }) {
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
                            Button(onClick = {
                                auth.signInWithEmailAndPassword(
                                    viewModel.email.value.text,
                                    viewModel.pwd.value
                                )
                                    .addOnCompleteListener(mainActivity) { task ->
                                        if (task.isSuccessful) {
                                            navController.navigate("Main") {
                                                popUpTo(0)
                                                launchSingleTop = true
                                            }
                                        } else {
                                            Toast.makeText(
                                                context, "Authentication failed.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            }) {
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