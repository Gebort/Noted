package com.example.noted.domain.use_case

import com.example.noted.domain.model.Note
import com.example.noted.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.*
import java.util.*

class GetNotesByWeek(
    private val repository: NoteRepository
) {

    operator fun invoke(): Flow<List<Note>> {
        val start = LocalDate.now()
        val startDate = LocalDate.of(start.year, start.month, start.dayOfMonth).toEpochDay() * 24 * 60 * 60 * 1000L

        val diff = 8L - start.dayOfWeek.value
        val end = start.plusDays(diff)
        val endDate = LocalDate.of(end.year, end.month, end.dayOfMonth).toEpochDay() * 24 * 60 * 60 * 1000L

        val startUTC = startDate - TimeZone.getDefault().rawOffset - TimeZone.getDefault().dstSavings
        val endUTC = endDate - TimeZone.getDefault().rawOffset - TimeZone.getDefault().dstSavings

        return repository.getNotesAfterBefore(startUTC, endUTC).map { notesDto ->
            notesDto.map{ it.toNote() }.sortedBy{ it.date.datestamp }
        }
    }
}