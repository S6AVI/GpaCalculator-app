package com.saleem.gpacalc.ui.course

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.saleem.gpacalc.data.Course
import com.saleem.gpacalc.databinding.ItemCourseBinding


class CourseAdapter(private val listener: OnItemClickListener): ListAdapter<Course, CourseAdapter.CourseViewHolder>(DiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val binding = ItemCourseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return CourseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class CourseViewHolder(private val binding: ItemCourseBinding): RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val course = getItem(position)
                        listener.onItemClick(course)
                    }

                }
            }
        }

        fun bind(course: Course) {
            binding.apply {
                tvName.text = course.name
                tvCreditHours.text = course.credit_hours.toString()
                tvTermGpa.text = course.grade
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