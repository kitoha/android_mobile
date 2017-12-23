package com.example.kitoha.myapplication

import android.graphics.drawable.Drawable

/**
 * Created by kitoha on 2017-12-01.
 */
class ListData(val get_Icon:Drawable?=null,val get_Title:String?=null,val get_Date:String?=null) {
    var mIcon:Drawable?=null
    var mTitle:String?=null
    var mDate:String?=null

    init {
        this.mIcon=get_Icon
        this.mTitle=get_Title
        this.mDate=get_Date
    }

    fun getTitle():String?{
        return this.mTitle
    }
}