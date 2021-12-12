package com.example.noted.ui.notes

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.noted.R
import com.example.noted.databinding.FragmentNotesDayBinding
import com.example.noted.domain.model.Note
import com.example.noted.presentation.notes.*
import com.google.android.material.snackbar.Snackbar


class NotesDayFragment : Fragment() {

    private var _binding: FragmentNotesDayBinding? = null
    private val binding get() = _binding!!

    private val model: NotesDayViewModel by activityViewModels()
    private var adapter: NotesListTimedAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesDayBinding.inflate(inflater, container, false)
        val view = binding.root

        activity?.title = resources.getString(R.string.today)


        binding.dayRecyclerView.layoutManager = LinearLayoutManager(activity?.applicationContext)

        val notes = model.state.value?.notes ?: listOf()

        adapter = NotesListTimedAdapter(
            binding.dayRecyclerView,
            { note -> deleteNote(note) },
            { id -> changeNote(id)},
            { note -> labelFavourite(note)}
        )
        binding.dayRecyclerView.adapter = adapter

        activity?.let {
            model.state.observe(it) { state ->
                adapter?.let { adapter ->
                    adapter.addHeaderAndSumbitList(state.notes)
                }
            }

            model.uiEvent.observe(it, { uiEvent ->
                if (uiEvent != null && !uiEvent.handled) {
                    when (uiEvent) {
                        is NotesUiEvent.NoteDeleted -> {
                            model.handledEvent(uiEvent)
                            binding.dayRecyclerView?.let { it1 -> Snackbar.make(it1, String.format(resources.getString(R.string.note_deleted), uiEvent.title), Snackbar.LENGTH_LONG)
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

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.action_sort).isVisible = false
        super.onPrepareOptionsMenu(menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onViewCreated(view, savedInstanceState)
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
        val action = NotesDayFragmentDirections.actionNotesDayFragmentToAddEditNoteFragment()
        action.id = id
        findNavController().navigate(action)
    }

    private fun labelFavourite(note: Note){
        model.onEvent(NotesEvent.ToggleFavourite(note))
    }

}