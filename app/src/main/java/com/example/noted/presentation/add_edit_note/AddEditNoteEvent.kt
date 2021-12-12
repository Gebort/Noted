package com.example.noted.presentation.add_edit_note

sealed class AddEditNoteEvent {
    data class EnteredTitle(val value: String): AddEditNoteEvent()
    data class EnteredContent(val value: String): AddEditNoteEvent()
    data class ChangedColor(val color: Int): AddEditNoteEvent()
    data class ChangedTimestamp(val value: Long): AddEditNoteEvent()
    data class ChangedDatestamp(val value: Long): AddEditNoteEvent()
    data class EnteredProgress(val value: Float): AddEditNoteEvent()
    object ToggleFavourite : AddEditNoteEvent()
    object SaveNote: AddEditNoteEvent()

}