package com.grid.app.data

data class TodoWithTopic(
    val id: Long,
    val topicId: Long,
    val title: String,
    val notes: String,
    val priority: Priority,
    val dueDate: Long?,
    val isDone: Boolean,
    val createdAt: Long,
    val topicName: String,
    val topicAccent: Long
)
