package com.delightroom.reminder.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.delightroom.reminder.data.Remind
import com.delightroom.reminder.databinding.ListItemRemindBinding
import java.text.SimpleDateFormat
import java.util.*

//todo mj https://developer.android.google.cn/codelabs/basic-android-kotlin-training-intro-room-flow?hl=ko#9
class RemindListAdapter(private val onItemClicked: (Remind) -> Unit) :
    ListAdapter<Remind, RemindListAdapter.RemindViewHolder>(DiffCallback) {

    companion object{
        private val DiffCallback = object : DiffUtil.ItemCallback<Remind>() {
            override fun areItemsTheSame(oldItem: Remind, newItem: Remind): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Remind, newItem: Remind): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RemindViewHolder {
        val viewHolder = RemindViewHolder(
            ListItemRemindBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        viewHolder.itemView.setOnClickListener{
            val position = viewHolder.adapterPosition
            onItemClicked(getItem(position))
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: RemindViewHolder, position: Int) {
        holder.bindViewHolder(getItem(position))
    }

    class RemindViewHolder(private val binding: ListItemRemindBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SimpleDateFormat")
        fun bindViewHolder(remind: Remind) {
            binding.txtName.text = remind.name
            binding.txtTime.text = SimpleDateFormat("h:mm a").format(Date(remind.time.toLong() * 1000))
            binding.checkbox.isChecked = remind.isDone
        }
    }
}