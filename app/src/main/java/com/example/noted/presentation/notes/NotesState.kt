package com.example.noted.presentation.notes

import com.example.noted.domain.model.Note
import com.example.noted.domain.util.NoteOrder
import com.example.noted.domain.util.OrderType

data class NotesState(
    val notes: List<Note> = emptyList(),
    val noteOrder: NoteOrder = NoteOrder.Date(OrderType.Ascending),
    val isOrderSelectionVisible: Boolean = false
)
