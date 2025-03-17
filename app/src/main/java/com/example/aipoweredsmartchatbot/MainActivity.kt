package com.example.aipoweredsmartchatbot

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.content.Intent

class MainActivity : AppCompatActivity() {
    private lateinit var chatAdapter: ChatAdapter
    private val chatList = mutableListOf<ChatMessage>()
    private lateinit var drawerLayout: DrawerLayout // Added DrawerLayout

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialize DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout)

        // Menu Icon Click Listener (Opens Navigation Drawer)
        val menuIcon = findViewById<ImageView>(R.id.menuIcon)
        menuIcon.setOnClickListener {
            drawerLayout.openDrawer(findViewById(R.id.drawer_layout))
        }

        try {
            val userInput = findViewById<EditText>(R.id.userInput)
            val sendButton = findViewById<Button>(R.id.sendButton)
            val chatRecyclerView = findViewById<RecyclerView>(R.id.chatRecyclerView)

            // Initialize RecyclerView
            chatAdapter = ChatAdapter(chatList)
            chatRecyclerView.layoutManager = LinearLayoutManager(this)
            chatRecyclerView.adapter = chatAdapter

            // Fetch API Key securely from secrets.xml
            val apiKey = getString(R.string.gemini_api_key)
            val generativeModel = GenerativeModel(
                modelName = "gemini-2.0-flash",
                apiKey = apiKey
            )

            sendButton.setOnClickListener {
                val prompt = userInput.text.toString().trim()
                if (prompt.isNotEmpty()) {
                    addMessage(ChatMessage(prompt, true))  // Add user message
                    userInput.text.clear()

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val response = generativeModel.generateContent(prompt)
                            val botReply = response.text ?: "Sorry, I didn't understand that."

                            runOnUiThread {
                                addMessage(ChatMessage(botReply, false))
                            }
                        } catch (e: Exception) {
                            Log.e("API_ERROR", "Error: ${e.message}")
                            runOnUiThread {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Failed to fetch response.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("INIT_ERROR", "Initialization failed: ${e.message}")
            Toast.makeText(this, "App initialization failed.", Toast.LENGTH_LONG).show()
        }
    }

    private fun addMessage(message: ChatMessage) {
        chatList.add(message)
        runOnUiThread {
            chatAdapter.notifyItemInserted(chatList.size - 1)
        }
    }
}
