package com.example.journeygenius

import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.journeygenius.data.models.Plans
import com.example.journeygenius.data.models.User
import com.example.journeygenius.ui.theme.JourneyGeniusTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Signup Composable
 * Handles logic of user sign up
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpField(
    viewModel: JourneyGeniusViewModel
){
  Row{
      Column{
          Text(
              text = "User Name: ",
              fontSize = MaterialTheme.typography.bodyLarge.fontSize,
              color = MaterialTheme.colorScheme.primary,
          )
          Spacer(modifier = Modifier.height(40.dp))
          Text(
              text = "Email: ",
              fontSize = MaterialTheme.typography.bodyLarge.fontSize,
              color = MaterialTheme.colorScheme.primary,
          )
          Spacer(modifier = Modifier.height(40.dp))
          Text(
              text = "Password: \n(At least 6 letters)",
              fontSize = MaterialTheme.typography.bodyLarge.fontSize,
              color = MaterialTheme.colorScheme.primary,
          )
          Spacer(modifier = Modifier.height(40.dp))
          Text(
              text = "Verify Password: \n" +
                      "(At least 6 letters)",
              fontSize = MaterialTheme.typography.bodyLarge.fontSize,
              color = MaterialTheme.colorScheme.primary,
          )
      }
      Column{
          TextField(
              value = viewModel.userName.value,
              onValueChange = {newUserName ->
                  viewModel.updateUserName(newUserName)
              } ,
              label = {
                  Text("User Name: ")
              },
              placeholder = {
                  Text(text = "Enter your User Name")
              },
              maxLines = 1,
              keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
          )
          Spacer(modifier = Modifier.height(8.dp))
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

@Composable
fun SignUpButton(
    navController: NavHostController,
    viewModel: JourneyGeniusViewModel,
    auth: FirebaseAuth,
    mainActivity: ComponentActivity,
    db: FirebaseFirestore
){
    val context = LocalContext.current
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
            if (viewModel.userName.value.text.isEmpty()) {
                Toast.makeText(context,"Please Enter User Name!", Toast.LENGTH_SHORT).show()
            } else if (viewModel.email.value.text.isEmpty()){
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
                val email = viewModel.email.value.text
                val pwd = viewModel.pwd.value
                val userName = viewModel.userName.value.text
                auth.createUserWithEmailAndPassword(email, pwd)
                    .addOnCompleteListener(mainActivity) {task ->
                        if (task.isSuccessful){
                            Log.i("Auth","Success")
                            val currentUser = auth.currentUser
                            val uid = currentUser!!.uid
                            val user = User(
                                name = userName,
                                email = email,
                                pwd = pwd,
                                id = uid,
                                Plan_List = mapOf(),
                                likedPlanList = listOf<String>())
                            db.collection("users").document(uid).set(user)
                                .addOnSuccessListener {
                                    Log.d("FIRESTORE", "DocumentSnapshot written with ID: $uid")
                                }.addOnFailureListener { e ->
                                    Log.w("FIRESTORE", "Error adding document", e)
                                }
                            navController.navigate("Login"){
                                popUpTo(0)
                                launchSingleTop = true
                            }
                        } else {
                            Toast.makeText(context, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }) {
            Text(text = "Sign Up")
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
    viewModel: JourneyGeniusViewModel,
    auth: FirebaseAuth,
    mainActivity: ComponentActivity,
    db: FirebaseFirestore
){
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
                        SignUpButton(
                            navController = navController,
                            viewModel = viewModel,
                            auth = auth,
                            mainActivity = mainActivity,
                            db = db
                        )
                    }
                }
                else -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(5.dp)
                    ){
                        SignUpField(viewModel = viewModel)
                        Spacer(modifier = Modifier.width(40.dp))
                        SignUpButton(
                            navController = navController,
                            viewModel = viewModel,
                            auth = auth,
                            mainActivity = mainActivity,
                            db = db
                        )
                    }
                }
            }
        }
    }
}