package com.g30lab3.app.ui.timeSlotDetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.g30lab3.app.models.TimeslotModel

class TimeSlotViewModel : ViewModel() {

    var lst = MutableLiveData<ArrayList<TimeslotModel>>()
    var newlist = arrayListOf<TimeslotModel>()

    fun add(timeslot: TimeslotModel) {
        newlist.add(timeslot)
        lst.value = newlist
    }

    fun remove(timeslot: TimeslotModel) {
        newlist.remove(timeslot)
        lst.value = newlist
    }
}