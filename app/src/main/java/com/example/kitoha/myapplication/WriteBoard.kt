package com.example.kitoha.myapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.media.ExifInterface
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Debug
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_write_board.*
import java.io.IOException
import java.util.*
import android.support.annotation.NonNull
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.storage.UploadTask
import com.google.android.gms.tasks.OnSuccessListener
import java.io.ByteArrayOutputStream
import java.io.File


class WriteBoard : AppCompatActivity() {

    val database:FirebaseDatabase = FirebaseDatabase.getInstance()
    val myRef:DatabaseReference = database.getReference()
    val mStorageRef : StorageReference = FirebaseStorage.getInstance().getReference()
    val user: FirebaseUser = FirebaseAuth.getInstance().getCurrentUser() as FirebaseUser

    var names = arrayListOf<String>()
    var adapter : ArrayAdapter<String>? =  null
    var bitmap : Bitmap? = null

    var album_date:String?=null
    var album_uri:Uri?=null
    var take_on:Int = 0
    lateinit var title:String
    lateinit var text:String

    private val GALLERY_REQUEST_MODE = 98

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_board)

        val titleinfo:EditText=findViewById(R.id.titleinfo)
        val textinfo:EditText=findViewById(R.id.textinfo)
        val up_btn:Button=findViewById(R.id.up_write)
        val take_btn:Button=findViewById(R.id.take_picture)


        adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,names)
        adapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        imageView.setImageBitmap(bitmap)
        imageNames.adapter = adapter

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
            info.user_id=user.uid
            if(take_on==1) {
                info.imagename = imageNames.selectedItem.toString()
            }
            else if(take_on==2){
                album_date=now_data
                Toast.makeText(this, album_uri.toString(), Toast.LENGTH_SHORT).show()
                info.imagename=now_data
                sendPicture(album_uri!!)
            }
            Log.d("디버깅 중",title)
            myRef.child("BoardInform").push().setValue(info)

            // myRef.setValue(title)
            // myRef.child(title).setValue(text)
            intent.putExtra("title",title)
            startActivity(intent)
        }

        take_btn.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST_MODE)
        }

        // Read from the database
        download_button.setOnClickListener{
            Toast.makeText(this, "사진을 다운로드 합니다.", Toast.LENGTH_SHORT).show()
            take_on=1
            val imageName = imageNames.selectedItem.toString()
            val downloadRef : StorageReference = mStorageRef.child("images/" + imageName + ".PNG")

            val ONE_MEGABYTE : Long = 1024 * 1024

            downloadRef.getBytes(ONE_MEGABYTE)
                    .addOnSuccessListener({ bytes->
                        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null)
                        imageView.setImageBitmap(bitmap)
                        Toast.makeText(this, "사진 다운로드에 성공했습니다.", Toast.LENGTH_SHORT).show()
                    })
                    .addOnFailureListener({
                        Toast.makeText(this, "사진 다운로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    })
        }

        myRef.child("images").addValueEventListener(object : ValueEventListener {  //바뀔때 마다 호출
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                names.clear()
                for (snapshot in dataSnapshot.children) {
                    names.add(snapshot.key.toString())
                }

                adapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // 읽기에 실패
                Log.w("--------", "Failed to read value.", error.toException())
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {
        //카메라로 사진을 찍은 후
        //갤러리에서 사진을 가져온 후
            1->{
                if(data!=null){
                    val bundle:Bundle=data.extras
                    val bitmap:Bitmap=bundle.getParcelable("data")
                    imageView.setImageBitmap(bitmap)
                    album_uri=getImageUri(this,bitmap)
                    Toast.makeText(this, "불러오기 완료", Toast.LENGTH_SHORT).show()
                }
            }
            GALLERY_REQUEST_MODE->{
                if (resultCode == Activity.RESULT_OK && data != null) {
                    try {
                        val uri:Uri = data.data;
                       //album_uri=uri
                        take_on=2
                        val CropIntent:Intent=Intent("com.android.camera.action.CROP")
                        CropIntent.setDataAndType(uri,"image/*")
                        CropIntent.putExtra("crop","true")
                        CropIntent.putExtra("outputX",270)
                        CropIntent.putExtra("outputY",170)
                        CropIntent.putExtra("aspectX",3)
                        CropIntent.putExtra("aspectY",5)
                        CropIntent.putExtra("scaleUpIfNeeded",true)
                        CropIntent.putExtra("return-data",true)

                        startActivityForResult(CropIntent,1)

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            else -> {
                Toast.makeText(this, "unrecognized request", Toast.LENGTH_SHORT).show()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    fun sendPicture(uri:Uri){
        val timeStamp : String = album_date!!
        val uploadRef : StorageReference = mStorageRef.child("images/" + timeStamp + ".jpg")

        uploadRef.putFile(uri)
                .addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                    // Get a URL to the uploaded content
                    val downloadUrl = taskSnapshot.downloadUrl
                })
                .addOnFailureListener(OnFailureListener {
                    // Handle unsuccessful uploads
                    // ...
                })
    }

    fun getImageUri(inContext:Context,image:Bitmap):Uri{
        val bytes:ByteArrayOutputStream= ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG,100,bytes)
        val path:String=MediaStore.Images.Media.insertImage(inContext.getContentResolver(),image,"Title",null)
        return Uri.parse(path)
    }

}