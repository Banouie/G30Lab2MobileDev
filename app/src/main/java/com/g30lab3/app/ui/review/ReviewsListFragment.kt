package com.g30lab3.app.ui.review

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.g30lab3.app.R
import com.g30lab3.app.ReviewVM
import com.g30lab3.app.adapters.ReviewsAdapter


class ReviewsListFragment : Fragment(R.layout.fragment_reviews_list) {

    val reviewsVM by viewModels<ReviewVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val valuedUser = arguments?.get("valuedUser") as String //id of the valued user
        val isOfferer = arguments?.get("valuedUserIsOfferer") as Boolean
        val recyclerView: RecyclerView = view.findViewById(R.id.reviews_rv)
        recyclerView.layoutManager= LinearLayoutManager(requireContext())

        reviewsVM.getUserReviews(valuedUser,isOfferer)
        reviewsVM.retrievedReviews.observe(requireActivity()){
            recyclerView.adapter = ReviewsAdapter(it)
        }

    }

}