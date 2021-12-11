package com.example.noted.presentation.add_edit_note

sealed class AddEditNoteUiEvent(var handled: Boolean) {
    class FailedToSave: AddEditNoteUiEvent(false)
    class SavedNote: AddEditNoteUiEvent(false)
}