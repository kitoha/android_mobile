package com.example.kitoha.myapplication

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn1: Button = findViewById(R.id.button1)
        val btn2: Button = findViewById(R.id.go_chat)

        btn1.setOnClickListener {
            val intent : Intent = Intent(this,Board::class.java)
            startActivity(intent)
        }

        btn2.setOnClickListener {
            val intent : Intent = Intent(this,Chat::class.java)
            startActivity(intent)
        }
    }


}
