package com.saleem.gpacalc.ui.subject

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.saleem.gpacalc.data.Course
import com.saleem.gpacalc.databinding.ItemSubjectBinding

class SubjectAdapter(private val listener: OnItemClickListener): ListAdapter<Course, SubjectAdapter.SubjectViewHolder>(DiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val binding = ItemSubjectBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return SubjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class SubjectViewHolder(private val binding: ItemSubjectBinding): RecyclerView.ViewHolder(binding.root) {


        fun bind(course: Course) {
            binding.apply {
                tvName.text = course.name
                tvCreditHours.text = course.credit_hours.toString()
                tvGrade.text = course.grade
            }
        }
    }


    interface OnItemClickListener {
        fun onItemClick(course: Course)

    }

    class DiffCallback: DiffUtil.ItemCallback<Course>() {
        // compare id fields (== operator)
        override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean =
            oldItem.id == newItem.id

        // when the content has changed (equals)
        override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean =
            oldItem == newItem

    }
}