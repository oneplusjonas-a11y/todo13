package com.grid.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grid.app.data.Topic
import com.grid.app.ui.GridViewModel
import com.grid.app.ui.components.MetallicPanel
import com.grid.app.ui.theme.GridCyan
import com.grid.app.ui.theme.GridMuted
import com.grid.app.ui.theme.GridWhite
import com.grid.app.ui.theme.TopicAccents

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: GridViewModel, onTopicClick: (Long) -> Unit) {
    val topics by viewModel.topics.collectAsState()
    val allTodos by viewModel.allTodos.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("GRID", fontWeight = FontWeight.Bold, letterSpacing = 4.sp, color = GridCyan) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = GridCyan,
                contentColor = Color.Black
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add topic")
            }
        }
    ) { padding ->
        if (topics.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No topics yet. Tap + to create one.", color = GridMuted)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(padding)
            ) {
                items(topics, key = { it.id }) { topic ->
                    val openCount = allTodos.count { it.topicId == topic.id && !it.isDone }
                    TopicCard(topic = topic, openCount = openCount, onClick = { onTopicClick(topic.id) })
                }
            }
        }
    }

    if (showAddDialog) {
        AddTopicDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, accent ->
                viewModel.addTopic(name, accent)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun TopicCard(topic: Topic, openCount: Int, onClick: () -> Unit) {
    val accent = Color(topic.accentColor)
    MetallicPanel(
        accent = accent,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() }
    ) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = topic.name,
                color = GridWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 2
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(8.dp)
                        .background(accent, shape = CircleShape)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "$openCount open",
                    color = accent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTopicDialog(onDismiss: () -> Unit, onConfirm: (String, Long) -> Unit) {
    var name by remember { mutableStateOf("") }
    var selectedAccent by remember { mutableStateOf(TopicAccents.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0B0F14),
        title = { Text("New Topic", color = GridWhite) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Topic name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GridCyan,
                        unfocusedBorderColor = Color(0xFF2A3542),
                        focusedTextColor = GridWhite,
                        unfocusedTextColor = GridWhite,
                        cursorColor = GridCyan
                    )
                )
                Spacer(Modifier.height(12.dp))
                Text("Accent color", color = GridMuted, fontSize = 12.sp)
                Spacer(Modifier.height(6.dp))
                Row {
                    TopicAccents.forEach { accentLong ->
                        val c = Color(accentLong)
                        Box(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(28.dp)
                                .border(
                                    width = if (selectedAccent == accentLong) 2.dp else 0.dp,
                                    color = GridWhite,
                                    shape = CircleShape
                                )
                                .padding(2.dp)
                                .background(c, shape = CircleShape)
                                .clickable { selectedAccent = accentLong }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.isNotBlank()) onConfirm(name.trim(), selectedAccent)
            }) { Text("Create", color = GridCyan) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = GridMuted) }
        }
    )
}
