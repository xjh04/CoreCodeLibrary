package com.jxdx.corecodelibrary.demo1

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jxdx.corecodelibrary.databinding.ItemRecyclerviewBinding

class RecyclerViewAdapter(private val data :List<RecyclerData>) :RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    class MyViewHolder1(itemView: ItemRecyclerviewBinding) : RecyclerView.ViewHolder(itemView.root) {
        val textView = itemView.text
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder1(ItemRecyclerviewBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as MyViewHolder1
        holder.textView.text = data[position].data
    }

    override fun getItemCount(): Int {
        return data.size
    }
}