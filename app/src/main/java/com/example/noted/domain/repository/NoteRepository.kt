package com.example.noted.domain.repository

import com.example.noted.domain.model.NoteDto
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    fun getNotes(): Flow<List<NoteDto>>

    fun getFavouriteNotes(): Flow<List<NoteDto>>

    fun getNotesAfterBefore(timeFrom: Long, timeBefore: Long): Flow<List<NoteDto>>

    suspend fun getNoteById(id: Int): NoteDto?

    suspend fun insertNote(note: NoteDto)

    suspend fun deleteNote(note: NoteDto)
}