package com.example.exam.ui.screen


import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.exam.R
import com.example.exam.data.entity.WaterLog
import com.example.exam.ui.Screen
import com.example.exam.viewmodel.WaterViewModel
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterTrackerHomeScreen(viewModel: WaterViewModel, navController: NavController) {
    val totalIntake by viewModel.todayTotal.collectAsState()
    val history by viewModel.allLogs.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val weather by viewModel.weather.collectAsState()
    val dailyGoal = viewModel.dailyGoal
    val progress = (totalIntake.toFloat() / dailyGoal).coerceIn(0f, 1f)

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var showCupDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    viewModel.fetchWeather(it.latitude, it.longitude)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Home", fontWeight = FontWeight.Bold, fontSize = 20.sp)
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
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Weather Card
            item {
                weather?.let { w ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF1976D2))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Current Weather",
                                    fontSize = 12.sp,
                                    color = Color(0xFF1976D2)
                                )
                                Text(
                                    text = "${w.current_weather.temperature}°C",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1976D2)
                                )
                            }
                        }
                    }
                }
            }

            // Gauge Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.size(240.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                progress = { 0.75f },
                                modifier = Modifier.fillMaxSize().rotate(-135f),
                                strokeWidth = 24.dp,
                                color = Color(0xFFE9ECEF),
                                strokeCap = StrokeCap.Round,
                                trackColor = Color.Transparent
                            )
                            CircularProgressIndicator(
                                progress = { progress * 0.75f },
                                modifier = Modifier.fillMaxSize().rotate(-135f),
                                strokeWidth = 24.dp,
                                color = Color(0xFF42A5F5),
                                strokeCap = StrokeCap.Round,
                                trackColor = Color.Transparent
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier.size(100.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = if(totalIntake==0) painterResource(R.drawable.logo0) else if((totalIntake*100/dailyGoal)<30) painterResource(R.drawable.logo2) else if((totalIntake*100/dailyGoal)<100) painterResource(R.drawable.logo3) else painterResource(R.drawable.logo4),
                                        contentDescription = null,
                                        modifier = Modifier.size(150.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "$totalIntake / $dailyGoal mL",
                                    fontSize = 16.sp,
                                    color = Color(0xFF6C757D)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = { viewModel.addWater(viewModel.cupSize) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(54.dp),
                                shape = RoundedCornerShape(27.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF42A5F5))
                            ) {
                                Text("Drink (${viewModel.cupSize} mL)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Surface(
                                modifier = Modifier.size(54.dp),
                                shape = CircleShape,
                                border = BorderStroke(1.dp, Color(0xFFDEE2E6)),
                                color = Color.White,
                                onClick = { showCupDialog = true }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Image(
                                        painter = painterResource(R.drawable.cup),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // History Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "History",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            TextButton(onClick = {
                                navController.navigate(Screen.History.route)
                            }) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("View All", color = Color(0xFF42A5F5))
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = Color(0xFF42A5F5),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (history.isEmpty()) {
                            Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                                Text("No history yet", color = Color.Gray)
                            }
                        } else {
                            // Map the items directly here instead of using a nested LazyColumn
                            history.take(5).forEach { log ->
                                HistoryItem(log, onDelete = { viewModel.deleteLog(log) })
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }

    if (showCupDialog && userProfile != null) {
        HomeScreenCupSizeDialog(
            currentSize = userProfile!!.cupSize,
            onDismiss = { showCupDialog = false },
            onConfirm = { newSize: Int ->
                viewModel.updateProfile(userProfile!!.copy(cupSize = newSize))
                showCupDialog = false
            }
        )
    }
}

@Composable
fun HomeScreenCupSizeDialog(currentSize: Int, onDismiss: () -> Unit, onConfirm: (Int) -> Unit) {
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
fun HistoryItem(log: WaterLog, onDelete: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFF1F3F5)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(R.drawable.cup),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Water", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(
                text = SimpleDateFormat("HH:mm a", Locale.getDefault()).format(Date(log.timestamp)),
                color = Color.Gray,
                fontSize = 12.sp
            )
        }

        Text(
            text = "${log.amountMl} mL",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        Box {
            IconButton(onClick = { showMenu = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.Gray)
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier
                    .background(Color.White)
                    .width(150.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                DropdownMenuItem(
                    text = { Text("Delete", color = Color(0xFFFF5252), fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Outlined.Delete, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(20.dp)) },
                    onClick = {
                        onDelete()
                        showMenu = false
                    }
                )
            }
        }
    }
}
