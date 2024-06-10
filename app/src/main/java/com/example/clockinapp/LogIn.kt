package com.example.clockinapp
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.clockinapp.databinding.ActivityLogInBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LogIn : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var binding: ActivityLogInBinding
    private lateinit var usersRef: DatabaseReference

    class User {
        var password: String? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = FirebaseDatabase.getInstance()
        usersRef = database.reference.child("users")

        //setContentView(R.layout.activity_log_in)
        //defult
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val signUpTextView = findViewById<TextView>(R.id.signUpTextView)
        signUpTextView.setOnClickListener {
            // Start the SignUp activity
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
        binding.loginBtn.setOnClickListener {
            val username = binding.usernameInput.text.toString()
            val password = binding.passwordInput.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            usersRef.orderByChild("userName").equalTo(username).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (userSnapshot in dataSnapshot.children) {
                            val user = userSnapshot.getValue(User::class.java)
                            if (user != null && user.password == password) {
                                // Login successful
                                Toast.makeText(this@LogIn, "Login successful", Toast.LENGTH_SHORT)
                                    .show()
                                // Redirect to Home Activity or any other activity you want
                                startActivity(Intent(this@LogIn, Report::class.java))
                                finish()
                                return
                            }
                        }
                    }
                    Toast.makeText(this@LogIn, "Invalid username or password", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Error handling
                    Toast.makeText(
                        this@LogIn,
                        "Database error: ${databaseError.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }

    }
}


