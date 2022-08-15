package cn.lancet.socketdemo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.lancet.socketdemo.data.Message
import cn.lancet.socketdemo.databinding.LayoutMessageItemBinding

/**
 * Created by Lancet at 2022/8/15 09:21
 */
class MessageAdapter(private val messages:MutableList<Message>) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutMessageItemBinding.inflate(LayoutInflater.from(parent.context), parent,false))

    override fun getItemCount() = messages.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]
        if (message.type == 1){
            holder.mView.tvLeftMsg.text = message.message
        }else{
            holder.mView.tvRightMsg.text = message.message
        }
        holder.mView.ivLeftAvatar.visibility = if (message.type == 1) View.VISIBLE else View.INVISIBLE
        holder.mView.tvLeftMsg.visibility = if (message.type == 1) View.VISIBLE else View.GONE
        holder.mView.ivRightAvatar.visibility = if (message.type == 1) View.INVISIBLE else View.VISIBLE
        holder.mView.tvRightMsg.visibility = if (message.type == 1) View.GONE else View.VISIBLE
    }

    class ViewHolder(itemView:LayoutMessageItemBinding) : RecyclerView.ViewHolder(itemView.root){
        var mView:LayoutMessageItemBinding
        init { mView = itemView}
    }

}