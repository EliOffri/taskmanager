package com.example.taskmanager.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.R
import com.example.taskmanager.databinding.FragmentMainBinding

class MainFragment : Fragment(R.layout.fragment_main) {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainBinding.bind(view)

        setupRecyclerView()

        binding.fabAddTask.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_addEditFragment)
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val task = (binding.recyclerViewTasks.adapter as TaskAdapter).getTaskAt(position)

                viewModel.delete(task)
                Toast.makeText(requireContext(), "Task deleted", Toast.LENGTH_SHORT).show()
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.recyclerViewTasks)
    }

    private fun setupRecyclerView() {
        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(requireContext())

        viewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
            if (tasks.isEmpty()) {
                binding.recyclerViewTasks.visibility = View.GONE
                binding.layoutEmptyState.visibility = View.VISIBLE
            } else {
                binding.layoutEmptyState.visibility = View.GONE
                binding.recyclerViewTasks.visibility = View.VISIBLE

                val adapter = TaskAdapter(tasks) { task ->
                    val bundle = Bundle().apply { putInt("taskId", task.id) }
                    findNavController().navigate(R.id.action_mainFragment_to_detailsFragment, bundle)
                }
                binding.recyclerViewTasks.adapter = adapter
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}