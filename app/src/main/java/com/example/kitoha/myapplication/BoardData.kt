package com.example.kitoha.myapplication

/**
 * Created by kitoha on 2017-12-03.
 */
class BoardData(get_Title:String?=null,val get_Date:String?=null,get_description:String?=null,imagename:String?=null) {

    var title:String?=null
    var date:String?=null
    var description:String?=null
    var user_id:String?=null
    var imagename : String?=null

    init {
        this.title=get_Title
        this.date=get_Date
        this.description=get_description
    }
}