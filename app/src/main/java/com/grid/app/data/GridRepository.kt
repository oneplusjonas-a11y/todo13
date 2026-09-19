package com.grid.app.data

class GridRepository(private val dao: GridDao) {
    fun getTopics() = dao.getTopics()
    suspend fun insertTopic(topic: Topic) = dao.insertTopic(topic)
    suspend fun deleteTopic(topic: Topic) = dao.deleteTopic(topic)

    fun getTodosForTopic(topicId: Long) = dao.getTodosForTopic(topicId)
    fun getAllTodosWithTopic() = dao.getAllTodosWithTopic()
    suspend fun insertTodo(todo: TodoItem) = dao.insertTodo(todo)
    suspend fun updateTodo(todo: TodoItem) = dao.updateTodo(todo)
    suspend fun deleteTodo(todo: TodoItem) = dao.deleteTodo(todo)

    fun getLogsForTopic(topicId: Long) = dao.getLogsForTopic(topicId)
    suspend fun insertLog(log: LogEntry) = dao.insertLog(log)
    suspend fun updateLog(log: LogEntry) = dao.updateLog(log)
    suspend fun deleteLog(log: LogEntry) = dao.deleteLog(log)
}
