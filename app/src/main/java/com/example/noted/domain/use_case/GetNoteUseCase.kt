package com.example.noted.domain.use_case

import com.example.noted.domain.model.Note
import com.example.noted.domain.repository.NoteRepository

class GetNoteUseCase (
    private val repository: NoteRepository
){

    suspend operator fun invoke(id: Int): Note? {
        return repository.getNoteById(id)
    }
}