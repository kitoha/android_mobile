package com.example.kitoha.myapplication

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.SparseBooleanArray
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_board.*

class DelBoard : AppCompatActivity() {
    lateinit var list: ArrayList<ListData>
    lateinit var add_list:MutableList<ListData>
    lateinit var adapter: DAdapter
    val user: FirebaseUser = FirebaseAuth.getInstance().getCurrentUser() as FirebaseUser
    val database:FirebaseDatabase= FirebaseDatabase.getInstance()
    val myRef: DatabaseReference =database.getReference()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_del_board)

        val del_btn: Button = findViewById(R.id.del_btn)

        list = ArrayList()

        add_list = mutableListOf<ListData>()

        list.addAll(add_list as List<ListData>)

        adapter = DAdapter(this, list)

        listview.adapter = adapter

        del_btn.setOnClickListener {
            val checkedItem: SparseBooleanArray = listview.checkedItemPositions
            var count: Int = adapter.getCount()
            // Toast.makeText(this,"count "+count.toString(),Toast.LENGTH_SHORT).show()
            count = count - 1;
            for (i in 0..count) {
                if (checkedItem.get(i)) {
                    // Toast.makeText(this,"선택됨 "+i.toString(),Toast.LENGTH_SHORT).show()
                    val current_user = list.get(i).mId

                    if (current_user != user.uid.toString()) {
                        Toast.makeText(this, "다른 사람이 쓴 글은 삭제가 불가능합니다. ".toString(), Toast.LENGTH_SHORT).show()
                    } else {
                        val query: Query = myRef.child("BoardInform").orderByChild("date").equalTo(list.get(i).mDate)
                        list.removeAt(i)
                        query.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError?) {

                            }

                            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                                for (snapshot: DataSnapshot in dataSnapshot!!.getChildren()) {
                                    snapshot.getRef().removeValue()
                                    // now_delete = true
                                }
                            }

                        })
                    }
                    // list.removeAt(i)
                }
            }
            listview.clearChoices()
            adapter.notifyDataSetChanged()

        }
        myRef.child("BoardInform").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                    list.clear()
                    for (snapshot: DataSnapshot in dataSnapshot.getChildren()) {
                        var list_info: ListData = ListData()
                        var get_data: BoardData = snapshot.getValue<BoardData>(BoardData::class.java)!!
                        list_info.mTitle = get_data.title
                        list_info.mIcon = getResources().getDrawable(R.drawable.ic_launcher_background)
                        list_info.mDate = get_data.date
                        list_info.mId=get_data.user_id
                        list.add(list_info)
                        add_list.add(list_info)
                    }
                    adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
            }
        })
    }

}
