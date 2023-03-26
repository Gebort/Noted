package com.example.noted.data.repository

import com.example.noted.data.data_source.NoteDatabase
import com.example.noted.domain.model.NoteDto
import com.example.noted.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class NoteRepositoryImpl(db: NoteDatabase): NoteRepository {

    private val dao = db.noteDao

    override fun getNotes(): Flow<List<NoteDto>> {
        return dao.getNotes()
    }

    override fun getFavouriteNotes(): Flow<List<NoteDto>> {
        return dao.getFavouriteNotes()
    }

    override fun getNotesAfterBefore(timeFrom: Long, timeBefore: Long): Flow<List<NoteDto>> {
        return dao.getNotesAfterBefore(timeFrom, timeBefore)
    }

    override suspend fun getNoteById(id: Int): NoteDto? {
        return dao.getNoteById(id)
    }

    override suspend fun insertNote(note: NoteDto) {
        dao.insertNote(note)
    }

    override suspend fun deleteNote(note: NoteDto) {
        dao.deleteNote(note)
    }
}