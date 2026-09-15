package com.grid.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.grid.app.data.GridDatabase
import com.grid.app.data.GridRepository
import com.grid.app.data.LogEntry
import com.grid.app.data.Priority
import com.grid.app.data.Topic
import com.grid.app.data.TodoItem
import com.grid.app.data.TodoWithTopic
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GridViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = GridRepository(GridDatabase.getInstance(application).dao())

    val topics: StateFlow<List<Topic>> = repo.getTopics()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTodos: StateFlow<List<TodoWithTopic>> = repo.getAllTodosWithTopic()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun todosForTopic(topicId: Long) = repo.getTodosForTopic(topicId)
    fun logsForTopic(topicId: Long) = repo.getLogsForTopic(topicId)

    fun addTopic(name: String, accentColor: Long) = viewModelScope.launch {
        repo.insertTopic(Topic(name = name, accentColor = accentColor))
    }

    fun deleteTopic(topic: Topic) = viewModelScope.launch {
        repo.deleteTopic(topic)
    }

    fun addTodo(topicId: Long, title: String, notes: String, priority: Priority, dueDate: Long?) = viewModelScope.launch {
        repo.insertTodo(TodoItem(topicId = topicId, title = title, notes = notes, priority = priority, dueDate = dueDate))
    }

    fun toggleTodoDone(todo: TodoItem) = viewModelScope.launch {
        repo.updateTodo(todo.copy(isDone = !todo.isDone))
    }

    fun toggleTodoWithTopicDone(todo: TodoWithTopic) = viewModelScope.launch {
        repo.updateTodo(
            TodoItem(
                id = todo.id, topicId = todo.topicId, title = todo.title, notes = todo.notes,
                priority = todo.priority, dueDate = todo.dueDate, isDone = !todo.isDone, createdAt = todo.createdAt
            )
        )
    }

    fun deleteTodo(todo: TodoItem) = viewModelScope.launch {
        repo.deleteTodo(todo)
    }

    fun addLog(topicId: Long, content: String) = viewModelScope.launch {
        repo.insertLog(LogEntry(topicId = topicId, content = content))
    }

    fun deleteLog(log: LogEntry) = viewModelScope.launch {
        repo.deleteLog(log)
    }
}
