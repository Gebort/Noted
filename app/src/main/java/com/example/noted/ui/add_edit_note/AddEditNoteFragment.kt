package com.example.noted.ui.add_edit_note

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.noted.R
import com.example.noted.databinding.FragmentAddEditNoteBinding
import com.example.noted.presentation.add_edit_note.AddEditNoteEvent
import com.example.noted.presentation.add_edit_note.AddEditNoteViewModel
import com.example.noted.presentation.add_edit_note.AddEditNoteUiEvent
import com.google.android.material.snackbar.Snackbar


class AddEditNoteFragment : Fragment() {

    private var _binding: FragmentAddEditNoteBinding? = null
    private val binding get() = _binding!!

    private val model: AddEditNoteViewModel by activityViewModels()

    private var backgroundColor: Int = R.color.black

    private var colorTransitionTime = 250L

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activity?.title = resources.getString(R.string.note_edit)

        _binding = FragmentAddEditNoteBinding.inflate(inflater, container, false)
        val view = binding.root

        val args: AddEditNoteFragmentArgs by navArgs()
        model.setCurrentNoteId(args.id)

        binding.buttonBlue.setOnClickListener{
            model.onEvent(AddEditNoteEvent.ChangedColor(R.color.blue_200))
        }

        binding.buttonGreen.setOnClickListener{
            model.onEvent(AddEditNoteEvent.ChangedColor(R.color.green_200))
        }

        binding.buttonOrange.setOnClickListener{
            model.onEvent(AddEditNoteEvent.ChangedColor(R.color.orange_200))
        }

        binding.buttonPurple.setOnClickListener{
            model.onEvent(AddEditNoteEvent.ChangedColor(R.color.purple_200))
        }

        binding.buttonRed.setOnClickListener{
            model.onEvent(AddEditNoteEvent.ChangedColor(R.color.red_200))
        }

        binding.buttonTeal.setOnClickListener{
            model.onEvent(AddEditNoteEvent.ChangedColor(R.color.teal_200))
        }

        activity?.let {
            model.noteColor.observe(it, { color ->
                changeColor(color)
                backgroundColor = color
                when (color) {
                    R.color.purple_200 -> {
                        pressColorButton(binding.buttonPurple)
                    }
                    R.color.blue_200 -> {
                        pressColorButton(binding.buttonBlue)
                    }
                    R.color.red_200 -> {
                        pressColorButton(binding.buttonRed)
                    }
                    R.color.green_200 -> {
                        pressColorButton(binding.buttonGreen)
                    }
                    R.color.teal_200 -> {
                        pressColorButton(binding.buttonTeal)
                    }
                    R.color.orange_200 -> {
                        pressColorButton(binding.buttonOrange)
                    }
                }
            })
        }

        binding.textContent.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                model.onEvent(AddEditNoteEvent.EnteredContent(s.toString()))
            }
        })

        activity?.let {
            model.noteContent.observe(it, { content ->
                if (content != binding.textContent.text.toString()) {
                    binding.textContent.setText(content)
                }
            })
        }

        binding.textTitle.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                model.onEvent(AddEditNoteEvent.EnteredTitle(s.toString()))
            }
        })

        activity?.let {
            model.noteTitle.observe(it, { title ->
                if (title != binding.textTitle.text.toString()) {
                    binding.textTitle.setText(title)
                }
            })
        }

        activity?.let {
            model.uiEvent.observe(it, { uiEvent ->
                if (uiEvent != null && !uiEvent.handled) {
                    model.handledEvent(uiEvent)
                    when (uiEvent) {
                        is AddEditNoteUiEvent.FailedToSave -> {
                            this.view?.let { it1 -> Snackbar.make(it1, resources.getString(R.string.couldnt_save_note), Snackbar.LENGTH_SHORT).show() }
                        }
                        is AddEditNoteUiEvent.SavedNote -> {
                            this.view?.let { it1 -> Snackbar.make(it1, resources.getString(R.string.note_saved), Snackbar.LENGTH_SHORT).show() }
                            findNavController().navigateUp()
                        }
                    }
                }
            })
        }

        binding.fabAddEdit.setOnClickListener {
            model.onEvent(AddEditNoteEvent.SaveNote)
        }


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.action_sort).isVisible = false
        super.onPrepareOptionsMenu(menu)
    }

    override fun onDestroyView() {
        _binding = null
        activity?.let {
            model.noteColor.removeObservers(it)
            model.noteTitle.removeObservers(it)
            model.noteContent.removeObservers(it)
            model.uiEvent.removeObservers(it)
        }
        super.onDestroyView()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun pressColorButton(button: ImageButton){
        binding.buttonPurple.foreground = null
        binding.buttonRed.foreground = null
        binding.buttonOrange.foreground = null
        binding.buttonGreen.foreground = null
        binding.buttonTeal.foreground = null
        binding.buttonBlue.foreground = null
        button.foreground = resources.getDrawable(R.drawable.border_color_button)
    }

    private fun changeColor(color: Int){
        activity?.let {
            val colorFrom = ContextCompat.getColor(it.applicationContext, backgroundColor)
            val colorTo = ContextCompat.getColor(it.applicationContext, color)
            val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
            colorAnimation.duration = colorTransitionTime

            colorAnimation.addUpdateListener { animator -> this.view?.setBackgroundColor(animator.animatedValue as Int) }
            colorAnimation.start()
        }

    }

}