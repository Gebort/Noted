package com.example.noted.domain.model

import com.example.noted.R
import com.example.noted.domain.util.Date

data class Note(
        val id: Int? = null,
        val title: String,
        val content: String,
        val date: Date,
        val color: Int,
        val favourite: Boolean,
        val progress: Float
) {
    fun toDto(): NoteDto{
        return NoteDto(
                id = id,
                title = title,
                content = content,
                timestamp = date.dateUTC,
                color = color,
                favourite = favourite,
                progress = progress
        )
    }

    companion object {
        val noteColors = listOf(
                R.color.purple_200,
                R.color.blue_200,
                R.color.green_200,
                R.color.orange_200,
                R.color.teal_200,
                R.color.red_200
        )
    }
}

class InvalidNoteException(message: String): Exception(message)