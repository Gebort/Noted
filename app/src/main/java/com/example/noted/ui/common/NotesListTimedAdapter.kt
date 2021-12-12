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
import com.example.noted.ui.common.DataItem
import com.google.android.material.slider.Slider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

private const val ITEM_VIEW_TYPE_HEADER = -1
private const val ITEM_VIEW_TYPE_ITEM = -2

class NotesListTimedAdapter(
        private val recyclerView: RecyclerView,
        private val deleteNote: (Note) -> Unit,
        private val changeNote: (Int) -> Unit,
        private val labelFavourite: (Note) -> Unit
):
        ListAdapter<DataItem, RecyclerView.ViewHolder>(TimedDiffCallback()){

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> HeaderViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.header_item, parent, false))
            ITEM_VIEW_TYPE_ITEM -> ItemViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.note_item, parent, false))
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.NoteItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItemViewHolder -> {
                val noteItem = getItem(position) as DataItem.NoteItem
                holder.bind(
                        noteItem.note,
                        recyclerView,
                        deleteNote,
                        changeNote,
                        labelFavourite,
                )
            }
            is HeaderViewHolder -> {
                val headerItem = getItem(position) as DataItem.Header
                holder.bind(headerItem.dayOfWeek)
            }
        }
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

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val headerText: TextView? = itemView.findViewById(R.id.headerText)

        fun bind(
                dayOfWeek: Int,
        ) = with(itemView) {
            val dayStr = when (dayOfWeek){
                1 -> resources.getString(R.string.monday)
                2 -> resources.getString(R.string.tuesday)
                3 -> resources.getString(R.string.wednesday)
                4 -> resources.getString(R.string.thursday)
                5 -> resources.getString(R.string.friday)
                6 -> resources.getString(R.string.saturday)
                7 -> resources.getString(R.string.sunday)
                else -> resources.getString(R.string.error_day)
            }
            headerText?.text = dayStr
        }
    }

    fun addHeaderAndSumbitList(list: List<Note>?) {
        adapterScope.launch {
            val items =
                if (list != null){
                    val groupedList = list.groupBy {
                        ZonedDateTime.ofInstant(Instant.ofEpochMilli(it.timestamp), ZoneId.systemDefault()).dayOfWeek.value
                    }
                    var myList = ArrayList<DataItem>()

                    for(i in groupedList.keys){
                        myList.add(DataItem.Header(i))
                        for(v in groupedList.getValue(i)){
                            myList.add(DataItem.NoteItem(v))
                        }
                    }

                    myList
                }
                else {
                    listOf()
                }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }
}

class TimedDiffCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}

