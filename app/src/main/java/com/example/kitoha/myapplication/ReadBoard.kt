package com.example.kitoha.myapplication

import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_write_board.*

class ReadBoard : AppCompatActivity() {

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val ReadInfo: DatabaseReference =database.getReference()
    val mStorageRef : StorageReference = FirebaseStorage.getInstance().getReference()
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
                        val imageName : String? = get_data.imagename
                        if (imageName!=null){
                            Toast.makeText(applicationContext, "images/" + imageName + ".jpg", Toast.LENGTH_SHORT).show()
                            val downloadRef : StorageReference = mStorageRef.child("images/" + imageName + ".jpg")
                            val ONE_MEGABYTE : Long = 1024 * 1024

                            downloadRef.getBytes(ONE_MEGABYTE)
                                    .addOnSuccessListener({ bytes->
                                        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null)
                                        imageView.setImageBitmap(bitmap)
                                    })
                                    .addOnFailureListener({
                                    })
                        }

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
