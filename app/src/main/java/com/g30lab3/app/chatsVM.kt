package com.g30lab3.app

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.g30lab3.app.models.PendingRequestInfo
import com.g30lab3.app.models.Status
import com.g30lab3.app.models.TimeSlotStatus
import com.g30lab3.app.models.textMessage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import java.util.*


/** Each time a user shows interest in a specific timeSlot (press the button "Request this timeSlot" in the timeSlot details layout)
 *  a PendingRequestInfo is created in Firebase. Pending requests contains the id of the associated chat*/

class chatsVM(application: Application) : AndroidViewModel(application) {

    private val db = FirebaseFirestore.getInstance()
    private val _currentChat = MutableLiveData<List<textMessage>>()
    val currentChat = _currentChat
    private val _allPendingRequests = MutableLiveData<List<PendingRequestInfo>>()
    val allChats = _allPendingRequests
    private val _loggedUserPendingRequests = MutableLiveData<List<PendingRequestInfo>>()
    val loggedUserPendingRequests = _loggedUserPendingRequests //requests sent from current user still pending
    private val _loggedUserIncomeRequests = MutableLiveData<List<PendingRequestInfo>>()
    val loggedUserIncomeRequests = _loggedUserIncomeRequests //request received from other user to the logged user still pending
    private val _loggedUserAcceptedRequests = MutableLiveData<List<PendingRequestInfo>>()
    val loggedUserAcceptedRequests = _loggedUserAcceptedRequests //requests received from the current user and accepted
    private val _loggedUserAssignedRequests = MutableLiveData<List<PendingRequestInfo>>()
    val loggedUserAssignedRequests = _loggedUserAssignedRequests //requests sent and assigned to the current user

    private lateinit var allPendingRequestsListener: ListenerRegistration
    private lateinit var loggedUserSentPendingRequestsListener: ListenerRegistration
    private lateinit var loggedUserIncomeRequestsListener: ListenerRegistration
    private lateinit var loggedUserAcceptedRequestsListener: ListenerRegistration
    private lateinit var loggedUserAssignedRequestsListener: ListenerRegistration


    init {
        allPendingRequestsListener =
            db.collection("PendingRequests").addSnapshotListener { value, error ->
                if (error != null) {
                    Log.d("AllChatsError", "error1")
                    _allPendingRequests.value = listOf()
                    return@addSnapshotListener
                }
                if (value?.isEmpty == true || value == null) {
                    Log.d("AllChatsEmptyOrNull", "No chat info")
                    _allPendingRequests.value = listOf()
                    return@addSnapshotListener
                }
                _allPendingRequests.value = value.mapNotNull { d -> d.toPendingRequestInfo() }
            }

        loggedUserSentPendingRequestsListener = db.collection("PendingRequests")
            .whereEqualTo("requestingUser", Firebase.auth.currentUser?.uid)
            .whereEqualTo("status", "PENDING")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.d("LogUsrPenRequestError", "error1")
                    _loggedUserPendingRequests.value = listOf()
                    return@addSnapshotListener
                }
                if (value?.isEmpty == true || value == null) {
                    Log.d("RequestsEmptyOrNull", "No requests sent")
                    _loggedUserPendingRequests.value = listOf()
                    return@addSnapshotListener
                }
                _loggedUserPendingRequests.value =
                    value.mapNotNull { d -> d.toPendingRequestInfo() }
            }

        loggedUserIncomeRequestsListener = db.collection("PendingRequests")
            .whereEqualTo("authorOfTimeSlot", Firebase.auth.currentUser?.uid)
            .whereEqualTo("status", "PENDING")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    _loggedUserIncomeRequests.value = listOf()
                    return@addSnapshotListener
                }
                if (value?.isEmpty == true || value == null) {
                    _loggedUserIncomeRequests.value = listOf()
                    return@addSnapshotListener
                }
                _loggedUserIncomeRequests.value = value.mapNotNull { d -> d.toPendingRequestInfo() }
            }

        loggedUserAcceptedRequestsListener = db.collection("PendingRequests")
            .whereEqualTo("authorOfTimeSlot", Firebase.auth.currentUser?.uid)
            .whereEqualTo("status", "ACCEPTED")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    _loggedUserAcceptedRequests.value = listOf()
                    return@addSnapshotListener
                }
                if (value?.isEmpty == true || value == null) {
                    _loggedUserAcceptedRequests.value = listOf()
                    return@addSnapshotListener
                }
                _loggedUserAcceptedRequests.value =
                    value.mapNotNull { d -> d.toPendingRequestInfo() }
            }

       loggedUserAssignedRequestsListener=  db.collection("PendingRequests")
            .whereEqualTo("requestingUser", Firebase.auth.currentUser?.uid).whereEqualTo("status","ACCEPTED")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    _loggedUserAssignedRequests.value = listOf()
                    return@addSnapshotListener
                }
                if (value?.isEmpty == true || value == null) {
                    _loggedUserAssignedRequests.value = listOf()
                    return@addSnapshotListener
                }
                _loggedUserAssignedRequests.value = value.mapNotNull { d -> d.toPendingRequestInfo() }
            }
    }



    /**This function is used when a user want to request a timeslot: creates a new PendingRequest in firebase and initialize the chat between the two users with a request
     *  from the requesting user to the author of the timeslot*/
    fun createNewPendingRequestInfo(
        chatId: String,
        requestUserId: String,
        authorUserId: String,
        timeSlotId: String
    ) {
        val info = PendingRequestInfo(chatId, authorUserId, requestUserId, timeSlotId)
        db.collection("PendingRequests").document(chatId).set(info).addOnSuccessListener {
            Log.d("CHAT", "Created pending request Info")
            var startingRequest = textMessage("", Date(), requestUserId, true)
            addMessage(chatId, startingRequest)
            //update the timeslot status from available to requested
            db.collection("TimeSlotAdvCollection").document(timeSlotId).update("status",TimeSlotStatus.REQUESTED).addOnSuccessListener {
                Log.d("Timeslot status:", " new status: REQUESTED" )
            }
        }
    }

    /**Function that update the status of a pending request*/
    fun updatePendingRequestInfo(RequestToUpdate:PendingRequestInfo, newStatus : Status){
        RequestToUpdate.status = newStatus
        db.collection("PendingRequests").document(RequestToUpdate.chatId).set(RequestToUpdate).addOnSuccessListener {
            Log.d("UPDATEPR","Updated")
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

    /**For the moment this function delete both chat and PendingRequest*/
    fun deleteChat(chatId: String) {
        db.collection("Chats").document(chatId).collection(chatId).get().addOnSuccessListener {
            //delete the chat (it's a collection of messages)
            for (message in it.documents) {
                message.reference.delete()
            }
            //delete the Pending Request
            db.collection("PendingRequests").document(chatId).delete().addOnSuccessListener {
                Log.d("ChatDeletion", "OK")
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        allPendingRequestsListener.remove()
        loggedUserSentPendingRequestsListener.remove()
        loggedUserIncomeRequestsListener.remove()
        loggedUserAcceptedRequestsListener.remove()
        loggedUserAssignedRequestsListener.remove()
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

fun DocumentSnapshot.toPendingRequestInfo(): PendingRequestInfo {
    return PendingRequestInfo(
        chatId = get("chatId") as String,
        authorOfTimeSlot = get("authorOfTimeSlot") as String,
        requestingUser = get("requestingUser") as String,
        leadingTimeSlot = get("leadingTimeSlot") as String,
        status = Status.valueOf(get("status") as String)
    )
}


