package com.saleem.gpacalc.ui.subject

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.saleem.gpacalc.R
import com.saleem.gpacalc.data.Course
import com.saleem.gpacalc.databinding.FragmentSubjectBinding
import com.saleem.gpacalc.util.exhaustive

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SubjectFragment : Fragment(R.layout.fragment_subject), SubjectAdapter.OnItemClickListener {

    private val viewModel: SubjectViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentSubjectBinding.bind(view)

        val subjectAdapter = SubjectAdapter(this)

        binding.apply {
            recyclerView.apply {
                adapter = subjectAdapter
                // layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            //tvGpa.text = "Your GPA is: " + viewModel.calculateGpa().toString()

            fabAdd.setOnClickListener {
                viewModel.onAddNewCourseClick()
            }

        }

        // observe changes in the list and send it to the adapter
        viewModel.courses.observe(viewLifecycleOwner) {
            binding.tvGpa.text = "Your GPA is: " + viewModel.calculateGpa(it).toString()
            subjectAdapter.submitList(it)
//            binding.tvGpa.text = "Your GPA is: " + viewModel.calculateGpa().toString()

        }

        // handle events
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.subjectEvent.collect { event ->
                when (event) {
                    is SubjectViewModel.SubjectEvent.NavigateToAddSubjectScreen -> {
                        val action =
                            SubjectFragmentDirections.actionSubjectFragmentToAddEditSubjectFragment(
                                label = "Add Course"
                            )
                        findNavController().navigate(action)
                    }
                    is SubjectViewModel.SubjectEvent.NavigateToEditSubjectScreen -> {
                        val action =
                            SubjectFragmentDirections.actionSubjectFragmentToAddEditSubjectFragment(
                                event.course,
                                label = "Edit Course"
                            )
                        findNavController().navigate(action)
                    }
                    is SubjectViewModel.SubjectEvent.ShowCourseSavedConfirmationMessage -> {
                        Snackbar.make(
                            requireView(),
                            event.msg,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    is SubjectViewModel.SubjectEvent.ShowUndoDeleteCourseMessage -> {
                        Snackbar.make(
                            requireView(),
                            "Course Deleted",
                            Snackbar.LENGTH_LONG
                        ).setAction("UNDO") {
                            viewModel.onUndoDeleteCourse(event.course)
                        }.show()
                    }
                    SubjectViewModel.SubjectEvent.NavigateToDeleteAllCoursesScreen -> {
                        val action =
                            SubjectFragmentDirections.actionGlobalDeleteAllCoursesDialogFragment()
                        findNavController().navigate(action)
                    }
                }.exhaustive
            }
        }

        // get the result flag from the bundle
        setFragmentResultListener("add_edit_request") { _, bundle ->
            val result = bundle.getInt("add_edit_result")
            viewModel.onAddEditResult(result)
        }

        // swipe-to-delete functionality
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            // not needed; hence false
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            // specify the action when swiped
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val course = subjectAdapter.currentList[viewHolder.adapterPosition]
                viewModel.onCourseSwiped(course)

            }
        }).attachToRecyclerView(binding.recyclerView)

        // to display the options menu
        setHasOptionsMenu(true)
    }

    // call the viewModel method when an item is clicked; to edit the content
    override fun onItemClick(course: Course) {
        viewModel.onCourseSelected(course)
    }

    // inflate the menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_subject, menu)

    }

    // handle selection of menu items
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete_all_courses -> {
                viewModel.onDeleteAllCoursesClick()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}