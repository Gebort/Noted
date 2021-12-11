package com.example.noted.presentation.notes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noted.domain.model.InvalidNoteException
import com.example.noted.domain.model.Note
import com.example.noted.domain.use_case.NoteUseCases
import com.example.noted.domain.util.NoteOrder
import com.example.noted.presentation.add_edit_note.AddEditNoteUiEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class NotesViewModel(
) : ViewModel() {

    private val noteUseCases = NoteUseCases()
    private var _state = MutableLiveData<NotesState>()
    val state get() = _state

    private var lastDeletedNote: Note? = null

    private var getNotesJob: Job? = null

    private var _uiEvent = MutableLiveData<NotesUiEvent>()
    val uiEvent get() = _uiEvent

    init {
        _state.value = NotesState()
        _uiEvent.value = null
        state.value?.let {
            getNotes(it.noteOrder)
        }
    }

    fun handledEvent(uiEvent: NotesUiEvent){
        if (this.uiEvent.value == uiEvent){
            _uiEvent.value?.handled = true
        }
    }

    fun restoreNote(){
        if (lastDeletedNote != null) {
            try {
                viewModelScope.launch {
                    lastDeletedNote?.let {
                        noteUseCases.insertNote(lastDeletedNote!!)
                        lastDeletedNote = null
                    }
                }
            }catch (e: InvalidNoteException){

            }
        }
    }

    fun onEvent(event: NotesEvent){
        when (event){
            is NotesEvent.Order -> {
                state.value?.let {
                    if (it.noteOrder::class == event.noteOrder::class &&
                            it.noteOrder.orderType == event.noteOrder.orderType){
                        return
                    }
                    getNotes(event.noteOrder)
                }
            }
            is NotesEvent.DeleteNote -> {
                viewModelScope.launch {
                    noteUseCases.deleteNote(event.note)
                    lastDeletedNote = event.note
                    _uiEvent.value = NotesUiEvent.NoteDeleted(event.note.title)
                }
            }
            is NotesEvent.RestoreNote -> {
                viewModelScope.launch {
                    noteUseCases.insertNote(lastDeletedNote ?: return@launch)
                    lastDeletedNote = null
                }
            }
            is NotesEvent.ToggleOrderSelection -> {
                state.value?.let {
                    _state.value = it.copy(
                        isOrderSelectionVisible = !it.isOrderSelectionVisible
                    )
                }
            }
        }
    }

    private fun getNotes(noteOrder: NoteOrder){
        getNotesJob?.cancel()
        getNotesJob = noteUseCases.getNotes(noteOrder)
            .onEach { notes ->
            state.value?.let {
                _state.value = it.copy(
                    notes = notes,
                    noteOrder = noteOrder
                )
            }
        }
            .launchIn(viewModelScope)
    }
}