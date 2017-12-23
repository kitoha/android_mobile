package com.example.kitoha.myapplication

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import org.w3c.dom.Text

/**
 * Created by kitoha on 2017-12-01.
 */
class CAdapter(context: Context,list:ArrayList<ListData>) : BaseAdapter() {

    var informList:ArrayList<ListData>?=null
    var inflator : LayoutInflater?=null

    init {
        informList=list
        inflator= LayoutInflater.from(context)
    }

    override fun getView(position: Int, covertView: View?, parent: ViewGroup?): View {
        var view:View?=null

        view=inflator?.inflate(R.layout.listview_item,parent,false)

        var imgPicture:ImageView
        var txtDescription:TextView
        var txtDate:TextView

        imgPicture = view?.findViewById<ImageView>(R.id.Picture) as ImageView
        txtDescription=view?.findViewById<TextView>(R.id.mText) as TextView
        txtDate=view?.findViewById<TextView>(R.id.Date) as TextView

        var inf: ListData
        inf= getItem(position)!!

        imgPicture.setImageResource(R.drawable.ic_launcher_background)
        txtDescription.setText(inf.mTitle)
        txtDate.setText(inf.mDate)

        return view!!
    }

    override fun getItem(position: Int): ListData?{
        return informList?.get(position)

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()

    }

    override fun getCount(): Int {
        return informList!!.size
    }

}