package com.example.noted.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.noted.R

@Entity
data class Note(
        @PrimaryKey(autoGenerate = true) val id: Int? = null,
        val title: String,
        val content: String,
        val timestamp: Long,
        val color: Int,
) {
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