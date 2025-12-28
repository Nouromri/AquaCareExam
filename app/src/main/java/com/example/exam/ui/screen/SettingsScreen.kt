package com.example.exam.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.exam.R
import com.example.exam.data.entity.UserProfile
import com.example.exam.notification.WaterReminderWorker
import com.example.exam.viewmodel.WaterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: WaterViewModel) {
    val userProfile by viewModel.userProfile.collectAsState()
    val context = LocalContext.current
    
    var showPersonalInfoDialog by remember { mutableStateOf(false) }
    var showGoalDialog by remember { mutableStateOf(false) }
    var showCupDialog by remember { mutableStateOf(false) }
    var showScheduleDialog by remember { mutableStateOf(false) }
    var showIntervalDialog by remember { mutableStateOf(false) }

    // Sync WorkManager when profile changes
    LaunchedEffect(userProfile) {
        userProfile?.let { profile ->
            if (profile.notificationsEnabled) {
                WaterReminderWorker.scheduleReminder(context, profile.notificationIntervalMinutes)
            } else {
                WaterReminderWorker.cancelReminder(context)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Settings", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(R.drawable.logo1),
                            contentDescription = null,
                            tint = Color(0xFF42A5F5),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                    navigationIconContentColor = Color.Unspecified,
                    titleContentColor = Color.Unspecified,
                    actionIconContentColor = Color.Unspecified
                ),
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                SettingsSection(title = "General Settings") {
                    userProfile?.let { profile ->
                        SettingsItem(
                            icon = Icons.Default.Person,
                            title = "Personal Info",
                            subtitle = "${profile.gender}, ${profile.age} years",
                            onClick = { showPersonalInfoDialog = true }
                        )
                        SettingsDivider()
                        SettingsItem(
                            icon = painterResource(R.drawable.cup),
                            title = "Daily Goal",
                            subtitle = "${profile.dailyGoalInMl} mL",
                            onClick = { showGoalDialog = true }
                        )
                        SettingsDivider()
                        SettingsItem(
                            icon = painterResource(R.drawable.cup),
                            title = "Cup Size",
                            subtitle = "${profile.cupSize} mL",
                            onClick = { showCupDialog = true }
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            item {
                SettingsSection(title = "Reminders") {
                    userProfile?.let { profile ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(36.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF8F9FA)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Notifications, contentDescription = null, tint = Color(0xFF42A5F5), modifier = Modifier.size(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Notifications", fontWeight = FontWeight.Medium, fontSize = 16.sp, color = Color.DarkGray)
                                Text(text = if (profile.notificationsEnabled) "On" else "Off", color = Color.LightGray, fontSize = 12.sp)
                            }
                            Switch(
                                checked = profile.notificationsEnabled,
                                onCheckedChange = { viewModel.updateProfile(profile.copy(notificationsEnabled = it)) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF42A5F5))
                            )
                        }
                        SettingsDivider()
                        SettingsItem(
                            icon = Icons.Default.Settings,
                            title = "Interval",
                            subtitle = "Every ${profile.notificationIntervalMinutes} minutes",
                            onClick = { showIntervalDialog = true }
                        )
                        SettingsDivider()
                        SettingsItem(
                            icon = painterResource(R.drawable.time),
                            title = "Schedule",
                            subtitle = "${profile.wakeTime} - ${profile.sleepTime}",
                            onClick = { showScheduleDialog = true }
                        )
                    }
                }
            }

            
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }

    // Dialogs
    if (showPersonalInfoDialog && userProfile != null) {
        PersonalInfoDialog(
            profile = userProfile!!,
            onDismiss = { showPersonalInfoDialog = false },
            onConfirm = { updatedProfile ->
                viewModel.updateProfile(updatedProfile)
                showPersonalInfoDialog = false
            }
        )
    }

    if (showGoalDialog && userProfile != null) {
        GoalDialog(
            currentGoal = userProfile!!.dailyGoalInMl,
            onDismiss = { showGoalDialog = false },
            onConfirm = { newGoal ->
                viewModel.updateProfile(userProfile!!.copy(dailyGoalInMl = newGoal))
                showGoalDialog = false
            }
        )
    }

    if (showCupDialog && userProfile != null) {
        CupSizeDialog(
            currentSize = userProfile!!.cupSize,
            onDismiss = { showCupDialog = false },
            onConfirm = { newSize: Int ->
                viewModel.updateProfile(userProfile!!.copy(cupSize = newSize))
                showCupDialog = false
            }
        )
    }

    if (showScheduleDialog && userProfile != null) {
        ScheduleDialog(
            profile = userProfile!!,
            onDismiss = { showScheduleDialog = false },
            onConfirm = { wake, sleep ->
                viewModel.updateProfile(userProfile!!.copy(wakeTime = wake, sleepTime = sleep))
                showScheduleDialog = false
            }
        )
    }

    if (showIntervalDialog && userProfile != null) {
        IntervalDialog(
            currentInterval = userProfile!!.notificationIntervalMinutes,
            onDismiss = { showIntervalDialog = false },
            onConfirm = { newInterval ->
                viewModel.updateProfile(userProfile!!.copy(notificationIntervalMinutes = newInterval))
                showIntervalDialog = false
            }
        )
    }
}

@Composable
fun IntervalDialog(currentInterval: Int, onDismiss: () -> Unit, onConfirm: (Int) -> Unit) {
    var interval by remember { mutableStateOf(currentInterval.toString()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = Color.White,
        icon = { Icon(Icons.Default.Notifications, contentDescription = null, tint = Color(0xFF42A5F5)) },
        title = {
            Text(
                text = "Reminder Interval",
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
                    text = "How often should we remind you to drink water?",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                OutlinedTextField(
                    value = interval,
                    onValueChange = { if (it.all { c -> c.isDigit() }) interval = it },
                    label = { Text("Interval (minutes)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(interval.toIntOrNull()?.coerceAtLeast(15) ?: currentInterval) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF42A5F5)),
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            ) {
                Text("Save Changes", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}

@Composable
fun PersonalInfoDialog(
    profile: UserProfile,
    onDismiss: () -> Unit,
    onConfirm: (UserProfile) -> Unit
) {
    var gender by remember { mutableStateOf(profile.gender) }
    var age by remember { mutableStateOf(profile.age.toString()) }
    var weight by remember { mutableStateOf(profile.weight.toString()) }
    var height by remember { mutableStateOf(profile.height.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = Color.White,
        icon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF42A5F5)) },
        title = {
            Text(
                text = "Personal Info",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Update your profile details for more accurate tracking.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    FilterChip(
                        selected = gender == "Male",
                        onClick = { gender = "Male" },
                        label = { Text("Male") }
                    )
                    FilterChip(
                        selected = gender == "Female",
                        onClick = { gender = "Female" },
                        label = { Text("Female") }
                    )
                }
                OutlinedTextField(
                    value = age,
                    onValueChange = { if (it.all { c -> c.isDigit() }) age = it },
                    label = { Text("Age") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = weight,
                    onValueChange = { if (it.all { c -> c.isDigit() }) weight = it },
                    label = { Text("Weight (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = height,
                    onValueChange = { if (it.all { c -> c.isDigit() }) height = it },
                    label = { Text("Height (cm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        profile.copy(
                            gender = gender,
                            age = age.toIntOrNull() ?: profile.age,
                            weight = weight.toIntOrNull() ?: profile.weight,
                            height = height.toIntOrNull() ?: profile.height
                        )
                    )
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
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}

@Composable
fun GoalDialog(currentGoal: Int, onDismiss: () -> Unit, onConfirm: (Int) -> Unit) {
    var goal by remember { mutableStateOf(currentGoal.toString()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = Color.White,
        icon = { Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF42A5F5)) },
        title = {
            Text(
                text = "Daily Goal",
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
                    text = "Customize your target hydration. (Max 10,000 mL)",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                OutlinedTextField(
                    value = goal,
                    onValueChange = { 
                        val filtered = it.filter { c -> c.isDigit() }
                        if (filtered.isEmpty() || filtered.toLong() <= 10000) {
                            goal = filtered
                        }
                    },
                    label = { Text("Goal (mL)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalGoal = goal.toIntOrNull()?.coerceAtMost(10000) ?: currentGoal
                    onConfirm(finalGoal)
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
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}

@Composable
fun CupSizeDialog(currentSize: Int, onDismiss: () -> Unit, onConfirm: (Int) -> Unit) {
    var size by remember { mutableStateOf(currentSize.toString()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = Color.White,
        icon = { Icon(painterResource(R.drawable.cup), contentDescription = null, tint = Color(0xFF42A5F5), modifier = Modifier.size(24.dp)) },
        title = {
            Text(
                text = "Cup Size",
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
                    text = "Set your default cup size for quick logging.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                OutlinedTextField(
                    value = size,
                    onValueChange = { if (it.all { c -> c.isDigit() }) size = it },
                    label = { Text("Cup Size (mL)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(size.toIntOrNull() ?: currentSize) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF42A5F5)),
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            ) {
                Text("Save Changes", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}

@Composable
fun ScheduleDialog(profile: UserProfile, onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var wake by remember { mutableStateOf(profile.wakeTime) }
    var sleep by remember { mutableStateOf(profile.sleepTime) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = Color.White,
        icon = { Icon(Icons.Default.Notifications, contentDescription = null, tint = Color(0xFF42A5F5)) },
        title = {
            Text(
                text = "Edit Schedule",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Adjust your active hours for reminders.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = wake,
                    onValueChange = { wake = it },
                    label = { Text("Wake up time (HH:mm)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = sleep,
                    onValueChange = { sleep = it },
                    label = { Text("Sleep time (HH:mm)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(wake, sleep) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF42A5F5)),
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            ) {
                Text("Save Changes", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
        ) {
            Column(content = content)
        }
    }
}

@Composable
fun SettingsItem(
    icon: Any,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(36.dp),
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFF8F9FA)
        ) {
            Box(contentAlignment = Alignment.Center) {
                when (icon) {
                    is ImageVector -> Icon(icon, contentDescription = null, tint = Color(0xFF42A5F5), modifier = Modifier.size(20.dp))
                    is androidx.compose.ui.graphics.painter.Painter -> Icon(icon, contentDescription = null, tint = Color(0xFF42A5F5), modifier = Modifier.size(20.dp))
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Medium, fontSize = 16.sp, color = Color.DarkGray)
            if (subtitle != null) {
                Text(text = subtitle, color = Color.LightGray, fontSize = 12.sp)
            }
        }

        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.LightGray
        )
    }
}

@Composable
fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        thickness = 0.5.dp,
        color = Color(0xFFF1F3F5)
    )
}
