package com.example.myapp016asharedtasklist

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp016asharedtasklist.databinding.ItemTaskBinding
import android.widget.EditText
import android.widget.LinearLayout

class TaskAdapter(
    private val tasks: List<Task>, // Seznam úkolů
    private val onTaskChecked: (Task) -> Unit // Callback pro změnu stavu úkolu
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(private val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            // Nastavení názvu úkolu
            binding.tvTaskName.text = task.name

            // Nastavení jména, pokud je přiřazeno
            binding.tvAssignedTo.text = if (task.assignedTo.isNotEmpty()) {
                "Assigned to: ${task.assignedTo}"
            } else {
                "Assigned to: None"
            }

            // Nastavení aktuálního stavu checkboxu
            binding.cbTaskCompleted.setOnCheckedChangeListener(null) // Odstranění starého listeneru
            binding.cbTaskCompleted.isChecked = task.isCompleted // Správné nastavení stavu checkboxu
            Log.d("FIREBASE", "Binding task: ${task.name}, isCompleted: ${task.isCompleted}")

            // Listener pro změnu stavu checkboxu
            binding.cbTaskCompleted.setOnCheckedChangeListener { _, isChecked ->
                if (task.isCompleted != isChecked) { // Pokud se stav opravdu mění
                    task.isCompleted = isChecked
                    onTaskChecked(task) // Callback pro aktualizaci ve Firestore
                }
            }

            // Kliknutí na celý úkol – možnost přiřazení
            binding.root.setOnClickListener {
                if (task.assignedTo.isEmpty()) {
                    showAssignDialog(task)
                }
            }
        }


        private fun showAssignDialog(task: Task) {
            val builder = AlertDialog.Builder(itemView.context)
            builder.setTitle("Assign Task")

            val input = EditText(itemView.context).apply {
                hint = "Enter your name"
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            builder.setView(input)

            builder.setPositiveButton("Assign") { _, _ ->
                val name = input.text.toString()
                if (name.isNotBlank()) {
                    task.assignedTo = name
                    binding.tvAssignedTo.text = "Assigned to: ${task.assignedTo}"
                    onTaskChecked(task)
                }
            }

            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

            builder.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    override fun getItemCount(): Int = tasks.size
}


