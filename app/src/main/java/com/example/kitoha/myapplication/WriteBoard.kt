package com.example.kitoha.myapplication

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*


class WriteBoard : AppCompatActivity() {

    val database:FirebaseDatabase = FirebaseDatabase.getInstance()
    //val myRef:DatabaseReference = database.getReference("BoardInform")
    val myRef:DatabaseReference = database.getReference()
    lateinit var title:String
    lateinit var text:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_board)

        val titleinfo:EditText=findViewById(R.id.titleinfo)
        val textinfo:EditText=findViewById(R.id.textinfo)
        val up_btn:Button=findViewById(R.id.up_write)

        up_btn.setOnClickListener {
            val intent: Intent = Intent(this,Board::class.java)
            title=titleinfo.getText().toString()
            text=textinfo.getText().toString()
            var info:BoardData= BoardData()
            info.title=title
            val cal:Calendar= Calendar.getInstance()
            val year:String=cal.get(Calendar.YEAR).toString()
            val month:String=(cal.get(Calendar.MONTH)+1).toString()
            val day:String=cal.get(Calendar.DAY_OF_MONTH).toString()
            val hour:String=cal.get(Calendar.HOUR_OF_DAY).toString()
            val min:String=cal.get(Calendar.MINUTE).toString()
            val sec:String=cal.get(Calendar.SECOND).toString()
            val now_data:String=year+"-"+month+"-"+day+" "+hour+":"+min+":"+sec
            info.date=now_data
            info.description=text

            Log.d("디버깅 중",title)
            myRef.child("BoardInform").push().setValue(info)

           // myRef.setValue(title)
           // myRef.child(title).setValue(text)
            intent.putExtra("title",title)
            startActivity(intent)
        }


        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
            }
        })
    }

}
