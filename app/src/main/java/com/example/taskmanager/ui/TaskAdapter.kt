package com.example.taskmanager.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.databinding.ItemTaskBinding
import com.example.taskmanager.model.Task

class TaskAdapter(
    private val tasks: List<Task>,
    private val onItemClick: (Task) -> Unit
    ) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = tasks[position]
        holder.binding.textViewTitle.text = currentTask.title
        holder.binding.textViewDescription.text = currentTask.description

        val color = when (currentTask.priority) {
            1 -> android.graphics.Color.parseColor("#E53935") // High: Material Red
            2 -> android.graphics.Color.parseColor("#FDD835") // Medium: Material Yellow
            3 -> android.graphics.Color.parseColor("#43A047") // Low: Material Green
            else -> android.graphics.Color.GRAY            // Default
        }

        holder.binding.viewPriorityIndicator.setBackgroundColor(color)

        holder.itemView.setOnClickListener { onItemClick(currentTask) }
    }

    override fun getItemCount() = tasks.size

    fun getTaskAt(position: Int): Task {
        return tasks[position]
    }
}