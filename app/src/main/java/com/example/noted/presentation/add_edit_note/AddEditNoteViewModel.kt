package com.example.noted.presentation.add_edit_note

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noted.domain.model.InvalidNoteException
import com.example.noted.domain.model.Note
import com.example.noted.domain.use_case.NoteUseCases
import com.example.noted.domain.util.Date
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.*
import java.util.*

class AddEditNoteViewModel: ViewModel() {

    private val noteUseCases = NoteUseCases()

    private val _noteTitle = MutableLiveData<String>()
    val noteTitle get() = _noteTitle

    private val _noteContent = MutableLiveData<String>()
    val noteContent get() = _noteContent

    private val _noteColor = MutableLiveData<Int>()
    val noteColor get() = _noteColor

    private val _favourite = MutableLiveData<Boolean>()
    val favourite get() = _favourite

    private val _progress = MutableLiveData<Float>()
    val progress get() = _progress

    private val _uiEvent = MutableLiveData<AddEditNoteUiEvent>()
    val uiEvent get() = _uiEvent

    private val _timestamp = MutableLiveData<Long>()
    val timestamp get() = _timestamp

    private val _datestamp = MutableLiveData<Long>()
    val datestamp get() = _datestamp

    private var currentNoteId: Int? = null

    private var getNoteJob: Job? = null

    init {
        setCurrentNoteId(null)
    }

    fun setCurrentNoteId(id: Int?){
        if (id != currentNoteId) {
            currentNoteId = id
            emptyData()
            if (id != null && id != -1) {
                getNoteJob?.cancel()
                getNoteJob = viewModelScope.launch {
                    noteUseCases.getNote(id)?.also { note ->
                        currentNoteId = note.id
                        _noteTitle.value = note.title
                        _noteContent.value = note.content
                        _noteColor.value = note.color
                        _progress.value = note.progress
                        _favourite.value = note.favourite
                        _datestamp.value = note.date.dateLong
                        _timestamp.value = note.date.timeLong
                    }
                }
            }
        }
    }

    fun handledEvent(uiEvent: AddEditNoteUiEvent){
        if (this.uiEvent.value == uiEvent){
            _uiEvent.value?.handled = true
        }
    }

    private fun emptyData(){
        _noteTitle.value = ""
        _noteContent.value = ""
        _noteColor.value = Note.noteColors.random()
        _uiEvent.value = null
        _favourite.value = false
        _progress.value = 0f
        val date = Date.fromUTC(System.currentTimeMillis())
        _datestamp.value = date.dateLong
        _timestamp.value = date.timeLong
        currentNoteId = null
    }

    fun onEvent(event: AddEditNoteEvent){
        when (event){
            is AddEditNoteEvent.EnteredTitle -> {
                _noteTitle.value = event.value
            }
            is AddEditNoteEvent.EnteredContent -> {
                _noteContent.value = event.value
            }
            is AddEditNoteEvent.ChangedColor -> {
                _noteColor.value = event.color
            }
            is AddEditNoteEvent.ChangedTimestamp -> {
                _timestamp.value = event.value
            }
            is AddEditNoteEvent.ChangedDatestamp -> {
                _datestamp.value = event.value
            }
            is AddEditNoteEvent.ToggleFavourite -> {
                _favourite.value = favourite.value != true
            }
            is AddEditNoteEvent.EnteredProgress -> {
                _progress.value = event.value
            }
            is AddEditNoteEvent.SaveNote -> {
                viewModelScope.launch {
                    try {
                        noteUseCases.insertNote(
                            Note(
                                    id = if (currentNoteId != -1) currentNoteId else null,
                                    title = noteTitle.value!!,
                                    content = noteContent.value!!,
                                    date = Date.fromLocal(timestamp.value!! + datestamp.value!! ),
                                    color = noteColor.value!!,
                                    favourite = favourite.value!!,
                                    progress = progress.value!!
                            )
                        )
                        _uiEvent.value = AddEditNoteUiEvent.SavedNote()
                    } catch (e: InvalidNoteException) {
                        _uiEvent.value = AddEditNoteUiEvent.FailedToSave()
                    }
                }
            }
        }
    }
}