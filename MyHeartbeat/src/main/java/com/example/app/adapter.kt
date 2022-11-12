package com.example.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class adapter(private val userlist: ArrayList<user>): RecyclerView.Adapter<adapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent:ViewGroup, viewType:Int): adapter.MyViewHolder{
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: adapter.MyViewHolder, position: Int){
        val user: user = userlist[position]
        holder.name_curr.text = user.name
        holder.date_and_time.text = user.datetime
        holder.bpm_curr.text = user.bpm
    }

    override fun getItemCount(): Int {
        return userlist.size
    }

    public class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val name_curr: TextView = itemView.findViewById(R.id.tvname)
        val date_and_time: TextView = itemView.findViewById(R.id.tvdate_time)
        val bpm_curr: TextView = itemView.findViewById(R.id.tvbpm)
    }
}