package com.example.kitoha.myapplication

/**
 * Created by kitoha on 2017-12-25.
 */
class LoginData(user_name:String ? = null,user_id:String ? =null) {
    var user_name:String?=null
    var user_id:String?=null

    init {
        this.user_name=user_name
        this.user_id=user_id
    }
}