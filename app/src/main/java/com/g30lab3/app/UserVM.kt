package com.g30lab3.app

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.g30lab3.app.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase

class UserVM(application: Application) : AndroidViewModel(application) {

    private val db = FirebaseFirestore.getInstance()
    private var currentUser = Firebase.auth.currentUser //get the current logged user from Firebase auth

    private var _loggedUser = MutableLiveData<User>()
    var loggedUser = _loggedUser //for getting info of the logged user
    private var _retrievedUser = MutableLiveData<User>()
    var retrievedUser = _retrievedUser // for timeslot author details
    private val listener: ListenerRegistration
    private val _starredTimeSlots = MutableLiveData<List<String>>()
    val starredTimeSlots = _starredTimeSlots

    init {
        listener = db.collection("Users").document(currentUser?.uid!!)
            .addSnapshotListener { value, error ->
                if (value != null) {
                    _loggedUser.value = if (error != null) null else value.toUser()
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        listener.remove()
    }

    /** Save the user profile into Firestore DB, returns the Task in order to specify callbacks where is called **/
    fun upload(user: User): Task<Void> {
        return db.collection("Users").document(user.id).set(user)
    }

    /**Function that returns info of a user identified by its user unique ID*/
    fun getUserInfo(uid: String): MutableLiveData<User> {
        db.collection("Users").document(uid)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w("UserErr", "Listener failed", error)
                    _retrievedUser.value = User("", "", "", "", mutableListOf(), "", "")
                    return@addSnapshotListener
                }
                Log.d("USER_Author", value.toString())
                _retrievedUser.value = value?.toUser()
            }
        return retrievedUser
    }

    /**Function that update the variable "starredTimeSlots" retrieving a list of timeSlot ID which are the ones "starred" from the current logged user, if the user has no starred timeSlots the list is empty*/
    fun getStarredTimeSlotsOfLoggedUser(){
        db.collection("Users").document(currentUser?.uid!!).collection("starred").addSnapshotListener { value, error ->
            if(error!=null){
                Log.d("StarredTimeSlotsError", "Error retrieving starred timeSlots of current user")
                _starredTimeSlots.value= mutableListOf()
                return@addSnapshotListener
            }
            if(value?.isEmpty == true || value==null){
                Log.d("StarredTimeSlotsEmpty", "retrieved value is: ${value.toString()}")
                _starredTimeSlots.value= mutableListOf()
                return@addSnapshotListener
            }
            val list = mutableListOf<String>()
            for (starred in value){
                list.add(starred.toString())
            }
            _starredTimeSlots.value=list
        }
    }
    /**Function that add a star to a timeSlot, save the timeSlotId in the "starred" collection of the current user*/
    fun addStarredTimeSlot(timeSlotId : String){
        db.collection("Users").document(currentUser?.uid!!).collection("starred").add(timeSlotId)
            .addOnSuccessListener {
            Log.d("NewStar","Added correctly the timeslot $timeSlotId to starred ones of current logged user")
        }.addOnFailureListener {
            Log.d("NewStar","Error starring the timeslot, exception: ${it.message} ")
        }
    }

    /**Function that remove a star from a timeSlot, delete the timeSlotId from the "starred" collection of the current user*/
    fun removeStarredTimeSlot(timeSlotId : String){
        db.collection("Users").document(currentUser?.uid!!).collection("starred").document(timeSlotId).delete()
            .addOnSuccessListener {
                Log.d("RemoveStar","Removed correctly the timeslot $timeSlotId from the starred ones of current logged user")
            }.addOnFailureListener {
                Log.d("RemoveStar","Error removing timeslot from starred ones, exception: ${it.message} ")
            }
    }



}

//convert the retrieved data from Firebase to a kotlin User object class
fun DocumentSnapshot.toUser(): User {
    return User(
        id = get("id") as String,
        full_name = get("full_name") as String,
        nickname = get("nickname") as String,
        description = get("description") as String,
        skills = get("skills") as MutableList<String>,
        location = get("location") as String,
        mail = get("mail") as String
    )
}
