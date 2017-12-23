package com.example.kitoha.myapplication

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Debug
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.SparseBooleanArray
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_board.*


class Board : AppCompatActivity() {

    lateinit var list: ArrayList<ListData>
    lateinit var add_list:MutableList<ListData>
    lateinit var adapter: CAdapter
    var now_delete:Boolean=false
    val database:FirebaseDatabase= FirebaseDatabase.getInstance()
    val myRef:DatabaseReference=database.getReference()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)
        val editsearch:EditText=findViewById(R.id.input)
        val writebtn:Button=findViewById(R.id.write)
        val deletebtn:Button=findViewById(R.id.delete)

        list = ArrayList()

        add_list = mutableListOf<ListData>()

        settinglist()

        list.addAll(add_list as List<ListData>)

        adapter = CAdapter(this,list)

        listview.adapter=adapter

        listview.setOnItemClickListener { parent, view, position, id ->
            val list_info:ListData=parent.adapter.getItem(position) as ListData
            val intent:Intent=Intent(this,ReadBoard::class.java)
            intent.putExtra("read_title",list_info.mTitle)
            startActivity(intent)

        }

        editsearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val text:String=editsearch.getText().toString()
                search(text)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        writebtn.setOnClickListener {
            val intent : Intent = Intent(this,WriteBoard::class.java)
            startActivity(intent)
        }

        deletebtn.setOnClickListener {
            //val checkedItem:SparseBooleanArray=listview.getCheckedItemPositions()
            val checkedItem:SparseBooleanArray=listview.checkedItemPositions
            var count:Int=adapter.getCount()
           // Toast.makeText(this,"count "+count.toString(),Toast.LENGTH_SHORT).show()
            count=count-1;
            for(i in 0..count){
                if(checkedItem.get(i)){
                   // Toast.makeText(this,"선택됨 "+i.toString(),Toast.LENGTH_SHORT).show()
                    val query:Query=myRef.child("BoardInform").orderByChild("date").equalTo(list.get(i).mDate)
                    list.removeAt(i)
                    query.addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onCancelled(p0: DatabaseError?) {

                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot?) {
                            for(snapshot:DataSnapshot in dataSnapshot!!.getChildren()){
                                snapshot.getRef().removeValue()
                                now_delete=true
                            }
                        }

                    })
                   // list.removeAt(i)
                }
            }
            listview.clearChoices()
            adapter.notifyDataSetChanged()
        }

        // Read from the database
        myRef.child("BoardInform").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if(now_delete){
                    now_delete=false
                }
                else {
                    Log.d("zzzz","zzzz")
                    list.clear()
                    for (snapshot: DataSnapshot in dataSnapshot.getChildren()) {
                        var list_info: ListData = ListData()
                        var get_data: BoardData = snapshot.getValue<BoardData>(BoardData::class.java)!!
                        list_info.mTitle = get_data.title
                        list_info.mIcon = getResources().getDrawable(R.drawable.ic_launcher_background)
                        list_info.mDate = get_data.date
                        list.add(list_info)
                        add_list.add(list_info)
                    }
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
            }
        })
    }

    fun search(text:String){
        list.clear()

        if(text.length==null){
            //add_list.addAll(list)
            list.addAll(add_list)
        }
        else{
            Log.d("Debug","검색 중 "+text)
            for(s in add_list){
                Log.d("Debug","반복")
                if(s.getTitle()!!.contains(text)){
                    Log.d("Debug","검색완료")
                    list.add(s)
                }
            }
        }
        adapter.notifyDataSetChanged()
    }

    fun settinglist() {
      //  add_list.add(ListData(getResources().getDrawable(R.drawable.ic_launcher_background),"Text1","2017-12-01"))
     //   add_list.add(ListData(getResources().getDrawable(R.drawable.ic_launcher_background),"kite2","2017-12-01"))
     //   add_list.add(ListData(getResources().getDrawable(R.drawable.ic_launcher_background),"sse3","2017-12-01"))
      //  add_list.add(ListData(getResources().getDrawable(R.drawable.ic_launcher_background),"krt4","2017-12-01"))
     //   add_list.add(ListData(getResources().getDrawable(R.drawable.ic_launcher_background),"ou5","2017-12-01"))
    }


}
