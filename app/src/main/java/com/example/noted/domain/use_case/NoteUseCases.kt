package com.example.noted.domain.use_case

import com.example.noted.data.repository.NoteRepositoryImpl

data class NoteUseCases(
    val getNotes: GetNotesUseCase = GetNotesUseCase(NoteRepositoryImpl()),
    val deleteNote: DeleteNoteUseCase = DeleteNoteUseCase(NoteRepositoryImpl()),
    val insertNote: InsertNoteUseCase = InsertNoteUseCase(NoteRepositoryImpl()),
    val getNote: GetNoteUseCase = GetNoteUseCase(NoteRepositoryImpl())

)
