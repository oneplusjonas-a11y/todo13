package com.grid.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grid.app.data.Priority
import com.grid.app.data.TodoWithTopic
import com.grid.app.ui.GridViewModel
import com.grid.app.ui.components.MetallicPanel
import com.grid.app.ui.components.PriorityChip
import com.grid.app.ui.theme.GridCyan
import com.grid.app.ui.theme.GridDim
import com.grid.app.ui.theme.GridMuted
import com.grid.app.ui.theme.GridWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllTodosScreen(viewModel: GridViewModel) {
    val allTodos by viewModel.allTodos.collectAsState()
    var showDoneToggle by remember { mutableStateOf(false) }
    var filterPriority by remember { mutableStateOf<Priority?>(null) }
    var sortMenuExpanded by remember { mutableStateOf(false) }
    var sortOption by remember { mutableStateOf(TodoSort.PRIORITY) }

    val visible = remember(allTodos, showDoneToggle, filterPriority, sortOption) {
        val filtered = allTodos
            .filter { showDoneToggle || !it.isDone }
            .filter { filterPriority == null || it.priority == filterPriority }
        when (sortOption) {
            TodoSort.PRIORITY -> filtered // already priority/date sorted from the database
            TodoSort.DUE_DATE -> filtered.sortedWith(compareBy(nullsLast()) { it.dueDate })
            TodoSort.NEWEST -> filtered.sortedByDescending { it.createdAt }
            TodoSort.ALPHA -> filtered.sortedBy { it.title.lowercase() }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("ALL TO-DOS", fontWeight = FontWeight.Bold, letterSpacing = 2.sp, color = GridCyan) },
                actions = {
                    Box {
                        IconButton(onClick = { sortMenuExpanded = true }) {
                            Icon(Icons.Filled.Sort, contentDescription = "Sort", tint = GridCyan)
                        }
                        DropdownMenu(expanded = sortMenuExpanded, onDismissRequest = { sortMenuExpanded = false }) {
                            TodoSort.values().forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.label) },
                                    onClick = {
                                        sortOption = option
                                        sortMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filterPriority == null,
                    onClick = { filterPriority = null },
                    label = { Text("All") }
                )
                Priority.values().reversed().forEach { p ->
                    FilterChip(
                        selected = filterPriority == p,
                        onClick = { filterPriority = if (filterPriority == p) null else p },
                        label = { Text(p.label) }
                    )
                }
            }
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Show completed", color = GridDim, fontSize = 13.sp, modifier = Modifier.weight(1f))
                Switch(
                    checked = showDoneToggle,
                    onCheckedChange = { showDoneToggle = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = GridCyan, checkedTrackColor = Color(0xFF10202A))
                )
            }

            if (visible.isEmpty()) {
                EmptyState("Nothing here. You're clear.")
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(visible, key = { it.id }) { todo ->
                        MasterTodoRow(
                            todo = todo,
                            onToggle = { viewModel.toggleTodoWithTopicDone(todo) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MasterTodoRow(todo: TodoWithTopic, onToggle: () -> Unit) {
    val accent = Color(todo.topicAccent)
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
                Text(todo.topicName, color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    PriorityChip(todo.priority)
                    todo.dueDate?.let {
                        Spacer(Modifier.width(8.dp))
                        Text(formatDate(it), color = GridDim, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
