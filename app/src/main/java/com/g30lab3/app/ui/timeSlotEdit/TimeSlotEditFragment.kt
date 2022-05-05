package com.g30lab3.app.ui.timeSlotEdit

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.g30lab3.app.R
import com.g30lab3.app.models.timeSlot
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.*


class TimeSlotEditFragment : Fragment(R.layout.fragment_time_slot_edit) {


    lateinit var titleSelector: TextInputLayout
    lateinit var descriptionSelector: TextInputLayout
    lateinit var dateSelector: EditText
    lateinit var timeSelector: EditText
    lateinit var durationSelector: TextInputLayout
    lateinit var locationSelector:TextInputLayout

    //variables used to create the Time Slot
    var title: String = ""
    var description: String = ""
    var date: String = ""
    var time: String = ""
    var duration: Int = 0
    var location: String = ""
    var newTimeSlot: timeSlot = timeSlot(0,title,description, date, time, duration, location)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleSelector = view.findViewById(R.id.edit_title_Field)
        descriptionSelector = view.findViewById(R.id.edit_description_Field)
        dateSelector = view.findViewById(R.id.edit_date)
        timeSelector = view.findViewById(R.id.edit_time)
        durationSelector = view.findViewById(R.id.edit_duration_Field)
        locationSelector= view.findViewById(R.id.edit_location_Field)


        //Set the title of the timeSlot using the one digitized from the user
        titleSelector.editText?.doOnTextChanged { text, start, before, count ->
            newTimeSlot.title=text.toString()
        }
        // ...the same for description...
        descriptionSelector.editText?.doOnTextChanged { text, start, before, count ->
            newTimeSlot.description=text.toString()
        }
        // ...the same for duration...
        durationSelector.editText?.doOnTextChanged { text, start, before, count ->
            newTimeSlot.duration=text.toString().toInt()
        }
        // ...and finally for location
        locationSelector.editText?.doOnTextChanged { text, start, before, count ->
            newTimeSlot.location=text.toString()
        }

        // *** TIME picker relative code ***
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
            newTimeSlot.time=time
        }

        // *** DATE picker relative code ***
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
            newTimeSlot.date=date
        }

        // Manage the back button pressed and save the created timeSLot
        requireActivity().onBackPressedDispatcher.addCallback {
            //Todo: add created timeSlot to DB
            Snackbar.make(view, newTimeSlot.toString(), Snackbar.LENGTH_LONG)
                .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.purple_500))
                .show()
        }


    }

}