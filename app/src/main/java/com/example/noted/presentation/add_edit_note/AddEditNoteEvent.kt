package com.example.noted.presentation.add_edit_note

sealed class AddEditNoteEvent {
    data class EnteredTitle(val value: String): AddEditNoteEvent()
    data class EnteredContent(val value: String): AddEditNoteEvent()
    data class ChangedColor(val color: Int): AddEditNoteEvent()
    object SaveNote: AddEditNoteEvent()

}