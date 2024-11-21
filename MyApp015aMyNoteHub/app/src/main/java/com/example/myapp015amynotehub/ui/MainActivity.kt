package com.example.myapp015amynotehub.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapp015amynotehub.R
import com.example.myapp015amynotehub.database.CategoryDao
import com.example.myapp015amynotehub.database.NoteHubDatabase
import com.example.myapp015amynotehub.database.NoteHubDatabaseInstance
import com.example.myapp015amynotehub.database.NoteTagDao
import com.example.myapp015amynotehub.databinding.ActivityMainBinding
import com.example.myapp015amynotehub.models.Category
import com.example.myapp015amynotehub.models.Note
import com.example.myapp015amynotehub.models.NoteTagCrossRef
import com.example.myapp015amynotehub.models.Tag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var database: NoteHubDatabase
    private lateinit var categoryDao: CategoryDao
    private lateinit var noteTagDao: NoteTagDao

    private var selectedCategory: String? = null
    private var selectedTags = mutableListOf<String>()
    private var currentLanguage = "cs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializace databáze
        database = NoteHubDatabaseInstance.getDatabase(this)

        // Vložení výchozích kategorií a štítků do databáze
        insertDefaultCategories()
        insertDefaultTags()

        setupCategoryFilter()
        setupTagFilter()

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

        binding.switchLanguageButton.setOnClickListener {
            switchLanguage()
        }
    }

    private fun switchLanguage() {
        currentLanguage = if (currentLanguage == "en") "cs" else "en"
        setLocale(currentLanguage)
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        // Vytvoření nového kontextu s novou konfigurací
        val context = createConfigurationContext(config)

        // Aktualizace prostředí pro binding a další prvky
        resources.updateConfiguration(config, resources.displayMetrics)

        // Obnovení textů uživatelského rozhraní
        updateUITexts()

        // Znovu načtení seznamů z databáze
        setupCategoryFilter()
        setupTagFilter()
        loadNotes()
    }

    private fun updateUITexts() {
        binding.appTitle.text = getString(R.string.app_title)
        binding.filterTagsButton.text = getString(R.string.filter_tags_button)
        binding.switchLanguageButton.text = getString(R.string.switch_language_button)
        binding.fabAddNote.contentDescription = getString(R.string.fab_add_note)
    }


    private fun setupCategoryFilter() {
        lifecycleScope.launch {
            val categories = database.categoryDao().getAllCategories().first()
            val allCategoriesText = getString(R.string.all_categories)
            val categoryNames = listOf(allCategoriesText) + categories.map { it.name }
            val categoryAdapter = ArrayAdapter(this@MainActivity, R.layout.spinner_item, categoryNames)
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.filterCategorySpinner.adapter = categoryAdapter

            binding.filterCategorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    selectedCategory = if (position == 0) null else categoryNames[position]
                    loadNotes()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    selectedCategory = null
                    loadNotes()
                }
            }
        }
    }

    private fun setupTagFilter() {
        binding.filterTagsButton.setOnClickListener {
            lifecycleScope.launch {
                val tags = database.tagDao().getAllTags().first()
                val tagNames = tags.map { it.name }.toTypedArray()
                val checkedItems = BooleanArray(tagNames.size) { i -> selectedTags.contains(tagNames[i]) }

                AlertDialog.Builder(this@MainActivity)
                    .setTitle(getString(R.string.select_tags))
                    .setMultiChoiceItems(tagNames, checkedItems) { _, which, isChecked ->
                        if (isChecked) {
                            selectedTags.add(tagNames[which])
                        } else {
                            selectedTags.remove(tagNames[which])
                        }
                    }
                    .setPositiveButton("OK") { _, _ -> loadNotes() }
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show()
            }
        }
    }

    private fun loadNotes() {
        lifecycleScope.launch {
            var notes = database.noteDao().getAllNotes().first()

            // Filtrace podle kategorie
            if (selectedCategory != null) {
                notes = notes.filter { note ->
                    val category = database.categoryDao().getAllCategories().first().find { it.id == note.categoryId }
                    category?.name == selectedCategory
                }
            }

            // Filtrace podle štítků
            if (selectedTags.isNotEmpty()) {
                notes = notes.filter { note ->
                    val noteTags = database.noteTagDao().getTagsForNote(note.id).first().map { it.name }
                    selectedTags.any { it in noteTags }
                }
            }

            noteAdapter = NoteAdapter(
                notes,
                onDeleteClick = { note -> deleteNote(note) },
                onEditClick = { note -> showEditNoteDialog(note) },
                getCategoryName = { categoryId -> getCategoryNameForNote(categoryId) },
                getTagsForNote = { noteId -> getTagsForNoteFromDatabase(noteId) },
                lifecycleScope = lifecycleScope
            )
            binding.recyclerView.adapter = noteAdapter

        }
    }

    private suspend fun getCategoryNameForNote(categoryId: Int?): String {
        return withContext(Dispatchers.IO) {
            categoryId?.let {
                database.categoryDao().getCategoryById(it)?.name ?: "Bez kategorie"
            } ?: "Bez kategorie"
        }
    }

    private suspend fun getTagsForNoteFromDatabase(noteId: Int): List<String> {
        return withContext(Dispatchers.IO) {
            val tags = database.noteTagDao().getTagsForNote(noteId).first()
            tags.map { it.name }
        }
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
                    .setTitle(getString(R.string.select_tags))
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
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show()
            }
        }

        // Zobrazení dialogu pro úpravu poznámky
        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_edit_note_title))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                val updatedTitle = titleEditText.text.toString()
                val updatedContent = contentEditText.text.toString()
                val selectedCategory = categorySpinner.selectedItem.toString()
                updateNoteInDatabase(note, updatedTitle, updatedContent, selectedCategory, selectedTags)
            }
            .setNegativeButton(getString(R.string.cancel), null)
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

            loadNotes()  // aktualizuju
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
                    .setTitle(getString(R.string.select_tags))
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
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show()
            }
        }

        // Zobrazení dialogu pro přidání poznámky
        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_add_note_title))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.fab_add_note)) { _, _ ->
                val title = titleEditText.text.toString()
                val content = contentEditText.text.toString()
                val selectedCategory = categorySpinner.selectedItem.toString()
                addNoteToDatabase(title, content, selectedCategory, selectedTags)
            }
            .setNegativeButton(getString(R.string.cancel), null)
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
            val existingCategories = database.categoryDao().getAllCategories().first()
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