package com.example.noted.data.repository

import com.example.noted.data.common.App
import com.example.noted.data.data_source.NoteDao
import com.example.noted.domain.model.Note
import com.example.noted.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class NoteRepositoryImpl: NoteRepository {

    private val dao = App.instance?.getDatabase()?.noteDao!!

    override fun getNotes(): Flow<List<Note>> {
        return dao.getNotes()
    }

    override fun getFavouriteNotes(): Flow<List<Note>> {
        return dao.getFavouriteNotes()
    }

    override suspend fun getNoteById(id: Int): Note? {
        return dao.getNoteById(id)
    }

    override suspend fun insertNote(note: Note) {
        dao.insertNote(note)
    }

    override suspend fun deleteNote(note: Note) {
        dao.deleteNote(note)
    }
}