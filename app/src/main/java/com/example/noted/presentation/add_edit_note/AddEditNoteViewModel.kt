package com.example.noted.presentation.add_edit_note

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noted.domain.model.InvalidNoteException
import com.example.noted.domain.model.Note
import com.example.noted.domain.use_case.NoteUseCases
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AddEditNoteViewModel: ViewModel() {

    private val noteUseCases = NoteUseCases()

    private val _noteTitle = MutableLiveData<String>()
    val noteTitle get() = _noteTitle

    private val _noteContent = MutableLiveData<String>()
    val noteContent get() = _noteContent

    private val _noteColor = MutableLiveData<Int>()
    val noteColor get() = _noteColor

    private val _uiEvent = MutableLiveData<AddEditNoteUiEvent>()
    val uiEvent get() = _uiEvent

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
            is AddEditNoteEvent.SaveNote -> {
                viewModelScope.launch {
                    try {
                        noteUseCases.insertNote(
                            Note(
                                id = if (currentNoteId != -1) currentNoteId else null,
                                title = noteTitle.value!!,
                                content = noteContent.value!!,
                                timestamp = System.currentTimeMillis(),
                                color = noteColor.value!!
                            )
                        )
                        _uiEvent.value = AddEditNoteUiEvent.SavedNote()
                        emptyData()
                    } catch (e: InvalidNoteException) {
                        _uiEvent.value = AddEditNoteUiEvent.FailedToSave()
                    }
                }
            }
        }
    }
}