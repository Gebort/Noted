package com.example.noted.presentation.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noted.domain.model.InvalidNoteException
import com.example.noted.domain.model.Note
import com.example.noted.domain.use_case.NoteUseCases
import com.example.noted.domain.util.NoteOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesDayViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases
) : ViewModel() {

    private var _state = MutableStateFlow(NotesState())
    val state get() = _state.asStateFlow()

    private var _uiEvent = MutableSharedFlow<NotesUiEvent>()
    val uiEvent get() = _uiEvent.asSharedFlow()

    private var lastDeletedNote: Note? = null

    private var getNotesJob: Job? = null

    init {
        state.value.let {
            getNotes(it.noteOrder)
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
            is NotesEvent.DeleteNote -> {
                viewModelScope.launch {
                    noteUseCases.deleteNote(event.note)
                    lastDeletedNote = event.note
                    _uiEvent.emit(NotesUiEvent.NoteDeleted(event.note.title))
                }
            }
            is NotesEvent.RestoreNote -> {
                viewModelScope.launch {
                    noteUseCases.insertNote(lastDeletedNote ?: return@launch)
                    lastDeletedNote = null
                }
            }
            is NotesEvent.ToggleFavourite -> {
                viewModelScope.launch {
                    noteUseCases.insertNote(
                        event.note.copy(
                            favourite = !event.note.favourite
                        )
                    )
                }
            }
            is NotesEvent.Order -> {}
            is NotesEvent.ToggleOrderSelection -> {}
        }
    }

    private fun getNotes(noteOrder: NoteOrder){
        getNotesJob?.cancel()
        getNotesJob = noteUseCases.getNotesByDay()
            .onEach { notes ->
                state.value.let {
                    _state.value = it.copy(
                        notes = notes,
                        noteOrder = noteOrder
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}