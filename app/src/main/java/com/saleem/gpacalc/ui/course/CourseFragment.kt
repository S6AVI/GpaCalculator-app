package com.saleem.gpacalc.ui.course


import android.graphics.Canvas
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.saleem.gpacalc.R
import com.saleem.gpacalc.data.Course
import com.saleem.gpacalc.databinding.FragmentCourseBinding
import com.saleem.gpacalc.util.calculateGpa
import com.saleem.gpacalc.util.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class CourseFragment : Fragment(R.layout.fragment_course), CourseAdapter.OnItemClickListener {

    private val viewModel: CourseViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentCourseBinding.bind(view)

        val courseAdapter = CourseAdapter(this)

        binding.apply {
            recyclerView.apply {
                adapter = courseAdapter
                setHasFixedSize(true)
            }

            fabAdd.setOnClickListener {
                viewModel.onAddNewCourseClick()
            }

        }

        // observe changes in the list and send it to the adapter
        viewModel.courses.observe(viewLifecycleOwner) {
            courseAdapter.submitList(it)
            val termGpa = calculateGpa(it)
            binding.tvGpa.text = "Your GPA is: " + termGpa.toString()
            viewModel.updateTermGpa(termGpa)

        }

        // handle events
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.courseEvent.collect { event ->
                when (event) {
                    is CourseViewModel.CourseEvent.NavigateToAddCourseScreen -> {
                        val action =
                            CourseFragmentDirections.actionCourseFragmentToAddEditCourseFragment(
                                label = "Add Course",
                                term = event.term
                            )
                        findNavController().navigate(action)
                    }
                    is CourseViewModel.CourseEvent.NavigateToEditCourseScreen -> {
                        val action =
                            CourseFragmentDirections.actionCourseFragmentToAddEditCourseFragment(
                                course = event.course,
                                label = "Edit Course",
                                term = event.term
                            )
                        findNavController().navigate(action)
                    }
                    is CourseViewModel.CourseEvent.ShowCourseSavedConfirmationMessage -> {
                        Snackbar.make(
                            requireView(),
                            event.msg,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    is CourseViewModel.CourseEvent.ShowUndoDeleteCourseMessage -> {
                        Snackbar.make(
                            requireView(),
                            "Course Deleted",
                            Snackbar.LENGTH_LONG
                        ).setAction("UNDO") {
                            viewModel.onUndoDeleteCourse(event.course)
                        }.show()
                    }
                    is CourseViewModel.CourseEvent.NavigateToDeleteAllCoursesScreen -> {
                        val action =
                            CourseFragmentDirections.actionGlobalDeleteAllCoursesDialogFragment(
                                event.id
                            )
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
                val course = courseAdapter.currentList[viewHolder.adapterPosition]
                viewModel.onCourseSwiped(course)

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
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )

                RecyclerViewSwipeDecorator.Builder(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                    .addBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.delete_background
                        )
                    )
                    .addActionIcon(R.drawable.ic_delete)
                    .create()
                    .decorate()
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
        inflater.inflate(R.menu.menu_fragment_course, menu)

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