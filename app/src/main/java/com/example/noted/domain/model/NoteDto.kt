package com.example.noted.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.noted.R
import com.example.noted.domain.util.Date

@Entity
data class NoteDto(
        @PrimaryKey(autoGenerate = true) val id: Int? = null,
        val title: String,
        val content: String,
        val timestamp: Long,
        val color: Int,
        val favourite: Boolean,
        val progress: Float
){
    fun toNote(): Note{
        return Note(
                id = id,
                title = title,
                content = content,
                date = Date.fromUTC(timestamp),
                color = color,
                favourite = favourite,
                progress = progress
        )
    }
}
