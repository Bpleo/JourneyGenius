package com.example.journeygenius

import android.graphics.Paint.Style
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.journeygenius.ui.theme.JourneyGeniusTheme
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanScreen(
) {
    val selectedDateRange = remember {
        val value = Range(LocalDate.now().minusDays(3), LocalDate.now())
        mutableStateOf(value)
    }
    var budget by remember { mutableStateOf(TextFieldValue()) }
    val calenderState = rememberUseCaseState()
    CalendarDialog(
        state = calenderState,
        config = CalendarConfig(
            style = CalendarStyle.MONTH,
        ),
        selection = CalendarSelection.Period(
            selectedRange = selectedDateRange.value
        ) { startDate, endDate ->
            selectedDateRange.value = Range(startDate, endDate)
        }
    )
    val sdf = DateTimeFormatter.ofPattern("MM-dd-yyyy")
    JourneyGeniusTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.padding(32.dp,64.dp)
            ) {
                Text(
                    text = "Travel Date: ",
                    fontSize = MaterialTheme.typography.headlineLarge.fontSize
                )
                Spacer(modifier = Modifier.height(40.dp))
                Row {
                    Column {
                        Text(
                            text = "Start Date: ",
                            fontSize = MaterialTheme.typography.titleLarge.fontSize
                        )
                        Spacer(modifier = Modifier.height(40.dp))
                        Text(
                            text = "End Date: ",
                            fontSize = MaterialTheme.typography.titleLarge.fontSize
                        )
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Column {
                        Row(
                            modifier = Modifier
                                .background(Color.LightGray)
                                .border(BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface))
                        )
                         {
                            ClickableText(
                                text = AnnotatedString(
                                    selectedDateRange.component1().lower.format(
                                        sdf
                                    ), spanStyle = SpanStyle(fontSize = 34.sp)
                                ),
                                onClick = {
                                    calenderState.show()
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(23.dp))
                        Row(
                            modifier = Modifier
                                .background(Color.LightGray)
                                .border(BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface))
                        ) {
                            ClickableText(
                                text = AnnotatedString(
                                    selectedDateRange.component1().upper.format(
                                        sdf
                                    ), spanStyle = SpanStyle(fontSize = 34.sp)
                                ),
                                onClick = {
                                    calenderState.show()
                                })
                        }
                    }
                }
                Spacer(modifier = Modifier.height(60.dp))
                Text(
                    text = "Travel Type: ",
                    fontSize = MaterialTheme.typography.headlineLarge.fontSize
                )
                Spacer(modifier = Modifier.height(40.dp))
                Row {
                    Button(onClick = {}) {
                        Text(text = "SINGLE")
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(onClick = {}) {
                        Text(text = "FAMILY")
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(onClick = {}) {
                        Text(text = "BUSINESS")
                    }
                }
                Spacer(modifier = Modifier.height(60.dp))
                Text(
                    text = "What is your budget: ",
                    fontSize = MaterialTheme.typography.headlineLarge.fontSize
                )
                Spacer(modifier = Modifier.height(15.dp))
                TextField(
                    value = budget,
                    onValueChange = {newText ->
                        budget = newText
                    },
                    placeholder = {
                        Text("Enter your budget")
                    },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction =  ImeAction.Done
                    )
                )
                Spacer(modifier = Modifier.height(60.dp))
                Column(
                    modifier = Modifier.fillMaxSize()
                ){
                    Button(onClick = { /*TODO*/ }, modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)) {
                        Text(text = "CHOOSE YOUR DESTINATION")
                    }
                }
            }

        }
    }
}


@Preview(showBackground = true)
@Composable
fun PlanScreenPreview() {
    PlanScreen()
}