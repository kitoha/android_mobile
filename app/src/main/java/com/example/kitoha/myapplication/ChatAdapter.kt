package com.example.kitoha.myapplication

import android.content.Context
import android.media.Image
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import org.w3c.dom.Text

/**
 * Created by kitoha on 2017-12-23.
 */
class ChatAdapter(context: Context,list:ArrayList<ChatMessage>) : BaseAdapter() {

    var msg:ArrayList<ChatMessage>?=null
    var inflator : LayoutInflater?=null

    init {
        msg=list
        inflator= LayoutInflater.from(context)
    }

    override fun getView(position: Int, covertView: View?, parent: ViewGroup?): View {
        var view:View?=null

        view=inflator?.inflate(R.layout.chat_item,parent,false)

        var chatText:TextView
        val layout:LinearLayout=view?.findViewById<LinearLayout>(R.id.singleMessageContainer) as LinearLayout
        chatText=view?.findViewById<TextView>(R.id.singleMessage) as TextView


        var inf:ChatMessage
        inf= getItem(position)!!

        val send_message:String = inf.User_nickname+": "+inf.message
        chatText.setText(send_message)
        if(inf.way==true){
            chatText.setBackgroundResource(R.drawable.msg1)
            layout.setGravity(Gravity.LEFT)
        }
        else{
            chatText.setBackgroundResource(R.drawable.msg2)
            layout.setGravity(Gravity.RIGHT)
        }


        return view!!
    }

    override fun getItem(position: Int): ChatMessage?{
        return msg?.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()

    }

    override fun getCount(): Int {
        return msg!!.size
    }

}