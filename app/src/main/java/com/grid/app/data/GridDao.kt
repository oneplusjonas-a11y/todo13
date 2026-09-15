package com.grid.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GridDao {

    // Topics
    @Query("SELECT * FROM topics ORDER BY createdAt DESC")
    fun getTopics(): Flow<List<Topic>>

    @Insert
    suspend fun insertTopic(topic: Topic): Long

    @Delete
    suspend fun deleteTopic(topic: Topic)

    // Todos
    @Query("""
        SELECT * FROM todos WHERE topicId = :topicId
        ORDER BY isDone ASC,
                 CASE priority WHEN 'URGENT' THEN 3 WHEN 'HIGH' THEN 2 WHEN 'MEDIUM' THEN 1 ELSE 0 END DESC,
                 CASE WHEN dueDate IS NULL THEN 1 ELSE 0 END ASC,
                 dueDate ASC
    """)
    fun getTodosForTopic(topicId: Long): Flow<List<TodoItem>>

    @Query("""
        SELECT todos.id as id, todos.topicId as topicId, todos.title as title, todos.notes as notes,
               todos.priority as priority, todos.dueDate as dueDate, todos.isDone as isDone,
               todos.createdAt as createdAt, topics.name as topicName, topics.accentColor as topicAccent
        FROM todos INNER JOIN topics ON todos.topicId = topics.id
        ORDER BY todos.isDone ASC,
                 CASE todos.priority WHEN 'URGENT' THEN 3 WHEN 'HIGH' THEN 2 WHEN 'MEDIUM' THEN 1 ELSE 0 END DESC,
                 CASE WHEN todos.dueDate IS NULL THEN 1 ELSE 0 END ASC,
                 todos.dueDate ASC
    """)
    fun getAllTodosWithTopic(): Flow<List<TodoWithTopic>>

    @Insert
    suspend fun insertTodo(todo: TodoItem): Long

    @Update
    suspend fun updateTodo(todo: TodoItem)

    @Delete
    suspend fun deleteTodo(todo: TodoItem)

    // Logs
    @Query("SELECT * FROM logs WHERE topicId = :topicId ORDER BY createdAt DESC")
    fun getLogsForTopic(topicId: Long): Flow<List<LogEntry>>

    @Insert
    suspend fun insertLog(log: LogEntry): Long

    @Delete
    suspend fun deleteLog(log: LogEntry)
}
