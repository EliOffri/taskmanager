package com.example.taskmanager.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.R
import com.example.taskmanager.databinding.ItemTaskBinding
import com.example.taskmanager.model.Task

class TaskAdapter(
    private val onItemClick: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Task>() {
            override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem == newItem
        }
    }

    inner class TaskViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        val context = holder.itemView.context

        holder.binding.textViewTitle.text = task.title
        holder.binding.textViewDescription.text = task.description

        val colorRes = when (task.priority) {
            1    -> R.color.priority_high
            2    -> R.color.priority_medium
            3    -> R.color.priority_low
            else -> R.color.priority_medium
        }
        holder.binding.viewPriorityIndicator.setBackgroundColor(
            ContextCompat.getColor(context, colorRes)
        )

        holder.itemView.setOnClickListener { onItemClick(task) }
    }

    fun getTaskAt(position: Int): Task = getItem(position)
}
