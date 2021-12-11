package com.example.noted.presentation.notes

sealed class NotesUiEvent (var handled: Boolean){
    data class NoteDeleted(val title: String): NotesUiEvent(false)
}
