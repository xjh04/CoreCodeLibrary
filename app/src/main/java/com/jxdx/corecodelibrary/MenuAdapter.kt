package com.jxdx.corecodelibrary

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.jxdx.corecodelibrary.databinding.MenuItemBinding
import com.jxdx.corecodelibrary.demo1.MainActivity1
import com.jxdx.corecodelibrary.demo2.MainActivity2

class MenuAdapter(private val context: Context,private val data :List<String>) : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {
    class ViewHolder(itemView: MenuItemBinding) : RecyclerView.ViewHolder(itemView.root) {
        val activityName: TextView = itemView.activityName
        val card : CardView = itemView.card
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(MenuItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.activityName.text = data[position]
        holder.card.setOnClickListener{
            val intent : Intent
            when(position){
                0 ->{
                    intent = Intent(context,MainActivity1::class.java)
                    context.startActivity(intent)
                }
                1 ->{
                    intent = Intent(context, MainActivity2::class.java)
                    context.startActivity(intent)
                }
            }
        }
    }
}