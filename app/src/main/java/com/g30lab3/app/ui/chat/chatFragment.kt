package com.g30lab3.app.ui.chat


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.g30lab3.app.MainActivity
import com.g30lab3.app.R
import com.g30lab3.app.adapters.MessagesAdapter
import com.g30lab3.app.chatsVM
import com.g30lab3.app.models.PendingRequestInfo
import com.g30lab3.app.models.textMessage
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.*


class chatFragment : Fragment(R.layout.fragment_chat) {

    val chatVM by viewModels<chatsVM>()

    //set as title of the chat fragment the interlocutor
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val authorUserId = arguments?.get("authorUser") as String
        val requestUserId = arguments?.get("requestUser") as String
        val id = if(requestUserId == Firebase.auth.currentUser?.uid) authorUserId else requestUserId
        FirebaseFirestore.getInstance().collection("Users").document(id).get().addOnSuccessListener {
            (activity as MainActivity).supportActionBar?.title= it.get("full_name") as String
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView: RecyclerView = view.findViewById(R.id.chat)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val timeSlotId = arguments?.get("timeSlotId") as String
        val authorUserId = arguments?.get("authorUser") as String
        val requestUserId = arguments?.get("requestUser") as String
        val chatId = requestUserId + authorUserId + timeSlotId //obtain the unique Id for the chat
        val info = PendingRequestInfo(chatId,authorUserId,requestUserId,timeSlotId)

        //TODO create the pending request and the chat only if the loggedUser has credits

        //check if the chat with passed chatId already exists or must be initialized in ChatInfo
        chatVM.allChats.observe(requireActivity()){
            if(!it.any { item -> item.chatId == chatId }){
                chatVM.createNewPendingRequestInfo(chatId,requestUserId,authorUserId,timeSlotId)
            }
        }
        chatVM.getChat(chatId)//get the list of messages for the chat
        chatVM.currentChat.observe(requireActivity()) {
            if (it.isEmpty() && requestUserId == Firebase.auth.currentUser?.uid && context != null) {
                //this is a complete new chat created now from the requesting user, show him information:
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Time Slot saved in you pending requests!")
                    .setMessage("What do you want to do?")
                    .setNegativeButton("Chat now") { dialog, which ->
                        // dismiss dialog, remains in chat
                    }
                    .setPositiveButton("See pending requests") { dialog, which ->
                        //go to pending requests
                        findNavController().navigate(R.id.action_chatFragment_to_showRequestsFragment)
                    }
                    .show()
            }
            recyclerView.adapter = MessagesAdapter(it,info)
        }

        var sender: TextInputLayout = view.findViewById(R.id.insert_message)

        if(Firebase.auth.currentUser?.uid!= requestUserId){
            //the user that is looking at the chat is the author of the timeslot requested with this chat, he is able to accept or decline the request

            //this appear only for the author of timeslot (1 of 2)
            sender.helperText = HtmlCompat.fromHtml(
                "<b>Tap</b>: send a message, <b>long press</b>: answer request",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            sender.setEndIconOnLongClickListener {

                MaterialAlertDialogBuilder(requireContext())//this appear only for the author of timeslot (2 of 2)
                    .setTitle("Answer to the request")
                    .setMessage("What do you want to do with this request?")
                    .setNegativeButton("Reject") { dialog, which ->
                        // Respond to negative button press
                    }
                    .setPositiveButton("Accept") { dialog, which ->
                        //accept request
                    }
                    .show()
                true
            }
        }

        //single tap send a new message
        sender.setEndIconOnClickListener {
            val messageText = sender.editText?.text.toString()
            val senderId = Firebase.auth.currentUser?.uid!!
            val now = Date()
            val message = textMessage(messageText, now, senderId, false)
            chatVM.addMessage(chatId, message)
            sender.editText?.text?.clear()
        }
    }



}


