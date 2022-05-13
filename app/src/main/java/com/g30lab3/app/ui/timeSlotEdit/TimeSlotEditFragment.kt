package com.g30lab3.app.ui.timeSlotEdit

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.g30lab3.app.R
import com.g30lab3.app.models.timeSlot
import com.g30lab3.app.timeSlotVM
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class TimeSlotEditFragment : Fragment(R.layout.fragment_time_slot_edit) {


    lateinit var titleSelector: TextInputLayout
    lateinit var descriptionSelector: TextInputLayout
    lateinit var dateSelector: EditText
    lateinit var timeSelector: EditText
    lateinit var durationSelector: TextInputLayout
    lateinit var locationSelector: TextInputLayout

    lateinit var saveTimeSlotButton: Button

    //variables used to create the Time Slot
    var title: String = ""
    var description: String = ""
    var date: String = ""
    var time: String = ""
    var duration: Int = 0
    var location: String = ""
    var newTimeSlot: timeSlot = timeSlot(0, title, description, date, time, duration, location)

    // variable of viewModel to grant access to the DB, used to add the created time slot to it after back button pressed or save button pressed
    val vm by viewModels<timeSlotVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleSelector = view.findViewById(R.id.edit_title_Field)
        descriptionSelector = view.findViewById(R.id.edit_description_Field)
        dateSelector = view.findViewById(R.id.edit_date)
        timeSelector = view.findViewById(R.id.edit_time)
        durationSelector = view.findViewById(R.id.edit_duration_Field)
        locationSelector = view.findViewById(R.id.edit_location_Field)

        saveTimeSlotButton = view.findViewById(R.id.create_timeSlot)

        //if we come in this fragment after the user pressed the edit button of a timeSlot in the Home
        //we have to edit the selected timeSlot, so we set the edit fields to its values
        if (arguments?.get("time_slot_ID") != null) {
            vm.all.observe(requireActivity()) {
                //get from the list of timeSLot the correct one selected in the home fragment from the user:
                var result: List<timeSlot> =
                    it.filter { timeSlot -> timeSlot.id == arguments?.get("time_slot_ID") }
                var toShowTimeSlot: timeSlot = result[0]
                //copy the timeSlot to edit in the timeSlot that will be added to the DB:
                newTimeSlot.copy(toShowTimeSlot)
                //set the edit fields of the fragment correcty:
                titleSelector.editText?.setText(toShowTimeSlot.title)
                descriptionSelector.editText?.setText(toShowTimeSlot.description)
                dateSelector.setText(toShowTimeSlot.date)
                timeSelector.setText(toShowTimeSlot.time)
                durationSelector.editText?.setText(toShowTimeSlot.duration.toString())
                locationSelector.editText?.setText(toShowTimeSlot.location)
                //change the text of the button from "create" to "update"
                saveTimeSlotButton.setText("Update Time Slot")
            }
        }

        //Set the title of the timeSlot using the one digitized from the user
        titleSelector.editText?.doOnTextChanged { text, start, before, count ->
            newTimeSlot.title = text.toString()
        }
        // ...the same for description...
        descriptionSelector.editText?.doOnTextChanged { text, start, before, count ->
            newTimeSlot.description = text.toString()
        }
        // ...the same for duration...
        durationSelector.editText?.doOnTextChanged { text, start, before, count ->
            try {
                newTimeSlot.duration = text.toString().toInt()
            } catch (ex: NumberFormatException) {
                newTimeSlot.duration = 0
            }
        }
        // ...and finally for location
        locationSelector.editText?.doOnTextChanged { text, start, before, count ->
            newTimeSlot.location = text.toString()
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
            newTimeSlot.time = time
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
            newTimeSlot.date = date
        }

        // Manage the BACK BUTTON pressed event saving the created timeSLot and navigating to the Home
        requireActivity().onBackPressedDispatcher.addCallback {
            vm.add(newTimeSlot)
                .addOnSuccessListener {
                    createSnackBar("Saved",view,requireContext())
                    findNavController().navigate(R.id.action_nav_timeSlotEditFragment_to_nav_home)
                }
                .addOnFailureListener {
                    Log.d("FirebaseError", it.toString())
                    createSnackBar("Something went wrong",view,requireContext())
                }
        }
        // Manage the SAVE BUTTON pressed event doing the same stuff done in the back button pressed case
        saveTimeSlotButton.setOnClickListener {

        }


    }

}

private fun createSnackBar(message:String, view:View, context:Context){
    Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        .setBackgroundTint(ContextCompat.getColor(context, R.color.purple_500))
        .show()
}


