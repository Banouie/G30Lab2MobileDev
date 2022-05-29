package com.g30lab3.app

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.g30lab3.app.models.chatInfo
import com.g30lab3.app.models.textMessage
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.getField


class chatsVM(application: Application) : AndroidViewModel(application) {

    private val db = FirebaseFirestore.getInstance()
    private val _currentChat = MutableLiveData<List<textMessage>>()
    val currentChat = _currentChat
    private val _allChats = MutableLiveData<List<chatInfo>>()
    val allChats = _allChats
    private lateinit var allChatsListener: ListenerRegistration


    init {
        allChatsListener = db.collection("ChatsInfo").addSnapshotListener { value, error ->
            if (error != null) {
                Log.d("AllChatsError", "error1")
                _allChats.value = listOf()
                return@addSnapshotListener
            }
            if (value?.isEmpty == true || value == null) {
                Log.d("AllChatsEmptyOrNull", "No chat info")
                _allChats.value = listOf()
                return@addSnapshotListener
            }
            _allChats.value = value.mapNotNull { d -> d.toChatInfo() }
        }

    }


    fun createNewChatInfo(
        chatId: String,
        requestUserId: String,
        authorUserId: String,
        timeSlotId: String
    ) {
        val info = chatInfo(chatId, authorUserId, requestUserId, timeSlotId)
        db.collection("ChatsInfo").document(chatId).set(info).addOnSuccessListener {
            Log.d("CHAT", "Created chat Info")
        }
    }


    fun getChat(chatId: String) {
        db.collection("Chats").document(chatId).collection(chatId)
            .orderBy("time", Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.d("ChatError", "Error retrieving chat $chatId")
                    _currentChat.value = listOf() //empty chat
                    return@addSnapshotListener
                }
                if (value == null) {
                    _currentChat.value = listOf()
                    return@addSnapshotListener
                }
                val list = mutableListOf<textMessage>()
                for (message in value) {
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

    fun deleteChat(chatId: String) {
        db.collection("Chats").document(chatId).collection(chatId).get().addOnSuccessListener {
            //delete the chat (it's a collection of messages)
            for (message in it.documents) {
                message.reference.delete()
            }
            //delete the chatInfo
            db.collection("ChatsInfo").document(chatId).delete().addOnSuccessListener {
                Log.d("ChatDeletion", "OK")
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        allChatsListener.remove()
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

fun DocumentSnapshot.toChatInfo(): chatInfo {
    return chatInfo(
        chatId = get("chatId") as String,
        authorOfTimeSlot = get("authorOfTimeSlot") as String,
        requestingUser = get("requestingUser") as String,
        leadingTimeSlot = get("leadingTimeSlot") as String
    )
}


