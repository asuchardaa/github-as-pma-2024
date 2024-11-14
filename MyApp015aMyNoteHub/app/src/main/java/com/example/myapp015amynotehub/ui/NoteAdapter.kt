package com.example.myapp015amynotehub.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp015amynotehub.databinding.ItemNoteBinding
import com.example.myapp015amynotehub.models.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoteAdapter(
    private val notes: List<Note>,
    private val onDeleteClick: (Note) -> Unit,
    private val onEditClick: (Note) -> Unit,
    private val getCategoryName: suspend (Int?) -> String,
    private val getTagsForNote: suspend (Int) -> List<String>,
    private val lifecycleScope: LifecycleCoroutineScope // Přidání lifecycleScope
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun getItemCount() = notes.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.bind(note)
    }

    inner class NoteViewHolder(private val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(note: Note) {
            binding.noteTitle.text = note.title
            binding.noteContentPreview.text = note.content

            // Použití lifecycleScope pro asynchronní načítání dat
            lifecycleScope.launch {
                val categoryName = withContext(Dispatchers.IO) { getCategoryName(note.categoryId) }
                val tags = withContext(Dispatchers.IO) { getTagsForNote(note.id) }

                binding.noteCategory.text = categoryName
                binding.noteTags.text = if (tags.isNotEmpty()) tags.joinToString(", ") else "Žádné štítky"
            }

            binding.iconDelete.setOnClickListener {
                AlertDialog.Builder(itemView.context)
                    .setTitle("Smazat poznámku")
                    .setMessage("Opravdu chcete tuto poznámku smazat?")
                    .setPositiveButton("Ano") { _, _ ->
                        onDeleteClick(note)  // Vyvolání funkce pro mazání poznámky
                    }
                    .setNegativeButton("Ne", null)
                    .show()
            }

            binding.iconEdit.setOnClickListener {
                onEditClick(note)
            }
        }
    }
}
