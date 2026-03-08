package com.example.taskmanager.ui

import android.os.Bundle
import android.view.View
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
                binding.textViewDetailPriority.text = "Priority: ${it.priority}"
            }
        }

        binding.buttonDelete.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Delete") { _, _ ->
                    val taskId = arguments?.getInt("taskId") ?: -1

                    viewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
                        val taskToDelete = tasks.find { it.id == taskId }
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