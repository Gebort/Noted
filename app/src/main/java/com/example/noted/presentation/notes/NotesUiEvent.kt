package com.example.noted.presentation.notes

sealed class NotesUiEvent {
    data class NoteDeleted(val title: String): NotesUiEvent()
}
