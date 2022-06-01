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
import com.g30lab3.app.adapters.IncomePendingRequestAdapter
import com.g30lab3.app.chatsVM


class AssignedPendingRequestsFragment : Fragment() {

    val pendingVM by viewModels<chatsVM>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_assigned_pending_requests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        val rv = view.findViewById<RecyclerView>(R.id.pending_requests_rv_4)
        rv.layoutManager = LinearLayoutManager(requireContext())

        pendingVM.loggedUserAssignedRequests.observe(requireActivity()) {
            if (context != null) {
                rv.adapter = IncomePendingRequestAdapter(it, requireActivity())
            }
        }

    }

}