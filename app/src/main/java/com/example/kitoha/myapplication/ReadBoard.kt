package com.example.kitoha.myapplication

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.firebase.database.*

class ReadBoard : AppCompatActivity() {

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val ReadInfo: DatabaseReference =database.getReference()
    lateinit var title:String
    lateinit var til_txt:TextView
    lateinit var des_txt:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_board)
        val intent: Intent =getIntent()
        title=intent.getStringExtra("read_title")

        til_txt=findViewById(R.id.read_title)
        des_txt=findViewById(R.id.read_inform)


        ReadInfo.child("BoardInform").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(snapshot:DataSnapshot in dataSnapshot.getChildren()){
                    var get_data:BoardData=snapshot.getValue<BoardData>(BoardData::class.java)!!
                    if(get_data.title!!.equals(title)){
                        til_txt.setText(get_data.title)
                        des_txt.setText(get_data.description)
                        break
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
            }

        })

    }
}
