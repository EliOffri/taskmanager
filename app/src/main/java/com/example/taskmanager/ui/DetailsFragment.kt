package com.example.taskmanager.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.taskmanager.R
import com.example.taskmanager.databinding.FragmentDetailsBinding
import com.example.taskmanager.model.Task
import kotlinx.coroutines.launch
import java.io.File

class DetailsFragment : Fragment(R.layout.fragment_details) {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetailsBinding.bind(view)

        val taskId = arguments?.getInt("taskId") ?: -1

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allTasks.collect { tasks ->
                    val task = tasks.find { it.id == taskId }
                    task?.let { bindTask(it) }
                }
            }
        }

        binding.buttonDelete.setOnClickListener {
            showDeleteConfirmation(taskId)
        }
    }

    private fun bindTask(task: Task) {
        binding.textViewDetailTitle.text = task.title
        binding.textViewDetailDescription.text = task.description

        val (priorityLabel, priorityColor) = when (task.priority) {
            1    -> getString(R.string.priority_high)   to ContextCompat.getColor(requireContext(), R.color.priority_high)
            3    -> getString(R.string.priority_low)    to ContextCompat.getColor(requireContext(), R.color.priority_low)
            else -> getString(R.string.priority_medium) to ContextCompat.getColor(requireContext(), R.color.priority_medium)
        }

        binding.textViewDetailPriority.text = priorityLabel
        binding.textViewDetailPriority.setTextColor(priorityColor)
        binding.viewPriorityBar.setBackgroundColor(priorityColor)

        val badgeBg = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 40f
            setColor(Color.argb(40, Color.red(priorityColor), Color.green(priorityColor), Color.blue(priorityColor)))
        }
        binding.textViewDetailPriority.background = badgeBg

        if (task.imagePath != null) {
            binding.imageViewTask.isVisible = true
            binding.imageViewTask.load(File(task.imagePath)) {
                crossfade(true)
            }
        } else {
            binding.imageViewTask.isVisible = false
        }
    }

    private fun showDeleteConfirmation(taskId: Int) {
        val task = viewModel.allTasks.value.find { it.id == taskId } ?: return

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_delete_title)
            .setMessage(R.string.dialog_delete_message)
            .setPositiveButton(R.string.dialog_delete_confirm) { _, _ ->
                viewModel.delete(task)
                findNavController().navigateUp()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
