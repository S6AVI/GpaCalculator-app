package com.saleem.gpacalc.ui.addeditsubject

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.saleem.gpacalc.R
import com.saleem.gpacalc.databinding.FragmentAddEditSubjectBinding
import com.saleem.gpacalc.util.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddEditSubjectFragment : Fragment(R.layout.fragment_add_edit_subject) {

    private val viewModel: AddEditSubjectViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAddEditSubjectBinding.bind(view)

        binding.apply {
            etName.setText(viewModel.courseName)
            etHours.setText(viewModel.courseHours)
            etGrade.setText(viewModel.courseGrade)

            etName.addTextChangedListener {
                viewModel.courseName = it.toString()
            }

            etGrade.addTextChangedListener {
                viewModel.courseGrade = it.toString()
            }

            etHours.addTextChangedListener {
                viewModel.courseHours = it.toString()
            }

            fabAddEdit.setOnClickListener {
                viewModel.onSaveClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditTaskEvent.collect { event ->
                when (event) {
                    is AddEditSubjectViewModel.AddEditCourseEvent.NavigateBackWithResult -> {
                        binding.etGrade.clearFocus()
                        setFragmentResult(
                            "add_edit_request",
                            bundleOf("add_edit_result" to event.result)
                        )
                        findNavController().popBackStack()
                    }
                    is AddEditSubjectViewModel.AddEditCourseEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(
                            requireView(),
                            event.msg,
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }.exhaustive
            }
        }


    }
}