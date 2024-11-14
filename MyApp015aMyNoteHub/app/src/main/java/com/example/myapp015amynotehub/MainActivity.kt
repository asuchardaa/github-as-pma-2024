package com.example.myapp015amynotehub

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapp015amynotehub.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var database: NoteHubDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializace databáze
        database = NoteHubDatabaseInstance.getDatabase(this)

        // Vložení výchozích kategorií a štítků do databáze
        insertDefaultCategories()
        insertDefaultTags()

        // Inicializace RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        //noteAdapter = NoteAdapter(getSampleNotes())

        //noteAdapter = NoteAdapter(emptyList()) // Inicializace s prázdným seznamem
        //binding.recyclerView.adapter = noteAdapter

        // Vložení testovacích dat
        //insertSampleNotes()

        // Načtení poznámek z databáze
        loadNotes()

        binding.fabAddNote.setOnClickListener {
            showAddNoteDialog()
        }
    }

    private fun loadNotes() {
        lifecycleScope.launch {
            database.noteDao().getAllNotes().collect { notes ->
                noteAdapter = NoteAdapter(
                    notes,
                    onDeleteClick = { note -> deleteNote(note) },
                    onEditClick = { note -> showEditNoteDialog(note) }  // Při kliknutí na editaci
                )
                binding.recyclerView.adapter = noteAdapter
            }
        }
    }


    private fun insertSampleNotes() {
        lifecycleScope.launch {
            val sampleNotes = listOf(
                Note(title = "Vzorek 1", content = "Obsah první testovací poznámky"),
                Note(title = "Vzorek 2", content = "Obsah druhé testovací poznámky"),
                Note(title = "Vzorek 3", content = "Obsah třetí testovací poznámky")
            )
            sampleNotes.forEach { database.noteDao().insert(it) }
        }
    }

    private fun getSampleNotes(): List<Note> {
        // Testovací seznam poznámek
        return listOf(
            Note(title = "Poznámka 1", content = "Obsah první poznámky"),
            Note(title = "Poznámka 2", content = "Obsah druhé poznámky"),
            Note(title = "Poznámka 3", content = "Obsah třetí poznámky")
        )
    }

    private fun showEditNoteDialog(note: Note) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_note, null)
        val titleEditText = dialogView.findViewById<EditText>(R.id.editTextTitle)
        val contentEditText = dialogView.findViewById<EditText>(R.id.editTextContent)
        val categorySpinner = dialogView.findViewById<Spinner>(R.id.spinnerCategory)
        val tagsEditText = dialogView.findViewById<EditText>(R.id.editTextTags)

        // Nastavení aktuálních hodnot poznámky
        titleEditText.setText(note.title)
        contentEditText.setText(note.content)

        // Načtení kategorií do Spinneru a nastavení aktuální kategorie
        lifecycleScope.launch {
            val categories = database.categoryDao().getAllCategories().first()
            val categoryNames = categories.map { it.name }
            val categoryAdapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, categoryNames)
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = categoryAdapter
            val currentCategory = categories.find { it.id == note.categoryId }?.name
            val categoryPosition = categoryNames.indexOf(currentCategory)
            if (categoryPosition >= 0) {
                categorySpinner.setSelection(categoryPosition)
            }
        }

        // Načtení štítků a nastavení vybraných štítků
        var selectedTags = mutableListOf<String>()
        tagsEditText.setOnClickListener {
            lifecycleScope.launch {
                val tags = database.tagDao().getAllTags().first()
                val tagNames = tags.map { it.name }.toTypedArray()
                val checkedItems = BooleanArray(tagNames.size) { i ->
                    // Označení aktuálních štítků
                    val tag = tags[i]
                    database.noteTagDao().getTagsForNote(note.id).first().any { it.id == tag.id }
                }

                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Vyberte štítky")
                    .setMultiChoiceItems(tagNames, checkedItems) { _, which, isChecked ->
                        if (isChecked) {
                            selectedTags.add(tagNames[which])
                        } else {
                            selectedTags.remove(tagNames[which])
                        }
                    }
                    .setPositiveButton("OK") { _, _ ->
                        tagsEditText.setText(selectedTags.joinToString(", "))
                    }
                    .setNegativeButton("Zrušit", null)
                    .show()
            }
        }

        // Zobrazení dialogu pro úpravu poznámky
        val dialog = AlertDialog.Builder(this)
            .setTitle("Upravit poznámku")
            .setView(dialogView)
            .setPositiveButton("Uložit") { _, _ ->
                val updatedTitle = titleEditText.text.toString()
                val updatedContent = contentEditText.text.toString()
                val selectedCategory = categorySpinner.selectedItem.toString()
                updateNoteInDatabase(note, updatedTitle, updatedContent, selectedCategory, selectedTags)
            }
            .setNegativeButton("Zrušit", null)
            .create()

        dialog.show()
    }

    private fun updateNoteInDatabase(note: Note, title: String, content: String, categoryName: String, selectedTags: List<String>) {
        lifecycleScope.launch {
            // Získání ID kategorie podle názvu
            val category = database.categoryDao().getAllCategories().first().find { it.name == categoryName }
            val categoryId = category?.id

            // Aktualizace poznámky v databázi
            val updatedNote = note.copy(title = title, content = content, categoryId = categoryId)
            database.noteDao().update(updatedNote)

            // Aktualizace štítků spojených s poznámkou
            val noteTags = database.noteTagDao().getTagsForNote(note.id).first()
            val tagNames = noteTags.map { it.name }

            // Přidání nových štítků
            selectedTags.forEach { tagName ->
                if (tagName !in tagNames) {
                    val tag = database.tagDao().getAllTags().first().find { it.name == tagName }
                    if (tag != null) {
                        database.noteTagDao().insert(NoteTagCrossRef(noteId = note.id, tagId = tag.id))
                    }
                }
            }

            // Odstranění odstraněných štítků
            noteTags.forEach { tag ->
                if (tag.name !in selectedTags) {
                    database.noteTagDao().deleteNoteTagCrossRef(NoteTagCrossRef(noteId = note.id, tagId = tag.id))
                }
            }

            loadNotes()  // Aktualizace seznamu poznámek
        }
    }



    private fun showAddNoteDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_note, null)
        val titleEditText = dialogView.findViewById<EditText>(R.id.editTextTitle)
        val contentEditText = dialogView.findViewById<EditText>(R.id.editTextContent)
        val categorySpinner = dialogView.findViewById<Spinner>(R.id.spinnerCategory)
        val tagsEditText = dialogView.findViewById<EditText>(R.id.editTextTags)

        // Načtení kategorií do Spinneru
        lifecycleScope.launch {
            val categories = database.categoryDao().getAllCategories().first()
            val categoryNames = categories.map { it.name }
            val categoryAdapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, categoryNames)
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = categoryAdapter
        }

        // MultiChoice dialog pro výběr štítků
        var selectedTags = mutableListOf<String>()
        tagsEditText.setOnClickListener {
            lifecycleScope.launch {
                val tags = database.tagDao().getAllTags().first()
                val tagNames = tags.map { it.name }.toTypedArray()
                val checkedItems = BooleanArray(tagNames.size)

                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Vyberte štítky")
                    .setMultiChoiceItems(tagNames, checkedItems) { _, which, isChecked ->
                        if (isChecked) {
                            selectedTags.add(tagNames[which])
                        } else {
                            selectedTags.remove(tagNames[which])
                        }
                    }
                    .setPositiveButton("OK") { _, _ ->
                        tagsEditText.setText(selectedTags.joinToString(", "))
                    }
                    .setNegativeButton("Zrušit", null)
                    .show()
            }
        }

        // Zobrazení dialogu pro přidání poznámky
        val dialog = AlertDialog.Builder(this)
            .setTitle("Přidat poznámku")
            .setView(dialogView)
            .setPositiveButton("Přidat") { _, _ ->
                val title = titleEditText.text.toString()
                val content = contentEditText.text.toString()
                val selectedCategory = categorySpinner.selectedItem.toString()
                addNoteToDatabase(title, content, selectedCategory, selectedTags)
            }
            .setNegativeButton("Zrušit", null)
            .create()

        dialog.show()
    }


    private fun addNoteToDatabase(title: String, content: String, categoryName: String, selectedTags: List<String>) {
        lifecycleScope.launch {
            // Získat ID kategorie podle názvu
            val category = database.categoryDao().getAllCategories().first().find { it.name == categoryName }
            val categoryId = category?.id

            // Vložit poznámku do databáze
            val newNote = Note(title = title, content = content, categoryId = categoryId)
            val noteId = database.noteDao().insert(newNote).toInt() // Vložení poznámky a převod ID na Int

            // Vložení štítků spojených s poznámkou do křížové tabulky
            selectedTags.forEach { tagName ->
                val tag = database.tagDao().getAllTags().first().find { it.name == tagName }
                if (tag != null) {
                    database.noteTagDao().insert(NoteTagCrossRef(noteId = noteId, tagId = tag.id))
                }
            }

            loadNotes()  // Aktualizuje seznam poznámek
        }
    }


    private fun deleteNote(note: Note) {
        lifecycleScope.launch {
            database.noteDao().delete(note)  // Smazání poznámky z databáze
            loadNotes()  // Aktualizace seznamu poznámek
        }
    }

    private fun insertDefaultCategories() {
        lifecycleScope.launch {
            val existingCategories = database.categoryDao().getAllCategories().first()  // Použití first() pro získání seznamu
            if (existingCategories.isEmpty()) {
                val defaultCategories = listOf(
                    Category(name = "Práce"),
                    Category(name = "Osobní"),
                    Category(name = "Nápady")
                )
                defaultCategories.forEach { database.categoryDao().insert(it) }
            }
        }
    }

    private fun insertDefaultTags() {
        lifecycleScope.launch {
            val existingTags = database.tagDao().getAllTags().first()  // Použití first() pro získání seznamu
            if (existingTags.isEmpty()) {
                val defaultTags = listOf(
                    Tag(name = "Důležité"),
                    Tag(name = "Rychlé úkoly"),
                    Tag(name = "Projekt"),
                    Tag(name = "Nápad")
                )
                defaultTags.forEach { database.tagDao().insert(it) }
            }
        }
    }


}