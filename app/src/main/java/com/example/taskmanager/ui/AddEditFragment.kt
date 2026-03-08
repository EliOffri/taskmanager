package com.example.taskmanager.ui

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.taskmanager.R
import com.example.taskmanager.databinding.FragmentAddEditBinding
import com.example.taskmanager.model.Task

class AddEditFragment : Fragment(R.layout.fragment_add_edit) {

    private var _binding: FragmentAddEditBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskViewModel by viewModels()
    private var selectedPriority: Int = 3

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddEditBinding.bind(view)

        setupPriorityDropdown()

        binding.buttonSave.setOnClickListener {
            saveTask()
        }
    }

    private fun setupPriorityDropdown() {
        val items = arrayOf("High", "Medium", "Low")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, items)
        binding.autoCompletePriority.setAdapter(adapter)

        binding.autoCompletePriority.setOnItemClickListener { _, _, position, _ ->
            selectedPriority = position + 1
        }
    }

    private fun saveTask() {
        val title = binding.editTextTitle.text.toString().trim()
        val desc = binding.editTextDescription.text.toString().trim()

        binding.editTextTitle.error = null

        if (title.isEmpty()) {
            binding.editTextTitle.error = "Title cannot be empty"
            binding.editTextTitle.requestFocus()
            return
        }

        val finalDesc = if (desc.isEmpty()) "No description provided" else desc
        val task = Task(title = title, description = finalDesc, priority = selectedPriority)

        viewModel.insert(task)

        Toast.makeText(requireContext(), "Task saved successfully", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}