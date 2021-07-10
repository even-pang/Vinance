package com.project.vinance.view.recycler

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import com.project.vinance.R
import android.view.View

class RecycleFuturePosition : RecyclerView.Adapter<RecycleFuturePosition.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycle_position, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {}

    /** 리사이클 개수  */
    override fun getItemCount(): Int {
        return 1
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}