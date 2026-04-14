package com.example.clipsync

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var emailBox: EditText
    private lateinit var passBox: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        // If user is already logged in, go straight to MainActivity
        if (mAuth.currentUser != null) {
            goToMain()
        }

        emailBox = findViewById(R.id.email)
        passBox = findViewById(R.id.password)
        val btnEnter = findViewById<Button>(R.id.btnEnter)

        btnEnter.setOnClickListener {
            val email = emailBox.text.toString().trim()
            val pass = passBox.text.toString().trim()

            if (email.isEmpty() || pass.length < 6) {
                Toast.makeText(this, "Enter valid email and 6-char password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Attempt Login
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    goToMain()
                } else {
                    // If login fails, try to Register automatically
                    mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { regTask ->
                        if (regTask.isSuccessful) {
                            goToMain()
                        } else {
                            Toast.makeText(this, "Error: ${regTask.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
