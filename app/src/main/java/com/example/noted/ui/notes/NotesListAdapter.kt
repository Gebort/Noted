package com.example.noted.ui.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.noted.R
import com.example.noted.domain.model.Note

class NotesListAdapter(
        var notes: List<Note>,
        val recyclerView: RecyclerView,
        private val deleteNote: (Note) -> Unit,
        private val changeNote: (Int) -> Unit
        ):
    RecyclerView.Adapter<NotesListAdapter.MyViewHolder>(){

        class MyViewHolder(iv: View): RecyclerView.ViewHolder(iv){
                var title: TextView?
                var content: TextView?
                var deleteButton: ImageButton?
                var cardView: CardView?

                init {
                        title = iv.findViewById(R.id.titleText)
                        content = iv.findViewById(R.id.descText)
                        deleteButton = iv.findViewById(R.id.deleteButton)
                        cardView = iv.findViewById(R.id.cardView)
                }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
                val itemView = LayoutInflater.from(parent.context)
                                .inflate(R.layout.note_item, parent, false)
                return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(h: MyViewHolder, pos: Int) {
                h.cardView?.setCardBackgroundColor(ContextCompat.getColor(recyclerView.context, notes[pos].color))
                h.title?.text = notes[pos].title
                h.content?.text = notes[pos].content

                h.deleteButton?.setOnClickListener {
                        deleteNote(notes[pos])
                }

                h.cardView?.setOnClickListener {
                        notes[pos].id?.let { id -> changeNote(id) }
                }
        }

        override fun getItemCount(): Int {
                return notes.size
        }
}