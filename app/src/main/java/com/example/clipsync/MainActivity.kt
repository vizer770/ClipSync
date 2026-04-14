package com.example.clipsync

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var clipManager: ClipboardManager
    
    private lateinit var tvLaptopWindow: TextView
    private lateinit var etSendText: EditText
    private lateinit var btnSend: MaterialButton
    private lateinit var btnToggle: MaterialButton

    private var isUpdatingFromCloud = false
    private var isSyncEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        clipManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        tvLaptopWindow = findViewById(R.id.tvLaptopWindow)
        etSendText = findViewById(R.id.etSendText)
        btnSend = findViewById(R.id.btnSend)
        btnToggle = findViewById(R.id.btnToggle)

        val uid = auth.currentUser?.uid ?: return

        // 1. Toggle Sync Logic
        btnToggle.setOnClickListener {
            isSyncEnabled = !isSyncEnabled
            if (isSyncEnabled) {
                btnToggle.text = "Sync: ENABLED"
                btnToggle.setBackgroundColor(android.graphics.Color.parseColor("#4CAF50"))
            } else {
                btnToggle.text = "Sync: DISABLED"
                btnToggle.setBackgroundColor(android.graphics.Color.parseColor("#F44336"))
            }
        }

        // 2. Manual Send to Laptop
        btnSend.setOnClickListener {
            val textToSend = etSendText.text.toString().trim()
            if (textToSend.isNotEmpty()) {
                syncToCloud(textToSend, uid)
                etSendText.setText("")
                Toast.makeText(this, "Sent to Laptop!", Toast.LENGTH_SHORT).show()
            }
        }

        // 3. Watch Phone Clipboard -> Cloud (Automatic Sync)
        clipManager.addPrimaryClipChangedListener {
            if (!isSyncEnabled || isUpdatingFromCloud) return@addPrimaryClipChangedListener

            val clipData = clipManager.primaryClip
            if (clipData != null && clipData.itemCount > 0) {
                val text = clipData.getItemAt(0).text?.toString() ?: ""
                if (text.isNotEmpty()) {
                    syncToCloud(text, uid)
                }
            }
        }

        // 4. Watch Cloud -> Update Phone Clipboard & UI
        db.collection("users").document(uid)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    val cloudText = snapshot.getString("clipboard") ?: ""
                    tvLaptopWindow.text = cloudText
                    
                    if (isSyncEnabled) {
                        updateLocalClipboard(cloudText)
                    }
                }
            }
    }

    private fun syncToCloud(text: String, uid: String) {
        val data = hashMapOf(
            "clipboard" to text,
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("users").document(uid).set(data)
    }

    private fun updateLocalClipboard(text: String) {
        val currentClip = clipManager.primaryClip?.getItemAt(0)?.text?.toString()
        if (text == currentClip) return

        isUpdatingFromCloud = true
        val newClip = ClipData.newPlainText("ClipSync", text)
        clipManager.setPrimaryClip(newClip)

        // Reset flag after delay to avoid loop
        Handler(Looper.getMainLooper()).postDelayed({
            isUpdatingFromCloud = false
        }, 1000)
    }
}
