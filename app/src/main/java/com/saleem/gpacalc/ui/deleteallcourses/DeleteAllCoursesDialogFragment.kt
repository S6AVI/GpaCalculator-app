package com.saleem.gpacalc.ui.deleteallcourses

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.saleem.gpacalc.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteAllCoursesDialogFragment : DialogFragment() {

    private val viewModel: DeleteAllCoursesViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.confirm_delete))
            .setMessage(getString(R.string.delete_question))
            .setNegativeButton(getString(R.string.cancel), null)
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.onConfirmClick()
            }.create()


}