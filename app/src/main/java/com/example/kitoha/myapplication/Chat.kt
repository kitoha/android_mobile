package com.example.kitoha.myapplication

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.AbsListView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat.*

class Chat : AppCompatActivity() {

    lateinit var chat_list:ArrayList<ChatMessage>
    lateinit var adapter:ChatAdapter
    val chat_database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val chat_myRef: DatabaseReference =chat_database.getReference()
    var pos:Boolean=false
    var do_send:Boolean=false
    val chat_user: FirebaseUser = FirebaseAuth.getInstance().getCurrentUser() as FirebaseUser
    lateinit var user_nickname:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chat_list=ArrayList()

        adapter=ChatAdapter(this,chat_list)
        val chat_msg:EditText = findViewById(R.id.chatText)
        val send:Button=findViewById(R.id.buttonSend)

        val chat_query:Query=chat_myRef.child("users").orderByChild("user_id").equalTo(chat_user.uid.toString())
        chat_query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {}
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val id_child:Iterator<DataSnapshot> = dataSnapshot.getChildren().iterator()
                val ct:LoginData=id_child.next().getValue<LoginData>(LoginData::class.java)!!
                user_nickname=ct.user_name!!
            }
        })
        listView1.adapter=adapter

        send.setOnClickListener {
            val msg:String=chat_msg.text.toString()
            do_send=true
            sendChatMessage(msg)
            chat_msg.setText("")
        }

        chat_myRef.child("Message").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (do_send == false) {
                    chat_list.clear()
                    for (snapshot: DataSnapshot in dataSnapshot.getChildren()) {
                        val get_msg: ChatMessage = snapshot.getValue<ChatMessage>(ChatMessage::class.java)!!
                        val set_msg: ChatMessage = ChatMessage()
                        set_msg.message = get_msg.message
                        set_msg.way = get_msg.User_id != chat_user.uid.toString()
                        //set_msg.way=pos
                        set_msg.User_nickname=get_msg.User_nickname
                        chat_list.add(set_msg)
                    }
                    adapter.notifyDataSetChanged()
                }
                do_send=true
                listView1.setSelection(adapter.getCount()-1)
            }
        })

        chat_myRef.child("Message").addChildEventListener(object : ChildEventListener{
            override fun onCancelled(p0: DatabaseError?) {}
            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot?) {}

            override fun onChildAdded(datasnapshot: DataSnapshot, s: String?) {
                val get_msg: ChatMessage = datasnapshot.getValue<ChatMessage>(ChatMessage::class.java)!!
                val set_msg: ChatMessage = ChatMessage()
                set_msg.message = get_msg.message
                set_msg.way = get_msg.User_id != chat_user.uid.toString()
                set_msg.User_nickname=get_msg.User_nickname
                chat_list.add(set_msg)
                adapter.notifyDataSetChanged()
            }
        })

        listView1.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL)
        listView1.adapter=adapter
        if(do_send) do_send=!do_send
    }

    fun sendChatMessage(get_msg:String):Boolean{
        val chat_msg:ChatMessage= ChatMessage()
        chat_msg.message=get_msg
        chat_msg.way=false
        chat_msg.User_id=chat_user.uid.toString()
        chat_msg.User_nickname=user_nickname
        chat_myRef.child("Message").push().setValue(chat_msg)
       // chat_list.add(chat_msg)
        //adapter.notifyDataSetChanged()
        pos=!pos
        return true
    }
}
