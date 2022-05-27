package com.g30lab3.app

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.g30lab3.app.models.textMessage
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import java.util.*


class chatsVM(application: Application) : AndroidViewModel(application) {

    private val _allChats = MutableLiveData<List<String>>()
    val allChats = _allChats
    val currentChat = MutableLiveData<List<textMessage>>()
    lateinit var listener: ListenerRegistration

    init {
        listener = FirebaseFirestore.getInstance().collection("Chats")
            .addSnapshotListener { value, error ->
                if (value != null) {
                    _allChats.value =
                        if (error != null || value.isEmpty) mutableListOf() else value.mapNotNull { d ->
                            d.toString()
                        }
                }
            }
    }

    fun getChat(chatId: String) {
        FirebaseFirestore.getInstance().collection("Chats").document(chatId).collection(chatId).orderBy("time", Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.d("ChatError", "Error retrieving chat $chatId")
                    currentChat.value = listOf() //empty chat
                    return@addSnapshotListener
                }
                if(value?.isEmpty == true || value == null){
                    currentChat.value = listOf()
                    return@addSnapshotListener
                }
                var list = mutableListOf<textMessage>()
                for (message in value!!) {
                    list.add(message.toTextMessage())
                }
                currentChat.value=list
            }
    }

    fun addMessage(chatId:String, message:textMessage){
        FirebaseFirestore.getInstance().collection("Chats").document(chatId).collection(chatId).add(message).addOnSuccessListener {
            Log.d("MessageInserted", "Correctly added message to chat")
        }
    }
}

fun DocumentSnapshot.toTextMessage(): textMessage {
    return textMessage(
        text = get("text") as String,
        time = getTimestamp("time")?.toDate()!!,
        senderId = get("senderId") as String
        )
}

