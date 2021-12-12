package com.example.noted.ui.common

import com.example.noted.domain.model.Note

sealed class DataItem {
    data class NoteItem(val note: Note): DataItem() {
        override val id = note.id
    }

    data class Header(val dayOfWeek: Int): DataItem() {
        override val id = dayOfWeek.hashCode()
    }

    abstract val id: Int?
}
