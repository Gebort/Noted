package com.example.noted.ui.add_edit_note

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat.is24HourFormat
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
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*


class AddEditNoteFragment : Fragment() {

    private var _binding: FragmentAddEditNoteBinding? = null
    private val binding get() = _binding!!

    private val model: AddEditNoteViewModel by activityViewModels()

    private var backgroundColor: Int = R.color.black

    private var colorTransitionTime = 250L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activity?.title = resources.getString(R.string.note_edit)

        _binding = FragmentAddEditNoteBinding.inflate(inflater, container, false)
        val view = binding.root

        val args: AddEditNoteFragmentArgs by navArgs()
        model.setCurrentNoteId(args.id)

        binding.progressSlider.setLabelFormatter { value ->
            DecimalFormat("##%").format(value/100)
        }

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

        binding.starButton.setOnClickListener{
            model.onEvent(AddEditNoteEvent.ToggleFavourite)
        }

        binding.progressSlider.addOnChangeListener { _, value, _ ->
            model.onEvent(AddEditNoteEvent.EnteredProgress(value))
        }

        activity?.let {
            model.progress.observe(it, { value ->
                if (value != binding.progressSlider.value){
                    binding.progressSlider.value = value
                }
            })
        }

        activity?.let{
            model.favourite.observe(it, { favourite ->
                binding.starButton.setImageResource(if (favourite) R.drawable.star_icon else R.drawable.star_border_icon)
            })
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

        activity?.let{
            model.timestamp.observe(it, { timestamp ->
                val instant = Instant.ofEpochMilli(timestamp)
                val timeStr = DateTimeFormatter.ofPattern("HH.mm").format(ZonedDateTime.ofInstant(instant, ZoneOffset.UTC))
                if (timeStr != binding.timePickerInput.text.toString()) {
                    binding.timePickerInput.setText(timeStr)
                }
            })
        }

        activity?.let{
            model.datestamp.observe(it, { datestamp ->
                val instant = Instant.ofEpochMilli(datestamp)
                val dateStr = DateTimeFormatter.ofPattern("dd.MM.yyyy").format(ZonedDateTime.ofInstant(instant, ZoneId.systemDefault()))
                if (dateStr != binding.datePickerInput.text.toString()) {
                    binding.datePickerInput.setText(dateStr)
                }
            })
        }

        binding.datePickerInput.setOnFocusChangeListener { _, b ->
            if (b) {
                val constraintsBuilder =
                        CalendarConstraints.Builder()
                                .setValidator(DateValidatorPointForward.now())
                val picker =
                        MaterialDatePicker.Builder.datePicker()
                                .setTitleText(R.string.pick_date)
                                .setSelection(model.datestamp.value)
                                .setCalendarConstraints(constraintsBuilder.build())
                                .build()

                picker.addOnPositiveButtonClickListener { date: Long ->
                    model.onEvent(AddEditNoteEvent.ChangedDatestamp(date))
                }

                fragmentManager?.let { it1 -> picker.show(it1, "datePicker") }
                binding.datePickerInput.isActivated = false
                binding.datePickerInput.clearFocus()
            }
        }

         binding.timePickerInput.setOnFocusChangeListener { _, b ->
            if (b) {
                val isSystem24Hour = is24HourFormat(activity?.applicationContext)
                val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

                val picker =
                        MaterialTimePicker.Builder()
                                .setTimeFormat(clockFormat)
                                .setTitleText(R.string.pick_time)
                                .build()

                picker.addOnPositiveButtonClickListener {
                    val timestamp = LocalTime.of(picker.hour, picker.minute).toSecondOfDay()*1000L
                    val instant = Instant.ofEpochMilli(timestamp)
                    model.onEvent(AddEditNoteEvent.ChangedTimestamp(timestamp))
                }

                fragmentManager?.let { it1 -> picker.show(it1, "timePicker") }
                binding.timePickerInput.isActivated = false
                binding.timePickerInput.clearFocus()
            }
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
            model.progress.removeObservers(it)
            model.favourite.removeObservers(it)
            model.timestamp.removeObservers(it)
            model.datestamp.removeObservers(it)
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