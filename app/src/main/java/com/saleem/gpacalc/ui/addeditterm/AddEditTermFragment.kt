package com.saleem.gpacalc.ui.addeditterm

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.saleem.gpacalc.R
import com.saleem.gpacalc.databinding.FragmentAddEditTermBinding
import com.saleem.gpacalc.databinding.FragmentTermBinding
import com.saleem.gpacalc.util.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddEditTermFragment : Fragment(R.layout.fragment_add_edit_term) {

    private val viewModel: AddEditTermViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAddEditTermBinding.bind(view)

        binding.apply {
            etName.setText(viewModel.termName)

            etName.addTextChangedListener {
                viewModel.termName = it.toString()
            }
            tvGpa.isVisible = viewModel.term != null
            tvGpa.text = getString(R.string.term_gpa_details, viewModel.term?.gpa)
            fabAddEdit.setOnClickListener {
                viewModel.onSaveClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditTermEvent.collect { event ->
                when (event) {
                    is AddEditTermViewModel.AddEditTermEvent.NavigateBackWithResult -> {
                        binding.etName.clearFocus()
                        setFragmentResult(
                            "add_edit_term_request",
                            bundleOf("add_edit_term_result" to event.result)
                        )
                        findNavController().popBackStack()
                    }
                    is AddEditTermViewModel.AddEditTermEvent.ShowInvalidInputMessage -> {
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
}