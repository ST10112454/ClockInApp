package com.example.clockinapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clockinapp.model.UserData
import com.example.clockinapp.view.UserAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

data class EntryData(
    val id: String = "",
    val projectName: String = "",
    val description: String = "",
    val category: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

class EntryList : AppCompatActivity() {
    private lateinit var addingBtn:FloatingActionButton
    private lateinit var recv:RecyclerView
    private lateinit var userList:ArrayList<UserData>
    private lateinit var userAdapter:UserAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var entriesRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_entry_list)
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        //
        val userId = auth.currentUser?.uid ?: return
        /**set List*/
        userList = ArrayList()
        /**set find Id*/
        addingBtn = findViewById(R.id.addingBtn)
        recv = findViewById(R.id.mRecycler)
        /**set Adapter*/
        userAdapter = UserAdapter(this,userList)
        /**setRecycler view Adapter*/
        recv.layoutManager = LinearLayoutManager(this)
        recv.adapter = userAdapter
        /**set Dialog*/
        addingBtn.setOnClickListener { addInfo() }

        //added
        // Set Database Reference
        entriesRef = database.reference.child("entries").child(userId)

        // Fetch Data from Firebase
        fetchEntries()



        //navigation
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.bottom_EntryList

        bottomNavigationView.setOnItemSelectedListener {menuItem->
            when (menuItem.itemId){
                R.id.bottom_home ->{startActivity(Intent(applicationContext, Home::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true}
                R.id.bottom_EntryList ->
                    {true}
                R.id.bottom_AddEntry ->{startActivity(Intent(applicationContext, NewEntry::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true}
                R.id.bottom_Goal ->{startActivity(Intent(applicationContext, Report::class.java))
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    finish()
                    true}
                else -> false
            }

        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun fetchEntries() {
        entriesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (entrySnapshot in snapshot.children) {
                    val entry = entrySnapshot.getValue(EntryData::class.java)
                    if (entry != null) {
                        userList.add(UserData("Project: ${entry.projectName}", "Description: ${entry.description}"))
                    }
                }
                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EntryList, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addInfo() {
        val inflter = LayoutInflater.from(this)
        val v = inflter.inflate(R.layout.add_item,null)
        /**set view*/
        //val userName = v.findViewById<EditText>(R.id.ProjectName)
        //val userNo = v.findViewById<EditText>(R.id.Description)

        // set view
        val projectName = v.findViewById<EditText>(R.id.ProjectName)
        val description = v.findViewById<EditText>(R.id.Description)
        val category = v.findViewById<EditText>(R.id.Catagory)
        val addDialog = AlertDialog.Builder(this)
        addDialog.setView(v)
        addDialog.setPositiveButton("Ok"){
                dialog,_->
           // val names = userName.text.toString()
            //val number = userNo.text.toString()

            val cat = category.text.toString()
            val name = projectName.text.toString()
            val desc = description.text.toString()

            if (name.isEmpty() || desc.isEmpty() || cat.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            val entryId = database.reference.child("entries").push().key ?: return@setPositiveButton
            val userId = auth.currentUser?.uid ?: return@setPositiveButton
            val entry = EntryData(id = entryId, projectName = name, description = desc, category = cat)

            database.reference.child("entries").child(entryId).setValue(entry).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    userList.add(UserData("Project: $name", "Description: $desc"))
                    userAdapter.notifyDataSetChanged()
                    Toast.makeText(this, "Entry added successfully", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
            /*
            database.reference.child("entries").child(userId).child(entryId).setValue(entry).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    userList.add(UserData("Project: $name", "Description: $desc"))
                    userAdapter.notifyDataSetChanged()
                    Toast.makeText(this, "Entry added successfully", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            */
            //userList.add(UserData("Name: $names", "Description: $number"))

            //userList.add(UserData("Name: $names","Mobile No. : $number"))
            userAdapter.notifyDataSetChanged()
            Toast.makeText(this,"Adding User Information Success",Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        addDialog.setNegativeButton("Cancel"){
                dialog,_->
            dialog.dismiss()
            Toast.makeText(this,"Cancel",Toast.LENGTH_SHORT).show()

        }
        addDialog.create()
        addDialog.show()

    }


}

