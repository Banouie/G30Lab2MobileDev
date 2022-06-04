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
    private val _retrievedReviews = MutableLiveData<List<Review>>()
    val retrievedReviews = _retrievedReviews

    /** Function that update the value of retrievedReviews with a list of review of the passed user depending on the parameter iOfferer passed*/
    fun getUserReviews(user: String, isOfferer: Boolean) {
        FirebaseFirestore.getInstance().collection("Reviews")
            .whereEqualTo("valuedUser", user)
            .whereEqualTo("valuedUserIsOfferer", isOfferer)
            .get()
            .addOnSuccessListener {
                val list = mutableListOf<Review>()
                for(x in it){
                    list.add(x.toReview())
                }
                _retrievedReviews.value= list
            }
    }

    fun createNewReview(review: Review) {
        db.collection("Reviews").document().set(review).addOnSuccessListener {
            Log.d("REVIEW", "Created new review")
        }
    }

}

//convert the retrieved data from Firebase to a kotlin User object class
fun DocumentSnapshot.toReview(): Review {
    return Review(
        writerUser = get("writerUser") as String,
        valuedUser = get("valuedUser") as String,
        forRequest = get("forRequest") as String,
        valuedUserIsOfferer = get("valuedUserIsOfferer") as Boolean,
        ratingReview = (get("ratingReview") as Double).toFloat(),
        commentReview = get("commentReview") as String,
        date = getTimestamp("date")?.toDate()!!
    )
}