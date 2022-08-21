package com.subkingofmobile.skpos_android

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import com.subkingofmobile.skpos_android.order_format_handling.FormatDB

/**
 * A simple [Fragment] subclass.
 * Use the [NewOrderFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewOrderFragment : Fragment() {
    private lateinit var formatDB : FormatDB
    private lateinit var dialog : DialogBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.new_order_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        formatDB = FormatDB(context!!, activity!!)
        dialog = DialogBox(context!!)

        formatDB.getCurrentFormatID(
            onLoadID = { formatID ->
                dialog.showSingleOptionDialogBox("Current Format ID:", formatID)
            },
            onConnectionFailure = {errormsg ->
                dialog.showSingleOptionDialogBox("Error:", errormsg, {activity!!.onBackPressed()})
            }
        )
    }
}