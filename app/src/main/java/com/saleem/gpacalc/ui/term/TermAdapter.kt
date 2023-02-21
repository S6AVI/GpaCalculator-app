package com.saleem.gpacalc.ui.term

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.saleem.gpacalc.data.Term
import com.saleem.gpacalc.databinding.ItemTermBinding

class TermAdapter(private val listener: OnItemClickListener): ListAdapter<Term, TermAdapter.TermViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TermViewHolder {
        val binding = ItemTermBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TermViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TermViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class TermViewHolder(val binding: ItemTermBinding): RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val term = getItem(position)
                        listener.onItemClick(term)
                    }
                }
            }
        }

        fun bind(term: Term) {
            binding.apply {
                tvName.text = term.name
                tvTermGpa.isVisible = term.gpa != 0.0
                tvTermGpa.text = term.gpa.toString()
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(term: Term)
    }

    class DiffCallback: DiffUtil.ItemCallback<Term>()  {
        override fun areItemsTheSame(oldItem: Term, newItem: Term): Boolean =
            oldItem.termId == newItem.termId


        override fun areContentsTheSame(oldItem: Term, newItem: Term): Boolean =
            oldItem == newItem

    }


}