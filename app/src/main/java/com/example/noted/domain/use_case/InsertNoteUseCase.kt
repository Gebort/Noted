package com.example.noted.domain.use_case

import com.example.noted.domain.model.InvalidNoteException
import com.example.noted.domain.model.Note
import com.example.noted.domain.repository.NoteRepository
import kotlin.jvm.Throws

class InsertNoteUseCase(
    private val repository: NoteRepository
) {
    @Throws(InvalidNoteException::class)
    suspend operator fun invoke(note: Note){
        if (note.title.isBlank()){
            throw InvalidNoteException("Empty title")
        }
        if (note.content.isBlank()){
            throw InvalidNoteException("Empty content")
        }
        repository.insertNote(note.toDto())
    }
}