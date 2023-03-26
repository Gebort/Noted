package com.example.noted.presentation.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noted.domain.model.Note
import com.example.noted.domain.use_case.NoteUseCases
import com.example.noted.domain.util.NoteOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouriteNotesViewModel @Inject constructor (
    private val noteUseCases: NoteUseCases
) : ViewModel() {

    private var _state = MutableStateFlow(NotesState())
    val state get() = _state.asStateFlow()

    private var _uiEvent = MutableSharedFlow<NotesUiEvent>()
    val uiEvent get() = _uiEvent.asSharedFlow()


    private var lastDeletedNote: Note? = null

    private var getNotesJob: Job? = null

    init {
        getNotes(state.value.noteOrder)
    }


    fun onEvent(event: NotesEvent){
        when (event){
            is NotesEvent.Order -> {
                    if (state.value.noteOrder::class == event.noteOrder::class &&
                        state.value.noteOrder.orderType == event.noteOrder.orderType){
                        return
                    }
                    getNotes(event.noteOrder)
            }
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
            is NotesEvent.ToggleOrderSelection -> {
                    _state.value = state.value.copy(
                        isOrderSelectionVisible = !state.value.isOrderSelectionVisible
                    )
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
        }
    }

    private fun getNotes(noteOrder: NoteOrder){
        getNotesJob?.cancel()
        getNotesJob = noteUseCases.getFavouriteNotes(noteOrder)
            .onEach { notes ->
                    _state.value = state.value.copy(
                        notes = notes,
                        noteOrder = noteOrder
                    )
            }
            .launchIn(viewModelScope)
    }
}