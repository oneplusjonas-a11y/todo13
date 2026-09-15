package com.grid.app.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grid.app.data.LogEntry
import com.grid.app.data.Priority
import com.grid.app.data.TodoItem
import com.grid.app.ui.GridViewModel
import com.grid.app.ui.components.MetallicPanel
import com.grid.app.ui.components.PriorityChip
import com.grid.app.ui.theme.GridCyan
import com.grid.app.ui.theme.GridDim
import com.grid.app.ui.theme.GridMuted
import com.grid.app.ui.theme.GridWhite
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicDetailScreen(topicId: Long, viewModel: GridViewModel, onBack: () -> Unit) {
    val topic = viewModel.topics.collectAsState().value.find { it.id == topicId }
    val todos by viewModel.todosForTopic(topicId).collectAsState(initial = emptyList())
    val logs by viewModel.logsForTopic(topicId).collectAsState(initial = emptyList())

    var selectedTab by remember { mutableStateOf(0) }
    var showAddTodo by remember { mutableStateOf(false) }
    var showAddLog by remember { mutableStateOf(false) }

    val accent = topic?.let { Color(it.accentColor) } ?: GridCyan

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(topic?.name ?: "", color = GridWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = accent)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { if (selectedTab == 0) showAddTodo = true else showAddLog = true },
                containerColor = accent,
                contentColor = Color.Black
            ) { Icon(Icons.Filled.Add, contentDescription = "Add") }
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = accent
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("To-Dos") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Log") })
            }

            if (selectedTab == 0) {
                if (todos.isEmpty()) {
                    EmptyState("No to-dos yet for this topic.")
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(todos, key = { it.id }) { todo ->
                            TodoRow(
                                todo = todo,
                                accent = accent,
                                onToggle = { viewModel.toggleTodoDone(todo) },
                                onDelete = { viewModel.deleteTodo(todo) }
                            )
                        }
                    }
                }
            } else {
                if (logs.isEmpty()) {
                    EmptyState("No log entries yet for this topic.")
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(logs, key = { it.id }) { log ->
                            LogRow(log = log, accent = accent, onDelete = { viewModel.deleteLog(log) })
                        }
                    }
                }
            }
        }
    }

    if (showAddTodo) {
        AddTodoDialog(
            accent = accent,
            onDismiss = { showAddTodo = false },
            onConfirm = { title, notes, priority, due ->
                viewModel.addTodo(topicId, title, notes, priority, due)
                showAddTodo = false
            }
        )
    }

    if (showAddLog) {
        AddLogDialog(
            accent = accent,
            onDismiss = { showAddLog = false },
            onConfirm = { content ->
                viewModel.addLog(topicId, content)
                showAddLog = false
            }
        )
    }
}

@Composable
fun EmptyState(text: String) {
    Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Text(text, color = GridMuted)
    }
}

@Composable
fun TodoRow(todo: TodoItem, accent: Color, onToggle: () -> Unit, onDelete: () -> Unit) {
    MetallicPanel(accent = accent, modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Checkbox(
                checked = todo.isDone,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(checkedColor = accent, uncheckedColor = GridMuted)
            )
            Column(Modifier.weight(1f)) {
                Text(
                    text = todo.title,
                    color = GridWhite,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = if (todo.isDone) TextDecoration.LineThrough else TextDecoration.None
                )
                if (todo.notes.isNotBlank()) {
                    Text(todo.notes, color = GridDim, fontSize = 12.sp, maxLines = 2)
                }
                Row(modifier = Modifier.padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    PriorityChip(todo.priority)
                    todo.dueDate?.let {
                        Spacer(Modifier.width(8.dp))
                        Text(formatDate(it), color = GridDim, fontSize = 11.sp)
                    }
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = GridMuted)
            }
        }
    }
}

@Composable
fun LogRow(log: LogEntry, accent: Color, onDelete: () -> Unit) {
    MetallicPanel(accent = accent, modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.weight(1f)) {
                Text(formatDateTime(log.createdAt), color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(log.content, color = GridWhite, fontSize = 14.sp)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = GridMuted)
            }
        }
    }
}

fun formatDate(millis: Long): String =
    SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(millis))

fun formatDateTime(millis: Long): String =
    SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(millis))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTodoDialog(
    accent: Color,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Priority, Long?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Priority.MEDIUM) }
    var dueDate by remember { mutableStateOf<Long?>(null) }
    var priorityMenuExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0B0F14),
        title = { Text("New To-Do", color = GridWhite) },
        text = {
            Column {
                OutlinedTextField(
                    value = title, onValueChange = { title = it },
                    label = { Text("Title") }, singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accent, unfocusedBorderColor = Color(0xFF2A3542),
                        focusedTextColor = GridWhite, unfocusedTextColor = GridWhite, cursorColor = accent
                    )
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = notes, onValueChange = { notes = it },
                    label = { Text("Notes (optional)") }, maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accent, unfocusedBorderColor = Color(0xFF2A3542),
                        focusedTextColor = GridWhite, unfocusedTextColor = GridWhite, cursorColor = accent
                    )
                )
                Spacer(Modifier.height(12.dp))
                Box {
                    OutlinedButton(onClick = { priorityMenuExpanded = true }) {
                        Text("Priority: ${priority.label}", color = accent)
                    }
                    DropdownMenu(expanded = priorityMenuExpanded, onDismissRequest = { priorityMenuExpanded = false }) {
                        Priority.values().forEach { p ->
                            DropdownMenuItem(text = { Text(p.label) }, onClick = {
                                priority = p
                                priorityMenuExpanded = false
                            })
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = {
                    val cal = Calendar.getInstance()
                    android.app.DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            val picked = Calendar.getInstance()
                            picked.set(year, month, day, 0, 0, 0)
                            dueDate = picked.timeInMillis
                        },
                        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }) {
                    Text(dueDate?.let { "Due: ${formatDate(it)}" } ?: "Set due date", color = accent)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (title.isNotBlank()) onConfirm(title.trim(), notes.trim(), priority, dueDate)
            }) { Text("Add", color = accent) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = GridMuted) }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLogDialog(accent: Color, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0B0F14),
        title = { Text("New Log Entry", color = GridWhite) },
        text = {
            OutlinedTextField(
                value = content, onValueChange = { content = it },
                label = { Text("What happened?") }, minLines = 3, maxLines = 6,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accent, unfocusedBorderColor = Color(0xFF2A3542),
                    focusedTextColor = GridWhite, unfocusedTextColor = GridWhite, cursorColor = accent
                )
            )
        },
        confirmButton = {
            TextButton(onClick = {
                if (content.isNotBlank()) onConfirm(content.trim())
            }) { Text("Add", color = accent) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = GridMuted) }
        }
    )
}
