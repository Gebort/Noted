package com.example.noted.data.data_source

import androidx.room.*
import com.example.noted.domain.model.NoteDto
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM notedto")
    fun getNotes(): Flow<List<NoteDto>>

    @Query("SELECT * FROM notedto WHERE favourite = 1")
    fun getFavouriteNotes(): Flow<List<NoteDto>>

    @Query("SELECT * FROM notedto WHERE timestamp < :timeBefore AND timestamp >= :timeFrom")
    fun getNotesAfterBefore(timeFrom: Long, timeBefore: Long): Flow<List<NoteDto>>

    @Query("SELECT * FROM notedto WHERE id = :id")
    suspend fun getNoteById(id: Int): NoteDto?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteDto)

    @Delete
    suspend fun deleteNote(note: NoteDto)
}