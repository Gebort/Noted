package com.example.noted.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.noted.domain.model.NoteDto

@Database(
    entities = [NoteDto::class],
    version = 1
)
abstract class NoteDatabase: RoomDatabase() {

    abstract val noteDao: NoteDao

    companion object{
        const val DATABASE_NAME = "database"
    }
}