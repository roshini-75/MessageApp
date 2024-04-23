package com.example.firebasenotifications


import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import com.example.firebasenotifications.databinding.ActivityMainBinding

const val TOPIC = "/topics/myTopic2"

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            binding.etToken.setText(token)
        }

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        binding.btnSend.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val message = binding.etMessage.text.toString()
            val recipientToken = binding.etToken.text.toString()
            if(title.isNotEmpty() && message.isNotEmpty() && recipientToken.isNotEmpty()) {
                PushNotification(
                    NotificationData(title, message),
                    recipientToken
                ).also {
                    sendNotification(it)
                }
            }
        }
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d(TAG, "Response: ${Gson().toJson(response)}")
            } else {
                Log.e(TAG, response.errorBody().toString())
            }
        } catch(e: Exception) {
            Log.e(TAG, e.toString())
        }
    }
}