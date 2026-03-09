package com.example.taskmanager.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.taskmanager.R
import com.example.taskmanager.databinding.FragmentDetailsBinding

class DetailsFragment : Fragment(R.layout.fragment_details) {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetailsBinding.bind(view)

        val taskId = arguments?.getInt("taskId") ?: -1

        viewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
            val task = tasks.find { it.id == taskId }
            task?.let {
                binding.textViewDetailTitle.text = it.title
                binding.textViewDetailDescription.text = it.description

                val (priorityLabel, priorityColor) = when (it.priority) {
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
            }
        }

        binding.buttonDelete.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Delete") { _, _ ->
                    val id = arguments?.getInt("taskId") ?: -1
                    viewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
                        val taskToDelete = tasks.find { it.id == id }
                        taskToDelete?.let {
                            viewModel.delete(it)
                            findNavController().navigateUp()
                        }
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
