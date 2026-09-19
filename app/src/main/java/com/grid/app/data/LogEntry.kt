package com.grid.app.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "logs",
    foreignKeys = [ForeignKey(
        entity = Topic::class,
        parentColumns = ["id"],
        childColumns = ["topicId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("topicId")]
)
data class LogEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val topicId: Long,
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)
