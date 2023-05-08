package com.example.journeygenius.personal


import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.journeygenius.JourneyGeniusViewModel
import com.example.journeygenius.R
import com.example.journeygenius.ui.theme.JourneyGeniusTheme
import com.google.android.gms.maps.model.LatLng
import java.util.*


@Composable
fun PersonalSettingScreen(
    viewModel: JourneyGeniusViewModel,
    navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        JourneyGeniusTheme {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PersonalTopBar(context = LocalContext.current.applicationContext, title = stringResource(R.string.settings), navController = navController)
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        LanguageField(viewModel = viewModel)
                    }
                }
            }
        }
    }

}
@Composable
fun LanguageField(viewModel: JourneyGeniusViewModel) {
    var languageExpanded by remember { mutableStateOf(false) }

    val iconLanguage = if (languageExpanded) {
        Icons.Filled.KeyboardArrowUp
    } else {
        Icons.Filled.KeyboardArrowDown
    }

    val language by remember {
        mutableStateOf(viewModel.language)
    }

    val languageList by remember {
        viewModel.languageList
    }
    val context = LocalContext.current


    Column(){

        Row{
            Text(
                text = stringResource(R.string.language),
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                color = MaterialTheme.colorScheme.primary,
            )
            Box {
                languageList[language.value]?.let {
                    OutlinedTextField(value = it,
                        onValueChange = { viewModel.updateLanguage(it) },
                        modifier = Modifier
                            .width(150.dp)
                            .height(60.dp),
                        label = { Text(text = stringResource(id = R.string.language)) },
                        trailingIcon = {
                            Icon(
                                iconLanguage,
                                contentDescription = "",
                                Modifier.clickable {
                                    languageExpanded = !languageExpanded
                                })
                        })
                }
                DropdownMenu(
                    expanded = languageExpanded,
                    onDismissRequest = { languageExpanded = false },
                    modifier = Modifier
                        .width(150.dp)
                        .height(200.dp),
                ) {
                    languageList.forEach { language ->
                        DropdownMenuItem(text = { Text(language.value) }, onClick = {
                            viewModel.updateLanguage(language.key)
                            languageExpanded = false

                            val config = context.resources.configuration
                            when(language.key) {
                                "en"-> {
                                    Locale.setDefault(Locale.ENGLISH)
                                    config.setLocale(Locale.ENGLISH)
                                }
                                "es" -> {
                                    Locale.setDefault(Locale("es"))
                                    config.setLocale(Locale("es"))
                                }
                                "zh" -> {
                                    Locale.setDefault(Locale.SIMPLIFIED_CHINESE)
                                    config.setLocale(Locale.SIMPLIFIED_CHINESE)
                                }
                            }
                            context.resources.updateConfiguration(config, context.resources.displayMetrics)
                            Log.d("Data", "Current Language "+Locale.getDefault().toString())
                        })
                    }

                }
            }
        }
    }
}


