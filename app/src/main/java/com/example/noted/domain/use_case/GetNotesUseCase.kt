package com.example.noted.domain.use_case

import com.example.noted.domain.model.Note
import com.example.noted.domain.repository.NoteRepository
import com.example.noted.domain.util.NoteOrder
import com.example.noted.domain.util.OrderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

class GetNotesUseCase(
    private val repository: NoteRepository
) {

    operator fun invoke(
        noteOrder: NoteOrder = NoteOrder.Date(OrderType.Descending)
    ): Flow<List<Note>> {
        return repository.getNotes().map { notesDto ->
            val notes = notesDto.map { it.toNote() }
            when(noteOrder.orderType) {
                is OrderType.Ascending -> {
                    when(noteOrder){
                        is NoteOrder.Title -> notes.sortedBy { it.title.toLowerCase(Locale.ROOT) }
                        is NoteOrder.Date -> notes.sortedBy { it.date.datestamp }
                        is NoteOrder.Color -> notes.sortedBy { it.color }
                    }
                }
                is OrderType.Descending -> {
                    when(noteOrder){
                        is NoteOrder.Title -> notes.sortedByDescending { it.title.toLowerCase(Locale.ROOT) }
                        is NoteOrder.Date -> notes.sortedByDescending { it.date.datestamp }
                        is NoteOrder.Color -> notes.sortedByDescending { it.color }
                    }
                }
        }
        }
    }
}