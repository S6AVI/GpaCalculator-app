package com.saleem.gpacalc.ui.term

import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.saleem.gpacalc.R
import com.saleem.gpacalc.data.Term
import com.saleem.gpacalc.databinding.FragmentTermBinding
import com.saleem.gpacalc.util.calculateGpa
import com.saleem.gpacalc.util.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class TermFragment : Fragment(R.layout.fragment_term), TermAdapter.OnItemClickListener {

    private val viewModel: TermViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTermBinding.bind(view)

        val termAdapter = TermAdapter(listener = this, viewModel.preferencesFlow)

        binding.apply {
            recyclerView.adapter = termAdapter


            fabAdd.setOnClickListener {
                viewModel.onAddNewTermClick()
            }
        }

        viewModel.terms.observe(viewLifecycleOwner) {
            termAdapter.submitList(it)

        }

        viewModel.preferencesFlow.observe(viewLifecycleOwner) {
            binding.tvGpa.text = getString(R.string.cumulative_gpa, viewModel.gpa)
        }

        viewModel.courses.observe(viewLifecycleOwner) {
            viewModel.gpa = calculateGpa(it)
            binding.tvGpa.isVisible = viewModel.gpa != 0.0
            binding.tvGpa.text = getString(R.string.cumulative_gpa, viewModel.gpa)
        }

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val term = termAdapter.currentList[viewHolder.bindingAdapterPosition]
                when (direction) {
                    ItemTouchHelper.LEFT -> viewModel.onTermSwipedLift(term)
                    ItemTouchHelper.RIGHT -> viewModel.onTermSwipedRight(term)
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
                    .addSwipeLeftBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.delete_background
                        )
                    )
                    .addSwipeLeftActionIcon(R.drawable.ic_delete)
                    .addSwipeLeftLabel(getString(R.string.delete))
                    .addSwipeRightBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.edit_background
                        )
                    )
                    .addSwipeRightActionIcon(R.drawable.ic_edit)
                    .addSwipeRightLabel(getString(R.string.edit))
                    .create()
                    .decorate()

            }
        }).attachToRecyclerView(binding.recyclerView)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.termEvent.collect { event ->
                when (event) {

                    TermViewModel.TermEvent.NavigateToAddTermScreen -> {
                        val action =
                            TermFragmentDirections.actionTermFragmentToAddEditTermFragment(
                                label = getString(
                                    R.string.add_term
                                )
                            )
                        findNavController().navigate(action)
                    }
                    is TermViewModel.TermEvent.ShowTermSavedConfirmationMessage -> {
                        Snackbar.make(
                            requireView(),
                            event.uiText.asString(requireContext()),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    is TermViewModel.TermEvent.NavigateToCoursesScreen -> {
                        val action = TermFragmentDirections.actionTermFragmentToCourseFragment(
                            event.term,
                            event.term.name
                        )
                        findNavController().navigate(action)
                    }

                    is TermViewModel.TermEvent.ShowUndoDeleteTermMessage -> {
                        Snackbar.make(
                            requireView(),
                            getString(R.string.delete_message),
                            Snackbar.LENGTH_LONG
                        ).setAction(getString(R.string.undo)) {
                            viewModel.onUndoDeleteTerm(event.term, event.courses)
                        }
                            .show()

                    }
                    is TermViewModel.TermEvent.NavigateToEditTermScreen -> {
                        val action =
                            TermFragmentDirections.actionTermFragmentToAddEditTermFragment(
                                event.term, label = getString(
                                    R.string.edit_term
                                )
                            )
                        findNavController().navigate(action)
                    }
                }.exhaustive
            }
        }

        setFragmentResultListener("add_edit_term_request") { _, bundle ->
            val result = bundle.getInt("add_edit_term_result")
            viewModel.onAddEditResult(result)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_term, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val action = TermFragmentDirections.actionTermFragmentToSettingsFragment()
                findNavController().navigate(action)
                true
            }
            R.id.action_about -> {
                val action = TermFragmentDirections.actionTermFragmentToAboutFragment()
                findNavController().navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    override fun onItemClick(term: Term) {
        viewModel.onTermSelected(term)
    }


}