package com.g30lab3.app.ui.chat


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*


class chatFragment : Fragment(R.layout.fragment_chat) {

    val chatVM by viewModels<chatsVM>()



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView :RecyclerView = view.findViewById(R.id.chat)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val chatId = arguments?.get("chatId") as String
        val timeSlotId =  arguments?.get("timeSlotId") as String
        chatVM.getChat(chatId)//get the chat from VM
        chatVM.currentChat.observe(requireActivity()){
            recyclerView.adapter = MessagesAdapter(it)
        }


        var sender:TextInputLayout = view.findViewById(R.id.insert_message)
        sender.setEndIconOnClickListener{
            val messageText = sender.editText?.text.toString()
            val senderId = Firebase.auth.currentUser?.uid!!
            val now = Date()
            val message = textMessage(messageText,now,senderId)
            chatVM.addMessage(chatId,message)
            sender.editText?.text?.clear()
        }

    }

    //set as title of the fragment the selected skill in the layout before (Skills List)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val usersVM by viewModels<UserVM>()
        lateinit var otherUserId : String
        for (id in arguments?.getString("chatId")?.split("-")!!) {
            if (id != Firebase.auth.currentUser?.uid!!){
                otherUserId= id
            }
        }
        usersVM.getUserInfo(otherUserId).observe(requireActivity()){
            (activity as MainActivity).supportActionBar?.title= "${it.full_name}"
        }


        return super.onCreateView(inflater, container, savedInstanceState)
    }



}