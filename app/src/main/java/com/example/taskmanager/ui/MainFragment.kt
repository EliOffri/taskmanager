package com.example.taskmanager.ui

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.R
import com.example.taskmanager.databinding.FragmentMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class MainFragment : Fragment(R.layout.fragment_main) {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskViewModel by viewModels()
    private lateinit var adapter: TaskAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainBinding.bind(view)

        setupRecyclerView()

        binding.fabAddTask.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_addEditFragment)
        }
    }

    private fun setupRecyclerView() {
        adapter = TaskAdapter { task ->
            val bundle = Bundle().apply { putInt("taskId", task.id) }
            findNavController().navigate(R.id.action_mainFragment_to_detailsFragment, bundle)
        }

        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewTasks.adapter = adapter

        setupSwipeToDelete()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allTasks.collect { tasks ->
                    adapter.submitList(tasks)
                    binding.layoutEmptyState.visibility = if (tasks.isEmpty()) View.VISIBLE else View.GONE
                    binding.recyclerViewTasks.visibility = if (tasks.isEmpty()) View.GONE else View.VISIBLE
                }
            }
        }
    }

    private fun setupSwipeToDelete() {
        val deleteBackground = ColorDrawable(Color.RED)
        val editBackground = ColorDrawable(Color.parseColor("#2563EB"))
        val deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete)!!
        val editIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_edit)!!
        deleteIcon.setTint(Color.WHITE)
        editIcon.setTint(Color.WHITE)

        val swipeCallback = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                rv: RecyclerView,
                vh: RecyclerView.ViewHolder,
                t: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val task = adapter.getTaskAt(position)

                when (direction) {
                    ItemTouchHelper.RIGHT -> {
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle(R.string.dialog_delete_title)
                            .setMessage(R.string.dialog_delete_message)
                            .setPositiveButton(R.string.dialog_delete_confirm) { _, _ ->
                                viewModel.delete(task)
                            }
                            .setNegativeButton(R.string.cancel) { _, _ ->
                                adapter.notifyItemChanged(position)
                            }
                            .setOnCancelListener {
                                adapter.notifyItemChanged(position)
                            }
                            .show()
                    }
                    ItemTouchHelper.LEFT -> {
                        adapter.notifyItemChanged(position)
                        val bundle = Bundle().apply { putInt("taskId", task.id) }
                        findNavController().navigate(R.id.action_mainFragment_to_addEditFragment, bundle)
                    }
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView

                if (dX > 0) {
                    val iconMargin = (itemView.height - deleteIcon.intrinsicHeight) / 2
                    val iconTop = itemView.top + iconMargin
                    val iconBottom = iconTop + deleteIcon.intrinsicHeight

                    deleteBackground.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
                    deleteBackground.draw(c)

                    val iconLeft = itemView.left + iconMargin
                    val iconRight = iconLeft + deleteIcon.intrinsicWidth
                    deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    deleteIcon.draw(c)
                } else if (dX < 0) {
                    val iconMargin = (itemView.height - editIcon.intrinsicHeight) / 2
                    val iconTop = itemView.top + iconMargin
                    val iconBottom = iconTop + editIcon.intrinsicHeight

                    editBackground.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                    editBackground.draw(c)

                    val iconRight = itemView.right - iconMargin
                    val iconLeft = iconRight - editIcon.intrinsicWidth
                    editIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    editIcon.draw(c)
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        ItemTouchHelper(swipeCallback).attachToRecyclerView(binding.recyclerViewTasks)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
