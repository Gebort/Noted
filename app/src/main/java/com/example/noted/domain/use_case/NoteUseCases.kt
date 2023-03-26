package com.example.noted.domain.use_case

import com.example.noted.domain.repository.NoteRepository

class NoteUseCases(
    repository: NoteRepository
) {
    val getNotes: GetNotesUseCase = GetNotesUseCase(repository)
    val getFavouriteNotes: GetFavouriteNotesUseCase = GetFavouriteNotesUseCase(repository)
    val getNotesByDay: GetNotesByDay = GetNotesByDay(repository)
    val getNotesByWeek: GetNotesByWeek = GetNotesByWeek(repository)
    val deleteNote: DeleteNoteUseCase = DeleteNoteUseCase(repository)
    val insertNote: InsertNoteUseCase = InsertNoteUseCase(repository)
    val getNote: GetNoteUseCase = GetNoteUseCase(repository)
}
