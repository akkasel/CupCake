/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.cupcake

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.cupcake.databinding.FragmentSummaryBinding
import com.example.cupcake.model.OrderViewModel

/**
 * [SummaryFragment] contains a summary of the order details with a button to share the order
 * via another app.
 */
class SummaryFragment : Fragment() {

    // Binding object instance corresponding to the fragment_summary.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment.
    private var binding: FragmentSummaryBinding? = null

    // Get a reference to the shared ViewModel as a class variable.
    private val sharedViewModel: OrderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentSummaryBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            // set the shared ViewModel for this fragment.
            viewModel = sharedViewModel
            // set the lifecycle owner on the binding object.
            lifecycleOwner = viewLifecycleOwner
            // bind the fragment data variable with the fragment instance
            summaryFragment = this@SummaryFragment
        }
    }

    /**
     * Submit the order by sharing out the order details to another app via an implicit intent.
     */
    fun sendOrder() {
        // to figure out the cupcake quantity from the ViewModel and store that in variable.
        val numberOfCupCakes = sharedViewModel.quantity.value ?: 0

        // for sending an email with structured text like the value of the string 'order_details'
        val orderSummary = getString(
            // Create the formatted order_details string
            // by getting the order quantity, flavor, date, and price from the shared view model.
            R.string.order_details,
            resources.getQuantityString(R.plurals.cupcakes, numberOfCupCakes, numberOfCupCakes),
            sharedViewModel.flavor.value.toString(),
            sharedViewModel.date.value.toString(),
            sharedViewModel.price.value.toString()
        )

        // an Implicit Intent to create an email intent.
        val intent = Intent(Intent.ACTION_SEND)
            .setType("text/plain")
            // set the SUBJECT for the Email.
            .putExtra(Intent.EXTRA_SUBJECT, getString(R.string.new_cupcake_order))
            // set the TEXT for the Email.
            .putExtra(Intent.EXTRA_TEXT, orderSummary)

        // before launching an Activity with this intent, check to see if there's an app that
        // could even handle it. (to prevent crashing if there's no app to handle th Intent).
        if (activity?.packageManager?.resolveActivity(intent, 0) != null) {
            startActivity(intent)
        }
    }

    // to handle when User wants to cancel the order, and navigate from current fragment (flavorFragment) to the startFragment
    fun cancelOrder() {
        sharedViewModel.resetOrder()
        findNavController().navigate(R.id.action_summaryFragment_to_startFragment)
    }

    /**
     * This fragment lifecycle method is called when the view hierarchy associated with the fragment
     * is being removed. As a result, clear out the binding object.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}