package com.example.noted.ui.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.noted.R
import com.example.noted.domain.model.Note
import com.google.android.material.slider.Slider
import java.text.DecimalFormat
import java.time.*
import java.time.format.DateTimeFormatter

class NotesListAdapter(
        var notes: List<Note>,
        private val recyclerView: RecyclerView,
        private val deleteNote: (Note) -> Unit,
        private val changeNote: (Int) -> Unit,
        private val labelFavourite: (Note) -> Unit
):
    ListAdapter<Note, NotesListAdapter.ItemViewHolder>(DiffCallback()){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
                return ItemViewHolder(
                        LayoutInflater.from(parent.context)
                                .inflate(R.layout.note_item, parent, false)
                )
        }

        override fun onBindViewHolder(holder: NotesListAdapter.ItemViewHolder, position: Int) {
                holder.bind(
                        getItem(position),
                        recyclerView,
                        deleteNote,
                        changeNote,
                        labelFavourite,
                )
        }

        class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
                private val title: TextView? = itemView.findViewById(R.id.titleText)
                private val content: TextView? = itemView.findViewById(R.id.descText)
                private val deleteButton: ImageButton? = itemView.findViewById(R.id.deleteButton)
                private val starButton: ImageButton? = itemView.findViewById(R.id.starButton)
                private val progressSlider: Slider? = itemView.findViewById(R.id.progressSlider)
                private val cardView: CardView? = itemView.findViewById(R.id.cardView)
                private val dateText: TextView? = itemView.findViewById(R.id.dateText)
                private val timeText: TextView? = itemView.findViewById(R.id.timeText)

                fun bind(
                        item: Note,
                        recyclerView: RecyclerView,
                        deleteNote: (Note) -> Unit,
                        changeNote: (Int) -> Unit,
                        labelFavourite: (Note) -> Unit
                ) = with(itemView) {
                        title?.text = item.title
                        cardView?.setCardBackgroundColor(ContextCompat.getColor(recyclerView.context, item.color))
                        content?.text = item.content
                        progressSlider?.value = item.progress
                        val instant = Instant.ofEpochMilli(item.timestamp)
                        var dateSnap = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
                        timeText?.text  = DateTimeFormatter.ofPattern("HH.mm").format(dateSnap)
                        dateText?.text  = DateTimeFormatter.ofPattern("dd.MM.yyyy").format(dateSnap)

                        starButton?.setImageResource(if (item.favourite) R.drawable.star_icon else R.drawable.star_border_icon)

                        deleteButton?.setOnClickListener {
                                deleteNote(item)
                        }

                        starButton?.setOnClickListener {
                                labelFavourite(item)
                        }

                        cardView?.setOnClickListener {
                                item.id?.let { id -> changeNote(id) }
                        }

                }
        }
}

        class DiffCallback : DiffUtil.ItemCallback<Note>() {
                override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
                        return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem == newItem
        }
}
