package com.lossydragon.steamauth

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.lossydragon.steamauth.steamauth.SteamGuardAccount.generateSteamGuardCodeForTime
import com.lossydragon.steamauth.utils.PrefsManager
import kotlinx.android.synthetic.main.fragment_two_factor.*
import java.util.*

/**
 * The second [Fragment] to show when there is an account saved.
 * Will display the account saved to the app and the current TOTP code for Steam Authentication.
 */
class SteamFragment : Fragment() {

    private lateinit var clipData: ClipData
    private lateinit var clipboardManager: ClipboardManager

    private var secret: String? = null
    private var name: String? = null
    private var timeNewCode: Double = 10000.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        secret = PrefsManager.sharedSecret
        name = PrefsManager.accountName
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_two_factor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hide the FAB as we don't need it in this view.
        (activity as MainActivity?)?.getFloatingActionButton()?.visibility = View.GONE

        // Setup the Show Revocation button
        button_revocation.setOnClickListener {
            val revocation = String.format(
                    Locale.getDefault(),
                    getString(R.string.toast_revocation),
                    PrefsManager.revocationCode
            )
            Toast.makeText(context!!, revocation, Toast.LENGTH_LONG).show()
        }

        // Display the account name
        totp_account.text = name

        // Set up long-click clipboard
        totp_code?.setOnLongClickListener {
            clipboardManager =
                    context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipData = ClipData.newPlainText("Steam Guard", totp_code.text.toString())
            clipboardManager.setPrimaryClip(clipData)

            Toast.makeText(view.context, "Copied!", Toast.LENGTH_SHORT).show()
            true
        }

        // Start the auth generation
        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                if (secret != null) {
                    val validityTime = 30.0 - codeTime

                    totp_progress?.progress = (3000.0 * (validityTime / 30.0)).toInt()

                    val newCode = timeNewCode > validityTime
                    timeNewCode = validityTime
                    if (newCode) {
                        handler.post {
                            totp_code?.text = generateSteamGuardCodeForTime(secret!!, currentTime)
                        }
                    }
                } else {
                    totp_code.text = getString(R.string.totp_code_unknown)
                }

                // Repeat handler
                handler.postDelayed(this, 50)
            }
        }

        // Kick start the handler.
        handler.post(runnable)
    }

    companion object {
        private val codeTime: Double
            get() = 30.0 - System.currentTimeMillis() / 1000.0 % 30.0

        private val currentTime: Long
            get() = System.currentTimeMillis() / 1000L
    }
}
