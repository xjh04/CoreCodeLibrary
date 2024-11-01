package com.jxdx.corecodelibrary

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jxdx.corecodelibrary.databinding.ItemRecyclerviewBinding

class RecyclerViewAdapter(private val context: Context, private val data :List<String>) :RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>(){
    class MyViewHolder(itemView: ItemRecyclerviewBinding) : RecyclerView.ViewHolder(itemView.root) {
        val textView = itemView.text
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemRecyclerviewBinding.inflate(LayoutInflater.from(context)))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.textView.text = data[position]
    }

    override fun getItemCount(): Int {
        return data.size
    }
}