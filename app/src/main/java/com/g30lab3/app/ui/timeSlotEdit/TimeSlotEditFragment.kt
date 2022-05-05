package com.g30lab3.app.ui.timeSlotEdit

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.g30lab3.app.R
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.days


class TimeSlotEditFragment : Fragment(R.layout.fragment_time_slot_edit) {


    lateinit var dateSelector: EditText
    lateinit var timeSelector: EditText

    var time: String=""
    var date: String=""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dateSelector = view.findViewById(R.id.edit_date)
        timeSelector = view.findViewById(R.id.edit_time)

        // *** time picker relative code ***
        val timePicker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(10)
                .build()
        // show it
        timeSelector.setOnClickListener {
            timePicker.show(parentFragmentManager, "tag");
        }
        // get positive time selection
        timePicker.addOnPositiveButtonClickListener {
            time = timePicker.hour.toString() + " : " + timePicker.minute.toString()
            timeSelector.setText(time)
        }

        // *** date picker relative code ***
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .build()
        // show it
        dateSelector.setOnClickListener {
            datePicker.show(parentFragmentManager, "tag");
        }
        // get positive date selection
        datePicker.addOnPositiveButtonClickListener {
            val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            utc.timeInMillis = it
            val format = SimpleDateFormat("dd-MM-yyyy")
            date = format.format(utc.time)
            dateSelector.setText(date)
        }




        //TODO manage back button pression for saving edited timeslot


    }
}