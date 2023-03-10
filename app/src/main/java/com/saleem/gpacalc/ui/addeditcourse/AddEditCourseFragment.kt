package com.saleem.gpacalc.ui.addeditcourse

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.saleem.gpacalc.R
import com.saleem.gpacalc.databinding.FragmentAddEditCourseBinding

import com.saleem.gpacalc.util.exhaustive
import com.saleem.gpacalc.util.hours
import com.saleem.gpacalc.util.possibleGrades
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddEditCourseFragment : Fragment(R.layout.fragment_add_edit_course) {

    private val viewModel: AddEditCourseViewModel by viewModels()
    private lateinit var binding: FragmentAddEditCourseBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAddEditCourseBinding.bind(view)



        binding.apply {
            etName.setText(viewModel.courseName)

            acTvHours.setText(viewModel.courseHours)
            acTvGrade.setText(viewModel.courseGrade)

            etName.addTextChangedListener {
                viewModel.courseName = it.toString()
            }


            fabAddEdit.setOnClickListener {
                viewModel.onSaveClick()
            }


            acTvHours.setOnItemClickListener { parent, _, position, _ ->
                val hours = parent.getItemAtPosition(position)
                viewModel.courseHours = hours.toString()
            }

            acTvGrade.setOnItemClickListener { parent, _, position, _ ->
                val grade = parent.getItemAtPosition(position)
                viewModel.courseGrade = grade.toString()
            }


        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditCourseEvent.collect { event ->
                when (event) {
                    is AddEditCourseViewModel.AddEditCourseEvent.NavigateBackWithResult -> {

                        setFragmentResult(
                            "add_edit_request",
                            bundleOf("add_edit_result" to event.result)
                        )
                        findNavController().popBackStack()
                    }
                    is AddEditCourseViewModel.AddEditCourseEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(
                            requireView(),
                            event.uiText.asString(requireContext()),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }.exhaustive
            }
        }


    }

    override fun onResume() {
        super.onResume()
        val hoursAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, hours)
        binding.acTvHours.setAdapter(hoursAdapter)

        val gradesAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, possibleGrades)
        binding.acTvGrade.setAdapter(gradesAdapter)
    }
}