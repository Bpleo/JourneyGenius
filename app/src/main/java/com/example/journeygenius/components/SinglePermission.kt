package com.example.journeygenius.components

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.accompanist.permissions.*
import android.content.Context
import android.location.LocationManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.journeygenius.JourneyGeniusViewModel
import com.example.journeygenius.R
import com.example.journeygenius.data.models.Location


//a component used to request location information
@ExperimentalPermissionsApi
@Composable
fun RequestPermission(
    permission: String,
    rationaleMessage: String = stringResource(R.string.to_use_app_func),
    viewModel: JourneyGeniusViewModel
) {
    val permissionState = rememberPermissionState(permission)

    HandleRequest(
        permissionState = permissionState,
        deniedContent = {
            Content(onClick = { permissionState.launchPermissionRequest() })
        },
        viewModel
    )
}
//handle different status
@SuppressLint("MissingPermission")
@ExperimentalPermissionsApi
@Composable
fun HandleRequest(
    permissionState: PermissionState,
    deniedContent: @Composable (Boolean) -> Unit,
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
//a UI for request
@Composable
fun Content(showButton: Boolean = true, onClick: () -> Unit) {
    if (showButton) {
        val enableLocation = remember { mutableStateOf(true) }
        if (enableLocation.value) {
            CustomDialogLocation(
                title = stringResource(R.string.turn_on_loc_service),
                desc = stringResource(R.string.explore_the_world)+"\n\n"+ stringResource(R.string.give_app_permission),
                enableLocation,
                onClick
            )
        }
    }
}

//@ExperimentalPermissionsApi
//@Composable
//fun PermissionDeniedContent(
//    rationaleMessage: String,
//    shouldShowRationale: Boolean,
//    onRequestPermission: () -> Unit
//) {
//
//    if (shouldShowRationale) {
//
//        AlertDialog(
//            onDismissRequest = {},
//            title = {
//                Text(
//                    text = stringResource(R.string.permission_request),
//                    style = TextStyle(
//                        fontSize = MaterialTheme.typography.headlineLarge.fontSize,
//                        fontWeight = FontWeight.Bold
//                    )
//                )
//            },
//            text = {
//                Text(rationaleMessage)
//            },
//            confirmButton = {
//                Button(onClick = onRequestPermission) {
//                    Text(stringResource(R.string.give_permission))
//                }
//            }
//        )
//
//    }
//    else {
//        Content(onClick = onRequestPermission)
//    }
//
//}