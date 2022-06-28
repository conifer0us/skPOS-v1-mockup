package com.subkingofmobile.skpos_android

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.subkingofmobile.skpos_android.databinding.StartMenuBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class StartMenu : Fragment() {

    private var _binding: StartMenuBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = StartMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.NewOrderButton).setOnClickListener {
            displayDevLaziness()
        }

        view.findViewById<Button>(R.id.UnfinishedOrderButton).setOnClickListener {
            displayDevLaziness()
        }

        view.findViewById<Button>(R.id.PreviousOrderButton).setOnClickListener {
            displayDevLaziness()
        }

        view.findViewById<Button>(R.id.SettingsButton).setOnClickListener {
            findNavController().navigate(R.id.action_StartMenu_to_settingsContainerScreen)
        }
    }

    private fun displayDevLaziness() {
        val devLazinessDisplay = Toast.makeText(context, "OOPS! The dev hasn't implemented this feature yet! How Lazy of Him!", Toast.LENGTH_SHORT)
        devLazinessDisplay.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}