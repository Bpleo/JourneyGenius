package com.example.journeygenius.plan

import android.util.Range
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.journeygenius.WindowSize
import com.example.journeygenius.WindowType
import com.example.journeygenius.JourneyGeniusViewModel
import com.example.journeygenius.rememberWindowSize
import com.example.journeygenius.ui.theme.JourneyGeniusTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.maxkeppeker.sheets.core.models.base.UseCaseState
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun PlanScreenGraph(
    viewModel: JourneyGeniusViewModel,
    windowSize: WindowSize,
    planViewModel: PlanViewModel
){
    val planNavController= rememberNavController()
    NavHost(navController = planNavController,
        startDestination = "Plan Menu"){
        composable("Plan Menu"){
            PlanScreen(viewModel,windowSize,planNavController)
        }
        composable("Plan Map"){
            PlanChooseLocScreen(viewModel = planViewModel,planNavController,viewModel)
        }
        composable("Plan List"){
            PlanList(navController = planNavController, planViewModel = planViewModel)
        }
        composable("Plan Detail"){
            PlanDetail(planNavController,planViewModel)
        }
    }
}

@Composable
fun TravelDateComponent(
    selectedDateRange: MutableState<MutableState<Range<LocalDate>>>,
    calenderState: UseCaseState,
    windowSize: WindowSize
) {
    val sdf = DateTimeFormatter.ofPattern("MM-dd-yyyy")
    Column {
        when(windowSize.height){
            WindowType.Medium -> {
                Text(
                    text = "Travel Date: ",
                    fontSize = MaterialTheme.typography.headlineLarge.fontSize
                )
            }
            else -> {
                Text(
                    text = "Travel Date: ",
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row {
            Column {
                when(windowSize.height){
                    WindowType.Medium -> {
                        Text(
                            text = "Start Date: ",
                            fontSize = MaterialTheme.typography.titleLarge.fontSize
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "End Date: ",
                            fontSize = MaterialTheme.typography.titleLarge.fontSize
                        )
                    }
                    else -> {
                        Text(
                            text = "Start Date: ",
                            fontSize = MaterialTheme.typography.titleSmall.fontSize
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "End Date: ",
                            fontSize = MaterialTheme.typography.titleSmall.fontSize
                        )
                    }
                }

            }
            Column {
                Row(
                    modifier = Modifier
                        .background(Color.LightGray)
                        .border(BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface))
                )
                {
                    when(windowSize.height) {
                        WindowType.Medium -> {
                            ClickableText(
                                text = AnnotatedString(
                                    selectedDateRange.component1().component1().lower.format(
                                        sdf
                                    ), spanStyle = SpanStyle(fontSize = 34.sp)
                                ),
                                onClick = {
                                    calenderState.show()
                                }
                            )
                        }
                        else -> {
                            ClickableText(
                                text = AnnotatedString(
                                    selectedDateRange.component1().component1().lower.format(
                                        sdf
                                    ), spanStyle = SpanStyle(fontSize = 17.sp)
                                ),
                                onClick = {
                                    calenderState.show()
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(13.dp))
                Row(
                    modifier = Modifier
                        .background(Color.LightGray)
                        .border(BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface))
                ) {
                    when(windowSize.height) {
                        WindowType.Medium -> {
                            ClickableText(
                                text = AnnotatedString(
                                    selectedDateRange.component1().component1().upper.format(
                                        sdf
                                    ), spanStyle = SpanStyle(fontSize = 34.sp)
                                ),
                                onClick = {
                                    calenderState.show()
                                }
                            )
                        }
                        else -> {
                            ClickableText(
                                text = AnnotatedString(
                                    selectedDateRange.component1().component1().upper.format(
                                        sdf
                                    ), spanStyle = SpanStyle(fontSize = 17.sp)
                                ),
                                onClick = {
                                    calenderState.show()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun BudgetComponent(viewModel: JourneyGeniusViewModel){
    Column {
        Text(
            text = "What is your budget: ",
            fontSize = MaterialTheme.typography.headlineLarge.fontSize
        )
        Spacer(modifier = Modifier.height(15.dp))
        val sliderValue: Int by viewModel.sliderValue.observeAsState(0)
        val sliderLabel: String by viewModel.sliderLabel.observeAsState("cheap")

        Column {
            Slider(
                value = sliderValue.toFloat(),
                onValueChange = { viewModel.onSliderValueChanged(it.toInt()) },
                valueRange = 0f..30f,
                steps = 2,
                modifier = Modifier.width(200.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = sliderLabel, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun DestinationButton(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            onClick = {
                navController.navigate("Plan Map")
            }, modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        ) {
            Text(text = "CHOOSE YOUR ROUTE")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanScreen(
    viewModel: JourneyGeniusViewModel,
    windowSize: WindowSize,
    navController: NavController
) {

    val selectedDateRange = remember {
        val value = viewModel.dateRange
        mutableStateOf(value)
    }
    val budget by remember { mutableStateOf(viewModel.budget) }
    val travelType by remember {
        mutableStateOf(viewModel.travelType)
    }
    val calenderState = rememberUseCaseState()
    CalendarDialog(
        state = calenderState,
        config = CalendarConfig(
            yearSelection = true,
            monthSelection = true,
            style = CalendarStyle.MONTH,
        ),
        selection = CalendarSelection.Period(
            selectedRange = selectedDateRange.component1().value
        ) { startDate, endDate ->
            viewModel.updateRange(startDate, endDate)
        }
    )
    JourneyGeniusTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when(windowSize.height){
                WindowType.Medium -> {
                    Column(
                        modifier = Modifier.padding(32.dp,64.dp)
                    ) {
                        TravelDateComponent(selectedDateRange, calenderState, windowSize)
                        Spacer(modifier = Modifier.height(60.dp))
                        BudgetComponent(viewModel = viewModel)
                        Spacer(modifier = Modifier.height(60.dp))
                        DestinationButton(navController)
                    }
                }
                else -> {
                    Row(
                        modifier = Modifier.padding(64.dp, 32.dp)
                    ){
                        Column {
                            TravelDateComponent(selectedDateRange, calenderState, windowSize)
                        }
                        Spacer(modifier = Modifier.width(60.dp))
                        Column{
                            BudgetComponent(viewModel = viewModel)
                            Spacer(modifier = Modifier.height(60.dp))
                            DestinationButton(navController)
                        }
                    }
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlanScreenPreview() {
    PlanScreen(JourneyGeniusViewModel(Firebase.firestore, Firebase.auth), rememberWindowSize(),
        rememberNavController())
}

@Preview@Preview(showBackground = true, device = Devices.AUTOMOTIVE_1024p, heightDp = 320)
@Composable
fun PlanScreenLandscapePreview() {
    val viewModel = JourneyGeniusViewModel(Firebase.firestore, Firebase.auth)
    val selectedDateRange = remember {
        val value = viewModel.dateRange
        mutableStateOf(value)
    }
    val budget by remember { mutableStateOf(viewModel.budget) }
    val calenderState = rememberUseCaseState()
    JourneyGeniusTheme {
        Box {
            Row(
                modifier = Modifier.padding(64.dp, 32.dp)
            ) {
                Column {
                    TravelDateComponent(selectedDateRange, calenderState, rememberWindowSize())
                }
                Spacer(modifier = Modifier.width(60.dp))
                Column{
                    BudgetComponent(viewModel = viewModel)
                    Spacer(modifier = Modifier.height(60.dp))
                    DestinationButton(rememberNavController())
                }
            }
        }
    }
}