package com.example.taskmanager.ui

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.taskmanager.R
import com.example.taskmanager.databinding.FragmentAddEditBinding
import com.example.taskmanager.model.Task
import java.io.File
import java.io.FileOutputStream

class AddEditFragment : Fragment(R.layout.fragment_add_edit) {

    private var _binding: FragmentAddEditBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskViewModel by viewModels()
    private var selectedPriority: Int = 2
    private var savedImagePath: String? = null

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            savedImagePath = copyImageToInternalStorage(requireContext(), uri)
            binding.imageViewTask.isVisible = true
            binding.imageViewTask.load(File(savedImagePath!!)) {
                crossfade(true)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddEditBinding.bind(view)

        setupPriorityDropdown()

        binding.buttonPickImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.buttonSave.setOnClickListener {
            saveTask()
        }
    }

    private fun setupPriorityDropdown() {
        val items = arrayOf(
            getString(R.string.priority_item_high),
            getString(R.string.priority_item_medium),
            getString(R.string.priority_item_low)
        )
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
            binding.editTextTitle.error = getString(R.string.error_title_empty)
            binding.editTextTitle.requestFocus()
            return
        }

        val finalDesc = if (desc.isEmpty()) getString(R.string.default_description) else desc
        val task = Task(
            title = title,
            description = finalDesc,
            priority = selectedPriority,
            imagePath = savedImagePath
        )

        viewModel.insert(task)
        Toast.makeText(requireContext(), R.string.toast_task_saved, Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    private fun copyImageToInternalStorage(context: Context, uri: Uri): String {
        val file = File(context.filesDir, "task_image_${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        return file.absolutePath
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
