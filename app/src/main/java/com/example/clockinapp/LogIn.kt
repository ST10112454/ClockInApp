package com.example.clockinapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.Navigation
import com.example.clockinapp.R.id.username_input

class LogIn : AppCompatActivity() {

    lateinit var usernameInput : EditText
    lateinit var passwordInput : EditText
    lateinit var loginBtn : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_log_in)

        usernameInput = findViewById(username_input)
        passwordInput = findViewById(R.id.password_input)
        loginBtn = findViewById(R.id.login_btn)

        loginBtn.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()

            if (validationInput(username, password)){
                val intent = Intent(this, Home::class.java)
                startActivity(intent)
            }
            else{
                showError();
            }
            /*val intent = Intent(this, Register::class.java)
            startActivity(intent)*/
        }


        //defult
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }
    private fun validationInput(username:String, password:String) : Boolean {
        return username.isNotBlank() && password.isNotBlank()
    }

    private fun showError(){
        Toast.makeText(this, "Please Enter Valid Data", Toast.LENGTH_LONG).show()
    }

}