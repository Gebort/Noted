package com.example.noted.ui.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.noted.R
import com.example.noted.databinding.FragmentNotesListBinding
import com.example.noted.domain.model.Note
import com.example.noted.domain.util.NoteOrder
import com.example.noted.domain.util.OrderType
import com.example.noted.presentation.notes.NotesEvent
import com.example.noted.presentation.notes.NotesUiEvent
import com.example.noted.presentation.notes.NotesViewModel
import com.google.android.material.snackbar.Snackbar


class NotesListFragment : Fragment() {

    private var _binding: FragmentNotesListBinding? = null
    private val binding get() = _binding!!

    private val model: NotesViewModel by activityViewModels()
    private var adapter: NotesListAdapter? = null

    private val slidingDuration = 200L

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        _binding = FragmentNotesListBinding.inflate(inflater, container, false)
        val view = binding.root

        activity?.title = resources.getString(R.string.your_notes)


        binding.notesRecyclerView.layoutManager = LinearLayoutManager(activity?.applicationContext)

        val notes = model.state.value?.notes ?: listOf()

        adapter = NotesListAdapter(
                notes,
                binding.notesRecyclerView,
                { note -> deleteNote(note) },
                { id -> changeNote(id)},
                { note -> labelFavourite(note)}
        )
        binding.notesRecyclerView.adapter = adapter

        model.state.value?.isOrderSelectionVisible?.let { visible ->
            if (visible){
                binding.layoutOrder.visibility = View.VISIBLE
            }
            else{
                placeSortingOut()
            }
        }

       binding.radioAscending.setOnClickListener {
           model.state.value?.noteOrder?.let {
               model.onEvent(NotesEvent.Order(it.copy(OrderType.Ascending)))
           }
       }

        binding.radioDescending.setOnClickListener {
            model.state.value?.noteOrder?.let {
                model.onEvent(NotesEvent.Order(it.copy(OrderType.Descending)))
            }
        }

        binding.radioColor.setOnClickListener {
            model.state.value?.noteOrder?.let {
                model.onEvent(NotesEvent.Order(NoteOrder.Color(it.orderType)))
            }
        }

        binding.radioDate.setOnClickListener {
            model.state.value?.noteOrder?.let {
                model.onEvent(NotesEvent.Order(NoteOrder.Date(it.orderType)))
            }
        }

        binding.radioTitle.setOnClickListener {
            model.state.value?.noteOrder?.let {
                model.onEvent(NotesEvent.Order(NoteOrder.Title(it.orderType)))
            }
        }

        binding.fabAddNote.setOnClickListener {
            val action = NotesListFragmentDirections.actionNotesListFragmentToAddEditNoteFragment()
            findNavController().navigate(action)
        }

        activity?.let {
            model.state.observe(it) { state ->
                if (state.isOrderSelectionVisible != binding.layoutOrder.isVisible) {
                    if (state.isOrderSelectionVisible){
                        slideSortingDown()
                    }
                    else{
                        slideSortingUp()
                    }
                }

                when (state.noteOrder){
                    is NoteOrder.Title -> {
                        binding.radioTitle.isChecked = true
                    }

                    is NoteOrder.Date -> {
                        binding.radioDate.isChecked = true
                    }

                    is NoteOrder.Color -> {
                        binding.radioColor.isChecked = true
                    }
                }

                when (state.noteOrder.orderType){
                    is OrderType.Descending -> {
                        binding.radioDescending.isChecked = true
                    }

                    is OrderType.Ascending -> {
                        binding.radioAscending.isChecked = true
                    }
                }

                adapter?.let { adapter ->
                    adapter.submitList(state.notes)
                }
            }

            model.uiEvent.observe(it, { uiEvent ->
                if (uiEvent != null && !uiEvent.handled) {
                    when (uiEvent) {
                        is NotesUiEvent.NoteDeleted -> {
                            model.handledEvent(uiEvent)
                            this.view?.let { it1 -> Snackbar.make(it1, String.format(resources.getString(R.string.note_deleted), uiEvent.title), Snackbar.LENGTH_LONG)
                                    .setAction(R.string.cancel){
                                        model.restoreNote()
                                    }
                                    .show() }
                        }
                    }
                }
            })

        }

        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.action_sort -> {
                model.onEvent(NotesEvent.ToggleOrderSelection)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        _binding = null
        activity?.let {
            model.state.removeObservers(it)
        }
        super.onDestroyView()
    }

    private fun deleteNote(note: Note){
        model.onEvent(NotesEvent.DeleteNote(note))
    }

    private fun changeNote(id: Int){
        val action = NotesListFragmentDirections.actionNotesListFragmentToAddEditNoteFragment()
        action.id = id
        findNavController().navigate(action)
    }

    private fun labelFavourite(note: Note){
        model.onEvent(NotesEvent.ToggleFavourite(note))
    }

    private fun slideSortingDown(){
        binding.layoutOrder.visibility = View.VISIBLE
        val animate = TranslateAnimation(
                0F,
                0F,
                -binding.layoutOrder.height.toFloat(),
                0F)

        animate.duration = slidingDuration
        animate.fillAfter = true
        binding.layoutOrder.startAnimation(animate)
    }

    private fun slideSortingUp(){
        binding.layoutOrder.visibility = View.INVISIBLE
        val animate = TranslateAnimation(
                0F,
                0F,
                0F,
                -binding.layoutOrder.height.toFloat())

        animate.duration = slidingDuration
        animate.fillAfter = true
        binding.layoutOrder.startAnimation(animate)
    }

    private fun placeSortingOut(){
        binding.layoutOrder.visibility = View.INVISIBLE
        binding.layoutOrder.layout(
            0,
            -binding.layoutOrder.height,
            binding.layoutOrder.width,
            0)
    }

}