package com.example.myapp016asharedtasklist

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapp016asharedtasklist.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val tasks = mutableListOf<Task>() // Lokální seznam úkolů
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializace Firebase
        FirebaseApp.initializeApp(this)
        firestore = FirebaseFirestore.getInstance()

        // Inicializace RecyclerView
        taskAdapter = TaskAdapter(tasks) { task ->
            updateTaskCompletion(task) // Callback pro změnu stavu úkolu
        }
        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewTasks.adapter = taskAdapter

        // Nastavení logiky pro FloatingActionButton
        binding.fabAddTask.setOnClickListener {
            showAddTaskDialog()
        }

        listenToTaskUpdates()
    }

    private fun updateTaskCompletion(task: Task) {
        // Aktualizace hodnoty ve Firestore
        firestore.collection("tasks").document(task.id)
            .update(mapOf("isCompleted" to task.isCompleted))
            .addOnSuccessListener {
                Log.i(
                    "FIREBASE",
                    "Task updated successfully: ${task.name}, isCompleted: ${task.isCompleted}"
                )
            }
            .addOnFailureListener { e ->
                Log.e("FIREBASE", "Error updating task: ${e.message}")
                // Pokud selže aktualizace, vrátit původní stav
                task.isCompleted = !task.isCompleted
                taskAdapter.notifyDataSetChanged()
            }
    }


    private fun showAddTaskDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add Task")

        // Vytvoření vstupního layoutu
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        val inputName = EditText(this).apply {
            hint = "Task name"
            inputType = InputType.TYPE_CLASS_TEXT
        }
        val inputAssignedTo = EditText(this).apply {
            hint = "Your name (optional)"
            inputType = InputType.TYPE_CLASS_TEXT
        }

        layout.addView(inputName)
        layout.addView(inputAssignedTo)
        builder.setView(layout)

        builder.setPositiveButton("Add") { _, _ ->
            val taskName = inputName.text.toString()
            val assignedTo = inputAssignedTo.text.toString()

            if (taskName.isNotBlank()) {
                addTask(taskName, assignedTo)
            } else {
                Toast.makeText(this, "Task name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun addTask(name: String, assignedTo: String) {
        val newTask = Task(
            id = firestore.collection("tasks").document().id, // Vygenerujeme ID
            name = name,
            isCompleted = false, // Výchozí hodnota
            assignedTo = assignedTo
        )

        firestore.collection("tasks").document(newTask.id).set(newTask)
            .addOnSuccessListener {
                Log.i("FIREBASE", "Task added to Firestore: $name")
            }
            .addOnFailureListener { e ->
                Log.e("FIREBASE", "Error adding task: ${e.message}")
            }
    }

    private fun listenToTaskUpdates() {
        firestore.collection("tasks").addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.e("FIREBASE", "Listen failed: ${e.message}")
                return@addSnapshotListener
            }

            if (snapshots != null) {
                tasks.clear() // Vymažeme staré úkoly
                for (document in snapshots.documents) {
                    val task = document.toObject(Task::class.java)
                    if (task != null) {
                        // Doplníme výchozí hodnotu pro isCompleted, pokud chybí
                        if (!document.contains("isCompleted")) {
                            task.isCompleted = false
                            Log.d("FIREBASE", "Document missing 'isCompleted', defaulting to false")
                        }
                        tasks.add(task)
                        Log.d("FIREBASE", "Loaded task: ${task.name}, isCompleted: ${task.isCompleted}")
                    } else {
                        Log.e("FIREBASE", "Failed to parse document: ${document.id}")
                    }
                }
                taskAdapter.notifyDataSetChanged() // Aktualizace RecyclerView
            }
        }
    }

}
