package com.example.noted.domain.use_case

import com.example.noted.data.repository.NoteRepositoryImpl

data class NoteUseCases(
    val getNotes: GetNotesUseCase = GetNotesUseCase(NoteRepositoryImpl()),
    val getFavouriteNotes: GetFavouriteNotesUseCase = GetFavouriteNotesUseCase(NoteRepositoryImpl()),
    val getNotesByDay: GetNotesByDay = GetNotesByDay(NoteRepositoryImpl()),
    val getNotesByWeek: GetNotesByWeek = GetNotesByWeek(NoteRepositoryImpl()),
    val deleteNote: DeleteNoteUseCase = DeleteNoteUseCase(NoteRepositoryImpl()),
    val insertNote: InsertNoteUseCase = InsertNoteUseCase(NoteRepositoryImpl()),
    val getNote: GetNoteUseCase = GetNoteUseCase(NoteRepositoryImpl())

)
