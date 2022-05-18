package com.g30lab3.app

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class SkillsVM(application: Application) : AndroidViewModel(application) {

    private var _firebaseSkills = MutableLiveData<List<String>>()
    val firebaseSkills: LiveData<List<String>> = _firebaseSkills
    private val listener: ListenerRegistration

    init {
        listener = FirebaseFirestore.getInstance().collection("Skills")
            .addSnapshotListener { value, error ->
                if (value != null) {
                    _firebaseSkills.value =
                        if (error != null) emptyList() else value.mapNotNull { d -> d.toString() }
                }
            }
    }

    /** Function called in EditProfile, save on Firebase in the proper collection the skills inserted from a user.
     * IF the user declare a skill that already exists, it will NOT be added */
    fun add(newSkills: MutableSet<String>) {
        val x = newSkills.minus(_firebaseSkills.value)//obtain the skills that don't already exist
        for (skill in x) {
            FirebaseFirestore.getInstance().collection("Skills").document(skill.toString())
                .set(Skill(skill.toString())).addOnSuccessListener {
                    Log.d("Skill upload","${skill.toString()} correctly uploaded")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        listener.remove()
    }
}
//useful to store in Firebase the skill, simple Kotlin strings are not serializable!
data class Skill(
    var name: String
)