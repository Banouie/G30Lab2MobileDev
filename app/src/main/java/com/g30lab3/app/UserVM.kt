package com.g30lab3.app

import android.app.Application
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
    var loggedUser = _loggedUser
    private val listener: ListenerRegistration

    init {
        listener = FirebaseFirestore.getInstance().collection("Users").document(currentUser?.uid!!)
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
