package com.subkingofmobile.skpos_android

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.subkingofmobile.skpos_android.databinding.FragmentFirstBinding
import org.json.JSONObject

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    @SuppressLint("CutPasteId", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nextButton.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        view.findViewById<Button>(R.id.toast_button).setOnClickListener {
            val network = ConnectionHandler(requireContext(), requireActivity())
            network.isDeviceConnected(
                {resp -> Toast.makeText(context, "Network Connected!", Toast.LENGTH_SHORT).show()
                    view.findViewById<TextView>(R.id.textview_first).text = "Value: ${resp!!.getInt("statusCode")}"
                },
                {errormsg -> val txtview = view.findViewById<TextView>(R.id.textview_first); txtview.textSize = 41f; txtview.text = errormsg }
            )
        }

        view.findViewById<Button>(R.id.count_button).setOnClickListener {
            countMe(view)
        }

        binding.randomButton.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun countMe(view : View) {
        val textView = view.findViewById<TextView>(R.id.textview_first)
        val countViewVal = textView.text.toString().substring(7).toInt()
        textView.text = "Value: ${countViewVal + 1}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}