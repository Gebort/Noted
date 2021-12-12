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
import com.example.noted.databinding.FragmentFavouriteNotesBinding
import com.example.noted.databinding.FragmentNotesListBinding
import com.example.noted.domain.model.Note
import com.example.noted.domain.util.NoteOrder
import com.example.noted.domain.util.OrderType
import com.example.noted.presentation.notes.FavouriteNotesViewModel
import com.example.noted.presentation.notes.NotesEvent
import com.example.noted.presentation.notes.NotesUiEvent
import com.example.noted.presentation.notes.NotesViewModel
import com.google.android.material.snackbar.Snackbar


class FavouriteNotesFragment : Fragment() {

    private var _binding: FragmentFavouriteNotesBinding? = null
    private val binding get() = _binding!!

    private val model: FavouriteNotesViewModel by activityViewModels()
    private var adapter: NotesListAdapter? = null

    private val slidingDuration = 200L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentFavouriteNotesBinding.inflate(inflater, container, false)
        val view = binding.root

        activity?.title = resources.getString(R.string.favourite)


        binding.favNotesRecyclerView.layoutManager = LinearLayoutManager(activity?.applicationContext)

        val notes = model.state.value?.notes ?: listOf()

        adapter = NotesListAdapter(
            notes,
            binding.favNotesRecyclerView,
            { note -> deleteNote(note) },
            { id -> changeNote(id)},
            { note -> labelFavourite(note)}
        )
        binding.favNotesRecyclerView.adapter = adapter

        model.state.value?.isOrderSelectionVisible?.let { visible ->
            if (visible){
                binding.sortView.layoutOrder.visibility = View.VISIBLE
            }
            else{
                placeSortingOut()
            }
        }

        binding.sortView.radioAscending.setOnClickListener {
            model.state.value?.noteOrder?.let {
                model.onEvent(NotesEvent.Order(it.copy(OrderType.Ascending)))
            }
        }

        binding.sortView.radioDescending.setOnClickListener {
            model.state.value?.noteOrder?.let {
                model.onEvent(NotesEvent.Order(it.copy(OrderType.Descending)))
            }
        }

        binding.sortView.radioColor.setOnClickListener {
            model.state.value?.noteOrder?.let {
                model.onEvent(NotesEvent.Order(NoteOrder.Color(it.orderType)))
            }
        }

        binding.sortView.radioDate.setOnClickListener {
            model.state.value?.noteOrder?.let {
                model.onEvent(NotesEvent.Order(NoteOrder.Date(it.orderType)))
            }
        }

        binding.sortView.radioTitle.setOnClickListener {
            model.state.value?.noteOrder?.let {
                model.onEvent(NotesEvent.Order(NoteOrder.Title(it.orderType)))
            }
        }

        activity?.let {
            model.state.observe(it) { state ->
                if (state.isOrderSelectionVisible != binding.sortView.layoutOrder.isVisible) {
                    if (state.isOrderSelectionVisible){
                        slideSortingDown()
                    }
                    else{
                        slideSortingUp()
                    }
                }

                when (state.noteOrder){
                    is NoteOrder.Title -> {
                        binding.sortView.radioTitle.isChecked = true
                    }

                    is NoteOrder.Date -> {
                        binding.sortView.radioDate.isChecked = true
                    }

                    is NoteOrder.Color -> {
                        binding.sortView.radioColor.isChecked = true
                    }
                }

                when (state.noteOrder.orderType){
                    is OrderType.Descending -> {
                        binding.sortView.radioDescending.isChecked = true
                    }

                    is OrderType.Ascending -> {
                        binding.sortView.radioAscending.isChecked = true
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
                            binding.favNotesRecyclerView.let { it1 -> Snackbar.make(it1, String.format(resources.getString(R.string.note_deleted), uiEvent.title), Snackbar.LENGTH_LONG)
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
            model.uiEvent.removeObservers(it)
        }
        super.onDestroyView()
    }

    private fun deleteNote(note: Note){
        model.onEvent(NotesEvent.DeleteNote(note))
    }

    private fun changeNote(id: Int){
        val action = FavouriteNotesFragmentDirections.actionFavouriteNotesFragment2ToAddEditNoteFragment()
        action.id = id
        findNavController().navigate(action)
    }

    private fun labelFavourite(note: Note){
        model.onEvent(NotesEvent.ToggleFavourite(note))
    }

    private fun slideSortingDown(){
        binding.sortView.layoutOrder.visibility = View.VISIBLE
        val animate = TranslateAnimation(
            0F,
            0F,
            -binding.sortView.layoutOrder.height.toFloat(),
            0F)

        animate.duration = slidingDuration
        animate.fillAfter = true
        binding.sortView.layoutOrder.startAnimation(animate)
    }

    private fun slideSortingUp(){
        binding.sortView.layoutOrder.visibility = View.INVISIBLE
        val animate = TranslateAnimation(
            0F,
            0F,
            0F,
            -binding.sortView.layoutOrder.height.toFloat())

        animate.duration = slidingDuration
        animate.fillAfter = true
        binding.sortView.layoutOrder.startAnimation(animate)
    }

    private fun placeSortingOut(){
        binding.sortView.layoutOrder.visibility = View.INVISIBLE
        binding.sortView.layoutOrder.layout(
            0,
            -binding.sortView.layoutOrder.height,
            binding.sortView.layoutOrder.width,
            0)
    }

}