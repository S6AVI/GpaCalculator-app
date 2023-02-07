package com.saleem.gpacalc.ui.subject

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.saleem.gpacalc.R
import com.saleem.gpacalc.data.Course
import com.saleem.gpacalc.databinding.FragmentSubjectBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubjectFragment: Fragment(R.layout.fragment_subject), SubjectAdapter.OnItemClickListener {

    private val viewModel: SubjectViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentSubjectBinding.bind(view)

        val subjectAdapter = SubjectAdapter(this)

        binding.apply {
            recyclerView.apply {
                adapter = subjectAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        viewModel.courses.observe(viewLifecycleOwner) {
            subjectAdapter.submitList(it)
        }
    }

    override fun onItemClick(course: Course) {
        TODO("Not yet implemented")
    }

}