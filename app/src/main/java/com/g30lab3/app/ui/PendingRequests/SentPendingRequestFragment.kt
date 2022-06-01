package com.g30lab3.app.ui.PendingRequests

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.g30lab3.app.MainActivity
import com.g30lab3.app.R
import com.g30lab3.app.TimeSlotVM
import com.g30lab3.app.adapters.SentPendingRequestAdapter
import com.g30lab3.app.chatsVM
import com.google.android.material.tabs.TabLayout


class SentPendingRequestFragment : Fragment() {
    val pendingVM by viewModels<chatsVM>()
    val tsVM by viewModels<TimeSlotVM>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_sent_pending_request, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val rv = view.findViewById<RecyclerView>(R.id.pending_requests_rv)
        rv.layoutManager = LinearLayoutManager(requireContext())

        pendingVM.loggedUserPendingRequests.observe(requireActivity()) {
            tsVM.getRequestedTimeSlots(it) //set the requested variable of this VM to a list of requested timeSlot
            if (context != null) {
                tsVM.requested.observe(requireActivity()) { t ->
                    if (context != null) {
                        rv.adapter = SentPendingRequestAdapter(t, it, requireActivity())
                    }
                }
            }


        }

    }
}