package com.rb95.msgcountdragbombdemo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * @des
 * @author RenBing
 * @date 2020/7/2 0002
 */
class MyAdapter(val context: Context) : RecyclerView.Adapter<MyAdapter.ViewHolder>(){

    private val needRemoveList = arrayListOf<Int>()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv.text = "$position"
        holder.content.text = "第${position}个item"
        if (needRemoveList.contains(position)){
            holder.tv.visibility = View.GONE
        }else{
            holder.tv.visibility = View.VISIBLE
            holder.tv.text = "$position"
        }
        val dragView = LayoutInflater.from(context).inflate(R.layout.includeview,null,false)
        val tv = dragView.findViewById<TextView>(R.id.tv_dragView)
        tv.text = "$position"
        PointViewControl(context,dragView,holder.tv,object : PointViewControl.DragStatusListener{
            override fun inScope() {
                notifyDataSetChanged()
            }

            override fun outScope() {
                needRemoveList.add(position)
                notifyDataSetChanged()
            }

        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_layout,parent,false))
    }

    override fun getItemCount(): Int {
        return 20
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val tv = itemView.findViewById<TextView>(R.id.text)
        val content = itemView.findViewById<TextView>(R.id.content)
    }
}