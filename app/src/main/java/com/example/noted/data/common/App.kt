package com.example.noted.data.common

import android.app.Application
import androidx.room.Room
import com.example.noted.data.data_source.NoteDatabase


class App : Application() {
    private var database: NoteDatabase? = null
    override fun onCreate() {
        super.onCreate()
        instance = this
        database = Room.databaseBuilder(this, NoteDatabase::class.java, "database")
            .build()
    }

    fun getDatabase(): NoteDatabase? {
        return database
    }

    companion object {
        var instance: App? = null
    }
}