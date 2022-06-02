package com.g30lab3.app

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.g30lab3.app.models.PendingRequestInfo
import com.g30lab3.app.models.Review
import com.g30lab3.app.models.User
import com.g30lab3.app.models.textMessage
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import java.util.*

class ReviewVM(application: Application) : AndroidViewModel(application) {

    private val db = FirebaseFirestore.getInstance()
    private var currentUser = Firebase.auth.currentUser //get the current logged user from Firebase auth

    private var _receiverReview = MutableLiveData<User>()
    var receiverUserId = _receiverReview //for getting info of the logged user
    private var _senderReview = MutableLiveData<User>()
    var senderUserId = _senderReview // for timeslot author details
    private val listener: ListenerRegistration


    init {
        listener = db.collection("Reviews").document()
            .addSnapshotListener { value, error ->
                if (value != null) {
                    // I will fix this and other parts before Thrusday
                }
            }
    }


    fun createNewReview(review : Review){
        db.collection("Reviews").document().set(review).addOnSuccessListener {
            Log.d("REVIEW", "Created new review")
        }
    }




    override fun onCleared() {
        super.onCleared()
        listener.remove()
    }

    /** Save the user profile into Firestore DB, returns the Task in order to specify callbacks where is called **/
    /*
    fun upload(review: Review): Task<Void> {
        return db.collection("Reviews").document(review.id).set(review)
    }

     */


    //convert the retrieved data from Firebase to a kotlin User object class
    fun DocumentSnapshot.toReview(): Review {
        return Review(
            writerUser = get("writerUser") as String,
            valuedUser= get("valuedUser") as String,
            forRequest = get("forRequest") as String,
            valuedUserIsOfferer = get("valuedUserIsOfferer") as Boolean,
            ratingReview = get("ratingReview") as Float,
            commentReview = get("commentReview") as String
        )
    }

}