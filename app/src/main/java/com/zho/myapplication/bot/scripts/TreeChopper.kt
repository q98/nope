package com.zho.myapplication.bot.scripts

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.Spinner
import com.zho.myapplication.R
import com.zho.myapplication.bot.AutomationScript
import com.zho.myapplication.bot.ScriptActionListener

class TreeChopper : AutomationScript {
    private lateinit var logTypeSpinner: Spinner
    private lateinit var startLocationSpinner: Spinner
    private lateinit var bankLogsCheckbox: CheckBox
    private var logType: String = ""
    private var startLocation: String = ""
    private var shouldBankLogs: Boolean = false
    private var actionListener: ScriptActionListener? = null



    override fun setScriptSettings(settings: Map<String, Any?>) {
        logType = settings["logType"] as String
        startLocation = settings["startLocation"] as String
        shouldBankLogs = settings["shouldBankLogs"] as? Boolean ?: false
    }

    override fun onStart() {
        Log.d("HERE","ScRIPTSTARTED")
        // Logic to initialize the script with settings


        // Initialization based on these settings
    }

    override fun onLoop(): Long {
        Log.d("SCRIPT", "Cutting: " + logType  +" " + "Location " + startLocation + " Should bank? " + shouldBankLogs)
        // Main logic of the script
        // Example: Find tree, chop tree, check inventory, etc.
        return 500 // Loop every 50 milliseconds
    }

    override fun onPause() {
        // Pause logic, if needed
    }

    override fun onExit() {
        // Cleanup logic, if needed
    }
    override fun setActionListener(listener: ScriptActionListener) {
        actionListener = listener
    }

    override fun createSettingsView(inflater: LayoutInflater, container: ViewGroup?): View {
        val view = inflater.inflate(R.layout.tree_chopping_script_settings, container, false)

        logTypeSpinner = view.findViewById(R.id.logTypeSpinner)
        startLocationSpinner = view.findViewById(R.id.startLocationSpinner)
        bankLogsCheckbox = view.findViewById(R.id.bankLogsCheckbox)

        // Populate spinners with values
        ArrayAdapter.createFromResource(
            inflater.context,
            R.array.log_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            logTypeSpinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            inflater.context,
            R.array.start_locations,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            startLocationSpinner.adapter = adapter
        }
        val confirmButton: Button = view.findViewById(R.id.confirmSettingsButton2)
        confirmButton.setOnClickListener {
            val selectedLogType = logTypeSpinner.selectedItem.toString()
            val selectedStartLocation = startLocationSpinner.selectedItem.toString()
            val shouldBankLogs = bankLogsCheckbox.isChecked

            // Create a map with the retrieved settings
            val settings = mapOf(
                "logType" to selectedLogType,
                "startLocation" to selectedStartLocation,
                "shouldBankLogs" to shouldBankLogs
            )

            // Set the script settings using the map
            setScriptSettings(settings)
            Log.d("gert", "presssed")
            actionListener?.onStartScript(this)
            actionListener?.onHideSettingsOverlay()

        }

        return view
    }
}
