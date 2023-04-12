package com.example.journeygenius

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.journeygenius.personal.PersonalViewModel
import com.example.journeygenius.ui.theme.JourneyGeniusTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpField(
    viewModel: PersonalViewModel
){
  Row{
      Column{
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
          Spacer(modifier = Modifier.height(45.dp))
          Text(
              text = "Verify Password: ",
              fontSize = MaterialTheme.typography.bodyLarge.fontSize,
              color = MaterialTheme.colorScheme.primary,
          )
      }
      Column{
          TextField(
              value = viewModel.email.value,
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
              password = viewModel.pwd.value,
              onPasswordChange = { newPwd ->
                  viewModel.updatePwd(newPwd)
              },
              placeholder = "Enter your Password"
          )
          PasswordTextField(
              password = viewModel.verifyPwd.value,
              onPasswordChange = { newPwd ->
                  viewModel.updateVerifyPwd(newPwd)
              },
              placeholder = "Verify your Password"
          )
      }
  }
}

fun isValidEmail(email: String): Boolean {
    val pattern = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
    return pattern.matches(email)
}

@Composable
fun SignUpScreen(
    navController: NavHostController,
    windowSize: WindowSize,
    viewModel: PersonalViewModel
){
    val snackBarHostState = remember { SnackbarHostState()}
    val context = LocalContext.current
    JourneyGeniusTheme{
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            when(windowSize.height) {
                WindowType.Medium -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(5.dp)
                    ) {
                        SignUpField(viewModel = viewModel)
                        Spacer(modifier = Modifier.height(40.dp))
                        Row{
                            Button(onClick = {
                                    navController.navigate("Login"){
                                        popUpTo(0)
                                        launchSingleTop = true
                                    }
                                }
                            ){
                                Text(text = "Back")
                            }
                            Spacer(modifier = Modifier.width(15.dp))
                            Button(onClick = {
                                if (viewModel.email.value.text.isEmpty()){
                                    Toast.makeText(context,"Please Enter Email!", Toast.LENGTH_SHORT).show()
                                }else if (!isValidEmail(viewModel.email.value.text)){
                                    Toast.makeText(context,"Please Enter Email in Correct Format!", Toast.LENGTH_SHORT).show()
                                    viewModel.updateEmail(TextFieldValue(""))
                                }else if (viewModel.pwd.value.isEmpty() || viewModel.verifyPwd.value.isEmpty()) {
                                    Toast.makeText(context,"Please Enter Password!", Toast.LENGTH_SHORT).show()
                                }else if (viewModel.pwd.value != viewModel.verifyPwd.value){
                                    Toast.makeText(context,"Password doesn't match!", Toast.LENGTH_SHORT).show()
                                    viewModel.updatePwd("")
                                    viewModel.updateVerifyPwd("")
                                }else {

                                }
                            }) {
                                Text(text = "Sign Up")
                            }
                        }
                    }
                }
                else -> {
                    SignUpField(viewModel = viewModel)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview(){
    SignUpScreen(navController = rememberNavController(), windowSize = rememberWindowSize(), viewModel = PersonalViewModel())
}