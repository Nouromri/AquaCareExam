package com.example.exam.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.exam.R
import com.example.exam.data.entity.WaterLog
import com.example.exam.viewmodel.WaterViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: WaterViewModel) {
    val allLogs by viewModel.allLogs.collectAsState()
    var showTopMenu by remember { mutableStateOf(false) }
    
    val groupedLogs = remember(allLogs) {
        allLogs.groupBy { 
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it.timestamp))
        }.toList().sortedByDescending { it.first }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("History", fontWeight = FontWeight.Bold, fontSize = 20.sp)
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
                actions = {
                    Box {
                        IconButton(onClick = { showTopMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = null)
                        }
                        DropdownMenu(
                            expanded = showTopMenu,
                            onDismissRequest = { showTopMenu = false },
                            modifier = Modifier.background(Color.White).width(180.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Delete All Logs", color = Color(0xFFFF5252)) },
                                leadingIcon = { Icon(Icons.Outlined.Delete, contentDescription = null, tint = Color(0xFFFF5252)) },
                                onClick = {
                                    viewModel.clearHistory()
                                    showTopMenu = false
                                }
                            )
                        }
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
        Column(modifier = Modifier.padding(padding)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                if (allLogs.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No history yet", color = Color.Gray)
                        }
                    }
                } else {
                    groupedLogs.forEach { (dateStr, logs) ->
                        item {
                            DateHeader(dateStr)
                        }
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                            ) {
                                Column {
                                    logs.forEachIndexed { index, log ->
                                        HistoryLogItem(log, onDelete = { viewModel.deleteLog(log) })
                                        if (index < logs.size - 1) {
                                            HorizontalDivider(
                                                modifier = Modifier.padding(horizontal = 16.dp),
                                                thickness = 0.5.dp,
                                                color = Color(0xFFF1F3F5)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun DateHeader(dateStr: String) {
    val displayDate = try {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr)
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val yesterday = Calendar.getInstance().apply { add(Calendar.DATE, -1) }.time
        val yesterdayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(yesterday)
        
        val prefix = when (dateStr) {
            today -> "Today, "
            yesterdayStr -> "Yesterday, "
            else -> ""
        }
        prefix + SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date!!)
    } catch (e: Exception) {
        dateStr
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = displayDate,
            fontSize = 13.sp,
            color = Color.LightGray
        )
        Spacer(modifier = Modifier.width(8.dp))
        HorizontalDivider(modifier = Modifier.weight(1f), thickness = 0.5.dp, color = Color(0xFFF1F3F5))
    }
}

@Composable
fun HistoryLogItem(log: WaterLog, onDelete: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(44.dp),
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            shadowElevation = 0.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(R.drawable.cup),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Water", fontWeight = FontWeight.Medium, fontSize = 16.sp, color = Color.DarkGray)
            Text(
                text = SimpleDateFormat("HH:mm a", Locale.getDefault()).format(Date(log.timestamp)),
                color = Color.LightGray,
                fontSize = 12.sp
            )
        }

        Text(
            text = "${log.amountMl} mL",
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = Color.DarkGray
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
