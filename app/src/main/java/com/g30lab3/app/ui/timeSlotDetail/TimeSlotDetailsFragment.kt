package com.g30lab3.app.ui.timeSlotDetail

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.g30lab3.app.R
import com.g30lab3.app.models.timeSlot
import com.g30lab3.app.TimeSlotVM
import com.g30lab3.app.UserVM
import com.g30lab3.app.chatsVM
import com.g30lab3.app.models.TimeSlotStatus
import com.g30lab3.app.models.textMessage
import com.g30lab3.app.ui.timeSlotEdit.createSnackBar
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.*


class TimeSlotDetailsFragment : Fragment(R.layout.fragment_time_slot_details) {

    val vm by viewModels<TimeSlotVM>()
    val UserVM by viewModels<UserVM>()
    val chatVM by viewModels<chatsVM>()

    lateinit var to_show_timeSlot: timeSlot


    //crate the menu in this fragment for edit timeSlot option
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.time_slot_edit_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_editTimeSlot -> {
                if (to_show_timeSlot.author == Firebase.auth.currentUser?.uid) {
                    // navigate to edit timeSlot if the current logged in user is the author of the timeSlot
                    var id = arguments?.get("time_slot_ID") //get the ID of the current timeSlot
                    var bundle = bundleOf("time_slot_ID" to id)//create the bundle with the ID
                    findNavController().navigate(
                        R.id.action_timeSlotDetailsFragment_to_timeSlotEditFragment,
                        bundle
                    )//pass it to the edit Time Slot fragment and navigate
                    true
                } else {
                    createSnackBar("You are not the owner", requireView(), requireContext(), false)
                    true
                }


            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title: TextView = view.findViewById(R.id.adv_title)
        val description: TextView = view.findViewById(R.id.adv_description)
        val skill: TextView = view.findViewById(R.id.adv_skill)
        val dateTime: TextView = view.findViewById(R.id.adv_date_time)
        val location: TextView = view.findViewById(R.id.adv_location)
        val duration: TextView = view.findViewById(R.id.adv_duration)
        val author: TextView = view.findViewById(R.id.adv_author)
        val goToProfileBtn: Button = view.findViewById(R.id.go_to_profile_btn)
        val startChatBtn: MaterialButton = view.findViewById(R.id.request_timeSlot_btn)
        val deleteRequestBtn: MaterialButton = view.findViewById(R.id.delete_request_timeSlot_btn)




        vm.all.observe(requireActivity()) {

            //get from the list of timeSLot the correct one selected in the home fragment from the user, its ID is passed to this fragment in the arguments variable
            var result: List<timeSlot> =
                it.filter { timeSlot -> timeSlot.id == arguments?.get("time_slot_ID") }
            to_show_timeSlot = result[0]

            //Show the details of the selected timeSlot in the various textViews
            title.text = to_show_timeSlot.title
            //HtmlCompat.fromHtml just format the string in order to obtain the names of the fields of the time slot in bold
            description.text = HtmlCompat.fromHtml(
                "<b>Description</b>:<br> " + to_show_timeSlot.description,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            skill.text = HtmlCompat.fromHtml(
                "<b>Skill</b>:<br> " + to_show_timeSlot.skill,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            location.text = HtmlCompat.fromHtml(
                "<b>Location</b>:<br> " + to_show_timeSlot.location,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            duration.text = HtmlCompat.fromHtml(
                "<b>Duration (in number of time slots)</b>: " + to_show_timeSlot.duration.toString(),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            dateTime.text = HtmlCompat.fromHtml(
                "<b>Date</b>: " + to_show_timeSlot.date + "<br><b>Time</b>: " + to_show_timeSlot.time,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            //obtain the author info of the author of the timeslot
            UserVM.getUserInfo(to_show_timeSlot.author).observe(requireActivity()) { x ->
                author.text = HtmlCompat.fromHtml(
                    "<b>Author</b>:<br> " + x.full_name,
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                // manage the "go to profile" button, send a bundle containing the uid of the author in order to retrieve its info in the showProfile screen
                goToProfileBtn.setOnClickListener {
                    findNavController().navigate(
                        R.id.action_timeSlotDetailsFragment_to_showAuthorProfileFragment,
                        bundleOf("uid" to to_show_timeSlot.author)
                    )
                }

                //values for chats
                val requestingUserId = Firebase.auth.currentUser?.uid
                val authorId = to_show_timeSlot.author


                // manage the possibility that the request from the logged user already exist for this timeslot
                chatVM.allChats.observe(requireActivity()) { list ->
                    val requestFromCurrentUser =
                        list.find { chatInfo -> chatInfo.leadingTimeSlot == to_show_timeSlot.id && chatInfo.requestingUser == Firebase.auth.currentUser?.uid }
                    if (requestFromCurrentUser != null) {
                        //exist already a chatInfo created from the current logged user, so he can also delete the chatInfo from this layout
                        startChatBtn.text = "Open chat"
                        startChatBtn.setIconResource(R.drawable.ic_chat)
                        deleteRequestBtn.visibility = View.VISIBLE
                    } else if (to_show_timeSlot.status == TimeSlotStatus.UNAVAILABLE) {
                        //the logged user has no request for this timeslot and the status of the timeslot is Unavailable -> don't able request button
                        startChatBtn.setIconResource(R.drawable.ic_cancel)
                        startChatBtn.text = "Unavailable TimeSlot"
                        startChatBtn.isEnabled = false
                        startChatBtn.isCheckable = false
                    }
                    if (authorId == requestingUserId){
                        //the logged user is watching their timeslot, remove start request/chat btn:
                        startChatBtn.setOnClickListener {
                            createSnackBar("This is your Time Slot!",requireView(),requireContext(),false)
                            startChatBtn.isEnabled = false
                            startChatBtn.isCheckable = false
                        }
                    }
                }

                //manage the "request" button
                startChatBtn.setOnClickListener {
                    //go to chat or create the chat/pending request for the users
                    findNavController().navigate(
                        R.id.action_timeSlotDetailsFragment_to_chatFragment,
                        bundleOf(
                            "requestUser" to requestingUserId,
                            "timeSlotId" to to_show_timeSlot.id,
                            "authorUser" to authorId
                        )
                    )
                }

                //manage the "delete request" button
                deleteRequestBtn.setOnClickListener {
                    //delete list of messages and relative PendingRequest, than hide this button, also change the start request button text and icon
                    val chatId = requestingUserId + authorId + to_show_timeSlot.id
                    chatVM.deleteChat(chatId)
                    deleteRequestBtn.visibility = View.GONE
                    startChatBtn.text = "Request this Time Slot"
                    startChatBtn.setIconResource(R.drawable.ic_bookmark_add)
                    //if the user is the one that has this timeslot (the author accepted his request) we have also to make the timeSLot available again
                    FirebaseFirestore.getInstance().collection("TimeSlotAdvCollection")
                        .document(to_show_timeSlot.id)
                        .update("status", TimeSlotStatus.AVAILABLE).addOnSuccessListener {
                            Log.d("UP_TS", "Updated")
                        }
                    createSnackBar("Deleted", requireView(), requireContext(), true)
                }


            }
        }


        //manage the back button pressure to go back in the list of timeSlots, we need to pass in a bundle
        // the actual skill to retrieve timeSlots there

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                    when (findNavController().previousBackStackEntry?.destination?.id) {
                        R.id.timeSlotEditFragment -> {
                            //if we come from the timeSlot edit screen, go back to the home
                            findNavController().navigate(
                                R.id.action_timeSlotDetailsFragment_to_timeSlotListFragment,
                                bundleOf("skill" to to_show_timeSlot.skill)
                            )
                        }
                        R.id.timeSlotListFragment -> {
                            //if we come from the home, go back to the home
                            findNavController().navigate(
                                R.id.action_timeSlotDetailsFragment_to_timeSlotListFragment,
                                bundleOf("skill" to to_show_timeSlot.skill)
                            )
                        }
                        R.id.showRequestsFragment -> {
                            //we came from the pending requests fragment, come back there
                            findNavController().navigate(R.id.action_timeSlotDetailsFragment_to_showRequestsFragment)
                        }
                    }

                }
            })


    }

}