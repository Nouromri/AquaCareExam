package com.example.exam.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.exam.R



import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip

import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign

import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntroScreen(onProfileSaved: (String, Int, Int, Int, Int, String, String) -> Unit = { _, _, _, _, _, _, _ -> }) {
    var currentStep by remember { mutableStateOf(0) }

    // tabdi min step 0 i step 1
    if (currentStep == 0) {
        LaunchedEffect(Unit) {
            delay(2000)
            currentStep = 1
        }
    }

    // Form State
    var gender by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("25") }
    var weight by remember { mutableStateOf("70") }
    var height by remember { mutableStateOf("185") }
    var sleepTime by remember { mutableStateOf("22:00") }
    var wakeTime by remember { mutableStateOf("07:00") }
    var dailyGoal by remember { mutableStateOf("") }

    val steps = 7

    // Calculate goal when weight changes
    LaunchedEffect(weight) {
        val w = weight.toIntOrNull()
        if (w != null && w > 0) {
            // Cap at 10,000 mL
            val calculatedGoal = (w * 33).coerceAtMost(10000)
            dailyGoal = calculatedGoal.toString()
        }
    }

    Scaffold(
        topBar = {
            if (currentStep > 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { if (currentStep > 1) currentStep-- }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }

                    LinearProgressIndicator(
                        progress = { currentStep.toFloat() / (steps - 1) },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF42A5F5),
                        trackColor = Color(0xFFEEEEEE)
                    )

                    Text(
                        text = "$currentStep / ${steps - 1}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        bottomBar = {
            if (currentStep > 0) {
                Box(modifier = Modifier.padding(24.dp)) {
                    Button(
                        onClick = {
                            if (currentStep < steps - 1) {
                                currentStep++
                            } else {
                                onProfileSaved(
                                    gender,
                                    age.toIntOrNull() ?: 0,
                                    weight.toIntOrNull() ?: 0,
                                    height.toIntOrNull() ?: 0,
                                    dailyGoal.toIntOrNull() ?: 2000,
                                    sleepTime,
                                    wakeTime
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = isStepValid(currentStep, gender, age, weight, height),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF42A5F5))
                    ) {
                        Text(if (currentStep == steps - 1) "Let's Hydrate!" else "Continue", fontSize = 18.sp)
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .background(color = if (currentStep == 0) Color(0xFF42A5F5) else Color.White)
                .padding(if (currentStep > 0) padding else PaddingValues(0.dp))
                .fillMaxSize()
        ) {
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally { it } + fadeIn() togetherWith
                                slideOutHorizontally { -it } + fadeOut()
                    } else {
                        slideInHorizontally { -it } + fadeIn() togetherWith
                                slideOutHorizontally { it } + fadeOut()
                    }
                }, label = "OnboardingTransition"
            ) { step ->
                if (step == 0) {
                    WelcomeStep()
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        when (step) {
                            1 -> GenderStep(gender) { gender = it }
                            2 -> WeightStep(gender, weight) { weight = it }
                            3 -> HeightStep(gender, height) { height = it }
                            4 -> TimeScrollStep("When do you go to sleep?", "Sleep time is important for your health and hydration cycle.", sleepTime) { sleepTime = it }
                            5 -> TimeScrollStep("When do you wake up?", "Setting your wake up time helps us schedule your hydration reminders.", wakeTime) { wakeTime = it }
                            6 -> GoalStep(dailyGoal) { dailyGoal = it }
                        }
                    }
                }
            }
        }
    }
}

private fun isStepValid(step: Int, gender: String, age: String, weight: String, height: String): Boolean {
    return when (step) {
        0 -> true
        1 -> gender.isNotBlank()
        2 -> weight.isNotBlank()
        3 -> height.isNotBlank()
        else -> true
    }
}

@Composable
fun WelcomeStep() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(id = R.drawable.logo1),
            contentDescription = null,
            modifier = Modifier.size(140.dp),
            colorFilter = ColorFilter.tint(Color.White)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "AquaCare",
            style = MaterialTheme.typography.displayMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.weight(1f))
        CircularProgressIndicator(
            color = Color.White,
            strokeWidth = 4.dp,
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.height(64.dp))
    }
}

@Composable
fun GenderStep(selected: String, onSelected: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "What's your gender?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "AquaCare is here to tailor a hydration plan just for you! Let's kick things off by getting to know you better.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(60.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            GenderOption(
                title = "Male",
                icon = painterResource(R.drawable.male),
                isSelected = selected == "Male",
                onClick = { onSelected("Male") }
            )
            GenderOption(
                title = "Female",
                icon = painterResource(R.drawable.female),
                isSelected = selected == "Female",
                onClick = { onSelected("Female") }
            )
        }
    }
}

@Composable
fun GenderOption(title: String, icon: Painter, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(if (isSelected) Color(0xFF42A5F5) else Color.White)
                .border(1.dp, if (isSelected) Color(0xFF42A5F5) else Color(0xFFEEEEEE), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = icon,
                contentDescription = title,
                modifier = Modifier.size(60.dp),
                tint = if (isSelected) Color.White else Color(0xFF42A5F5)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = if (isSelected) Color(0xFF42A5F5) else Color.DarkGray,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun WeightStep(gender: String, weight: String, onWeightChange: (String) -> Unit) {
    MeasurementStep(
        title = "your current weight?",
        description = " Please enter your current weight:",
        value = weight,
        onValueChange = onWeightChange,
        unit = "kg",
        range = 30..200,
        gender = gender
    )
}

@Composable
fun HeightStep(gender: String, height: String, onHeightChange: (String) -> Unit) {
    MeasurementStep(
        title = "How tall are you?",
        description = "Choose your height measurement:",
        value = height,
        onValueChange = onHeightChange,
        unit = "cm",
        range = 100..250,
        gender = gender
    )
}

@Composable
fun MeasurementStep(
    title: String,
    description: String,
    value: String,
    onValueChange: (String) -> Unit,
    unit: String,
    range: IntRange,
    gender: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            description,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Body Image Placeholder
            Image(
                painter = if(gender == "Male") painterResource(id = R.drawable.boy) else painterResource(id = R.drawable.girl),
                contentDescription = null,
                modifier = Modifier.weight(1f).height(300.dp)
            )

            // Picker
            Box(modifier = Modifier.weight(1f).height(300.dp), contentAlignment = Alignment.Center) {
                VerticalNumberPicker(
                    value = value,
                    onValueChange = onValueChange,
                    range = range,
                    unit = unit
                )
            }
        }
    }
}

@Composable
fun VerticalNumberPicker(
    value: String,
    onValueChange: (String) -> Unit,
    range: IntRange,
    unit: String = "",
    itemHeight: Int = 50
) {
    val initialIndex = ((value.toIntOrNull() ?: range.first) - range.first).coerceIn(0, range.last - range.first)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = 110.dp)
        ) {
            items(range.toList()) { item ->
                val isSelected = item.toString() == value
                Text(
                    text = if (isSelected && unit.isNotEmpty()) "$item $unit" else item.toString().padStart(2, '0'),
                    style = if (isSelected) MaterialTheme.typography.displaySmall else MaterialTheme.typography.headlineMedium,
                    color = if (isSelected) Color(0xFF42A5F5) else Color.LightGray,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .clickable { onValueChange(item.toString()) }
                )
            }
        }

        // Update value when scrolling
        LaunchedEffect(listState.firstVisibleItemIndex) {
            val index = listState.firstVisibleItemIndex
            if (index >= 0 && index < range.count()) {
                onValueChange((range.first + index).toString())
            }
        }
    }
}

@Composable
fun TimeScrollStep(
    title: String = "",
    description: String ="",
    time: String ="22:00",
    onTimeChange: (String) -> Unit = {}
) {
    val initialHour = time.split(":")[0].toInt()
    val initialMinute = time.split(":")[1].toInt()

    var hour by remember { mutableStateOf(initialHour.toString()) }
    var minute by remember { mutableStateOf(initialMinute.toString()) }

    LaunchedEffect(hour, minute) {
        val h = hour.padStart(2, '0')
        val m = minute.padStart(2, '0')
        onTimeChange("$h:$m")
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            description,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().height(300.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                VerticalNumberPicker(
                    value = hour,
                    onValueChange = { hour = it },
                    range = 0..23
                )
            }

            Text(
                ":",
                style = MaterialTheme.typography.displaySmall,
                color = Color(0xFF42A5F5),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Box(modifier = Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                VerticalNumberPicker(
                    value = minute,
                    onValueChange = { minute = it },
                    range = 0..59
                )
            }
        }
    }
}

@Composable
fun GoalStep(goal: String, onGoalChange: (String) -> Unit) {
    var showAdjustDialog by remember { mutableStateOf(false) }
    var tempGoal by remember(goal) { mutableStateOf(goal) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Your daily goal is",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(60.dp))

        Image(
            painter = painterResource(id = R.drawable.logo1),
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            colorFilter = ColorFilter.tint(Color(0xFF42A5F5))
        )

        Spacer(modifier = Modifier.height(40.dp))

        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = goal,
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "mL",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = { showAdjustDialog = true },
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.height(48.dp),
            border = BorderStroke(1.dp, Color(0xFF42A5F5))
        ) {
            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color(0xFF42A5F5))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Adjust", fontWeight = FontWeight.Medium, color = Color(0xFF42A5F5))
        }
    }

    if (showAdjustDialog) {
        AlertDialog(
            onDismissRequest = { showAdjustDialog = false },
            shape = RoundedCornerShape(28.dp),
            containerColor = Color.White,
            icon = { Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF42A5F5)) },
            title = {
                Text(
                    text = "Adjust Daily Goal",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Customize your target hydration for better results. (Max 10,000 mL)",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )
                    OutlinedTextField(
                        value = tempGoal,
                        onValueChange = { 
                            val filtered = it.filter { c -> c.isDigit() }
                            if (filtered.isEmpty() || filtered.toLong() <= 10000) {
                                tempGoal = filtered
                            }
                        },
                        label = { Text("Daily Goal (mL)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF42A5F5),
                            focusedLabelColor = Color(0xFF42A5F5),
                            unfocusedBorderColor = Color.LightGray
                        ),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalGoal = tempGoal.toIntOrNull()?.coerceAtMost(10000) ?: 2000
                        onGoalChange(finalGoal.toString())
                        showAdjustDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF42A5F5)),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAdjustDialog = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}
