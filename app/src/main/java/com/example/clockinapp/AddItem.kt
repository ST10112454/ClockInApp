package com.example.clockinapp

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clockinapp.model.UserData
import com.example.clockinapp.view.UserAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AddItem : AppCompatActivity() {
    private lateinit var addsBtn: FloatingActionButton
    private lateinit var recv: RecyclerView
    private lateinit var userList: ArrayList<UserData>
    private lateinit var userAdapter: UserAdapter
    private lateinit var imageView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_item)

        //set list
        userList = ArrayList()
        //set find id
        addsBtn = findViewById(R.id.addingBtn)
        recv =findViewById(R.id.mRecycler)
        //set Adapter
        userAdapter = UserAdapter(this,userList)
        //setRecycler view adapter//
        recv.layoutManager = LinearLayoutManager(this)
        recv.adapter = userAdapter
        //set dialog
        addsBtn.setOnClickListener{ addInfo()}

        //defulte
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun addInfo() {
        val inflter = LayoutInflater.from(this )
        val v = inflter.inflate(R.layout.add_item,null )
        //set view
        val projectName = v.findViewById<EditText>(R.id.ProjectName)
        val description = v.findViewById<EditText>(R.id.Description )


        val addDiaglog = AlertDialog.Builder(this)
        addDiaglog.setView(v)
        addDiaglog.setPositiveButton("Ok"){
                diaglog,_->
            val nameOfP = projectName.text.toString()
            val desc = description.text.toString()

            userList.add(UserData("Project: $nameOfP", "Description :$desc" ))
            userAdapter.notifyDataSetChanged()
            Toast.makeText(this,"Adding Entry Infromation successful ", Toast.LENGTH_SHORT).show()
            diaglog.dismiss()
        }
        addDiaglog.setNegativeButton("Cancel"){
                dialog,_->
            dialog.dismiss()
            Toast.makeText(this,"Cancel", Toast.LENGTH_SHORT).show()
        }
        addDiaglog.create()
        addDiaglog.show()
    }
}