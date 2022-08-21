package com.subkingofmobile.skpos_android

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import net.glxn.qrgen.android.QRCode
import com.subkingofmobile.skpos_android.DialogBox

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsContainerScreen.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsContainerScreen : Fragment() {
    lateinit var connectionHandler : ConnectionHandler
    lateinit var dialog : DialogBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Adds the Settings Fragment to the current Fragment
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings_container_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //Create a ConnectionHandler Object for the buttons to use when connecting
        super.onViewCreated(view, savedInstanceState)
        connectionHandler = ConnectionHandler(context!!, activity!!)
        dialog = DialogBox(context!!)

        // Set Button Actions
        view.findViewById<Button>(R.id.TestDeviceConnection).setOnClickListener { checkDeviceConnection() }
        view.findViewById<Button>(R.id.TestServerConnection).setOnClickListener { checkServerConnection() }
        view.findViewById<Button>(R.id.TestDeviceRegistration).setOnClickListener { checkDeviceRegistration() }

        // Get Cookie Value from Settings and Use it to Generate a Registration QR Code
        val imgView = view.findViewById<ImageView>(R.id.qrCode)
        val settings = SettingsManager(context!!)
        val cookieString = settings.getAccessKey()
        val imgBM : Bitmap = QRCode.from(cookieString).bitmap()
        imgView!!.setImageBitmap(Bitmap.createScaledBitmap(imgBM, 750, 750, false).trimBorders(Color.WHITE))
    }

    private fun checkDeviceConnection() {
        connectionHandler.isDeviceConnected(onCompletion = { resp ->
            dialog.showSingleOptionDialogBox("Your Internet is Connected!", "Server Responded with the status code ${resp.getInt("statusCode")}")
        }, onFailure = { errormsg ->
            dialog.showSingleOptionDialogBox("Your Internet is Not Working. Error:", errormsg)
        })
    }

    private fun checkServerConnection() {
        connectionHandler.isServerUp(onCompletion = { resp ->
            dialog.showSingleOptionDialogBox("The Server is Up!", resp.getString("msg"))
        }, onFailure = {errormsg ->
            dialog.showSingleOptionDialogBox("The Server is Down. Error:", errormsg)
        })
    }

    private fun checkDeviceRegistration() {
        connectionHandler.isDeviceRegistered(onCompletion = {resp ->
            if (resp.has("err")) {
                dialog.showSingleOptionDialogBox("Your Device Is Not Registered. Message:", resp.getString("err"))
            } else if (resp.has("msg")) {
                dialog.showSingleOptionDialogBox("Your Device is Registered! Message:", resp.getString("msg"))
            } else {
                dialog.showSingleOptionDialogBox("Something Went Wrong.", "Neither the positive msg flag or negative err flag were set by the server.")
            }
        }, onFailure = { errormsg ->
            dialog.showSingleOptionDialogBox("Oops! Your Device Can't Connect or Is Not Registered! Message:", errormsg)
        })
    }



    private fun Bitmap.trimBorders(color: Int): Bitmap {
        var startX = 0
        loop@ for (x in 0 until width) {
            for (y in 0 until height) {
                if (getPixel(x, y) != color) {
                    startX = x
                    break@loop
                }
            }
        }
        var startY = 0
        loop@ for (y in 0 until height) {
            for (x in 0 until width) {
                if (getPixel(x, y) != color) {
                    startY = y
                    break@loop
                }
            }
        }
        var endX = width - 1
        loop@ for (x in endX downTo 0) {
            for (y in 0 until height) {
                if (getPixel(x, y) != color) {
                    endX = x
                    break@loop
                }
            }
        }
        var endY = height - 1
        loop@ for (y in endY downTo 0) {
            for (x in 0 until width) {
                if (getPixel(x, y) != color) {
                    endY = y
                    break@loop
                }
            }
        }

        val newWidth = endX - startX + 1
        val newHeight = endY - startY + 1

        return Bitmap.createBitmap(this, startX, startY, newWidth, newHeight)
    }
}