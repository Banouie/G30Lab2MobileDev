package com.g30lab3.app

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.g30lab3.app.models.chatInfo
import com.g30lab3.app.models.textMessage
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class chatsVM(application: Application) : AndroidViewModel(application) {

    private val db = FirebaseFirestore.getInstance()
    private val _currentChat = MutableLiveData<List<textMessage>>()
    val currentChat = _currentChat

    fun getChat(chatId: String, requestUserId: String, authorUserId: String, timeSlotId: String) {
        db.collection("Chats").document(chatId).collection(chatId)
            .orderBy("time", Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.d("ChatError", "Error retrieving chat $chatId")
                    _currentChat.value = listOf() //empty chat
                    return@addSnapshotListener
                }
                if (value?.isEmpty == true || value == null) {
                    //The chat is new: set information about the chat in the document
                    val info = chatInfo(authorUserId,requestUserId,timeSlotId)
                    db.collection("Chats").document(chatId).set(info)
                    _currentChat.value = listOf()
                    return@addSnapshotListener
                }
                val list = mutableListOf<textMessage>()
                for (message in value) {
                    Log.d("AAA",message.toString())
                    list.add(message.toTextMessage())
                }
                _currentChat.value = list
            }
    }

    fun addMessage(chatId: String, message: textMessage) {
        db.collection("Chats").document(chatId).collection(chatId).add(message)
            .addOnSuccessListener {
                Log.d("MessageInserted", "Correctly added message to chat")
            }
    }


}

fun DocumentSnapshot.toTextMessage(): textMessage {
    return textMessage(
        text = get("text") as String,
        time = getTimestamp("time")?.toDate()!!,
        senderId = get("senderId") as String,
        request = get("request") as Boolean
    )
}

fun DocumentSnapshot.toChatInfo() : chatInfo{
    return chatInfo(
        authorOfTimeSlot = get("authorOfTimeSlot") as String,
        requestingUser = get("requestingUser") as String,
        leadingTimeSlot = get("leadingTimeSlot") as String
    )
}

