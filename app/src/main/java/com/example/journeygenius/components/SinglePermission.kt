package com.example.journeygenius.components

import android.annotation.SuppressLint
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.google.accompanist.permissions.*
import android.content.Context
import android.location.LocationManager
import androidx.compose.ui.platform.LocalContext
import com.example.journeygenius.JourneyGeniusViewModel
import com.example.journeygenius.data.models.Location

@ExperimentalPermissionsApi
@Composable
fun RequestPermission(
    permission: String,
    rationaleMessage: String = "To use this app's functionalities, you need to give us the permission.",
    viewModel: JourneyGeniusViewModel
) {
    val permissionState = rememberPermissionState(permission)

    HandleRequest(
        permissionState = permissionState,
        deniedContent = { shouldShowRationale ->
            PermissionDeniedContent(
                rationaleMessage = rationaleMessage,
                shouldShowRationale = shouldShowRationale
            ) {
                permissionState.launchPermissionRequest()
            }
        },
        content = {
            /*   Content(
                   text = "PERMISSION GRANTED!",
                   showButton = false
               ) {}*/
        },
        viewModel
    )
}

@SuppressLint("MissingPermission")
@ExperimentalPermissionsApi
@Composable
fun HandleRequest(
    permissionState: PermissionState,
    deniedContent: @Composable (Boolean) -> Unit,
    content: @Composable () -> Unit,
    viewModel:JourneyGeniusViewModel
) {
    when (permissionState.status) {
        is PermissionStatus.Granted -> {
            val locationManager = LocalContext.current.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val updateLoc=Location(location?.latitude ?:42.351066968829144,location?.longitude ?:-71.10550779862372)
            viewModel.updateDepartLocation(updateLoc)
        }
        is PermissionStatus.Denied -> {
            deniedContent(permissionState.status.shouldShowRationale)
        }
    }
}

@Composable
fun Content(showButton: Boolean = true, onClick: () -> Unit) {
    if (showButton) {
        val enableLocation = remember { mutableStateOf(true) }
        if (enableLocation.value) {
            CustomDialogLocation(
                title = "Turn On Location Service",
                desc = "Explore the world without getting lost and keep the track of your location.\n\nGive this app a permission to proceed. If it doesn't work, then you'll have to do it manually from the settings.",
                enableLocation,
                onClick
            )
        }
    }
}

@ExperimentalPermissionsApi
@Composable
fun PermissionDeniedContent(
    rationaleMessage: String,
    shouldShowRationale: Boolean,
    onRequestPermission: () -> Unit
) {

    if (shouldShowRationale) {

        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(
                    text = "Permission Request",
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Text(rationaleMessage)
            },
            confirmButton = {
                Button(onClick = onRequestPermission) {
                    Text("Give Permission")
                }
            }
        )

    }
    else {
        Content(onClick = onRequestPermission)
    }

}