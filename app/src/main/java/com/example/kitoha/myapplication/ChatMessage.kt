package com.example.kitoha.myapplication

/**
 * Created by kitoha on 2017-12-23.
 */
class ChatMessage(way:Boolean? =null,message:String ? = null) {
    var message:String ? =null
    var way:Boolean ?= null
    var User_id:String ?=null
    var User_nickname:String ? =null

    init {
        this.message=message
        this.way=way
    }

}