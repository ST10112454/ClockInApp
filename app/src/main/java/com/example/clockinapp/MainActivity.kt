package com.example.clockinapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.BottomNavigationItemView


class MainActivity : AppCompatActivity() {
    lateinit var ButtonLogIn : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        //I dont know what this is: defult
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //defult end

        //button for home page....need to change to log in page
        ButtonLogIn = findViewById(R.id.btnLogIn)
        ButtonLogIn.setOnClickListener{
            val intent1 = Intent(this, LogIn::class.java  )
            startActivity(intent1)
        }
        // TextView to
        // Find the TextView by its ID
        val signUpTextView = findViewById<TextView>(R.id.signUpTextView)

        // Set an OnClickListener on the TextView
        signUpTextView.setOnClickListener {
            // Start the SignUp activity
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
    }
}