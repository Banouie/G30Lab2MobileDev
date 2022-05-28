package com.g30lab3.app.ui.chat


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.g30lab3.app.MainActivity
import com.g30lab3.app.R
import com.g30lab3.app.UserVM
import com.g30lab3.app.adapters.MessagesAdapter
import com.g30lab3.app.chatsVM
import com.g30lab3.app.models.textMessage
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import java.util.*


class chatFragment : Fragment(R.layout.fragment_chat) {

    val chatVM by viewModels<chatsVM>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView: RecyclerView = view.findViewById(R.id.chat)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val timeSlotId = arguments?.get("timeSlotId") as String
        val authorUserId = arguments?.get("authorUser") as String
        val requestUserId = arguments?.get("requestUser") as String
        val chatId = requestUserId + authorUserId + timeSlotId //obtain the unique Id for the chat

        chatVM.getChat(chatId, requestUserId, authorUserId, timeSlotId)//get the chat from VM
        chatVM.currentChat.observe(requireActivity()) {
            recyclerView.adapter = MessagesAdapter(it)
        }

        var sender: TextInputLayout = view.findViewById(R.id.insert_message)
        sender.helperText = HtmlCompat.fromHtml(
            "<b>Tap</b>: send a message, <b>long press</b>: send a request",
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
            sender.setEndIconOnLongClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Send a request")
                    .setMessage("Are you shure you want to request this Time Slot?")
                    .setNegativeButton("Discard") { dialog, which ->
                        // Respond to negative button press
                        Log.d("BTNPRESSED", "Discard selected")
                    }
                    .setPositiveButton("Yes I'm sure") { dialog, which ->
                        val request = textMessage(timeSlotId,Date(),Firebase.auth.currentUser?.uid!!,true) //if is a request the text of the message will contain the timeSlot id which is requested
                        chatVM.addMessage(chatId,request)
                    }
                    .show()
                true
            }
            sender.setEndIconOnClickListener {
                val messageText = sender.editText?.text.toString()
                val senderId = Firebase.auth.currentUser?.uid!!
                val now = Date()
                val message = textMessage(messageText, now, senderId,false)
                chatVM.addMessage(chatId, message)
                sender.editText?.text?.clear()
            }
        }


    }


        //TODO edit chat fragment label