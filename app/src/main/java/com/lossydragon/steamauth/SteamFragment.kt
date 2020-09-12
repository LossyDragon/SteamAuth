package com.lossydragon.steamauth

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager.LayoutParams.FLAG_SECURE
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.lossydragon.steamauth.steamauth.SteamGuardAccount.generateSteamGuardCodeForTime
import com.lossydragon.steamauth.utils.PrefsManager
import kotlinx.android.synthetic.main.fragment_two_factor.*

/**
 * The second [Fragment] to show when there is an account saved.
 * Will display the account saved to the app and the current TOTP code for Steam Authentication.
 */
class SteamFragment : Fragment() {

    private lateinit var clipData: ClipData
    private lateinit var clipboardManager: ClipboardManager

    private lateinit var secret: String
    private lateinit var name: String
    private var timeNewCode: Double = 10000.0

    private var looper = Looper.myLooper()

    private val codeTime: Double
        get() = 30.0 - System.currentTimeMillis() / 1000.0 % 30.0

    private val currentTime: Long
        get() = System.currentTimeMillis() / 1000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        secret = PrefsManager.sharedSecret
        name = PrefsManager.accountName
    }

    // Inflate the layout for this fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_two_factor, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hide the FAB as we don't need it in this view.
        (activity as MainActivity?)?.getFloatingActionButton()?.visibility = View.GONE

        // Block screenshots and overview preview
        requireActivity().window.setFlags(FLAG_SECURE, FLAG_SECURE)

        // Setup the Show Revocation button
        button_revocation.setOnClickListener {
            Toast.makeText(
                requireContext(),
                getString(R.string.toast_revocation, PrefsManager.revocationCode),
                Toast.LENGTH_LONG
            ).show()
        }

        // Display the account name
        totp_account.text = name

        // Set up long-click clipboard
        totp_code?.setOnLongClickListener {
            clipboardManager =
                context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipData =
                ClipData.newPlainText(getString(R.string.clip_label), totp_code.text.toString())
            clipboardManager.setPrimaryClip(clipData)

            Toast.makeText(view.context, R.string.toast_copied, Toast.LENGTH_SHORT).show()
            true
        }

        // Start the auth generation
        Handler(looper!!).apply {
            val runnable = object : Runnable {
                override fun run() {
                    val validityTime = 30.0 - codeTime
                    totp_progress?.progress = (3000.0 * (validityTime / 30.0)).toInt()

                    val newCode = timeNewCode > validityTime
                    timeNewCode = validityTime

                    if (newCode) {
                        this@apply.post {
                            totp_code?.text = generateSteamGuardCodeForTime(secret, currentTime)
                        }
                    }

                    postDelayed(this, 50)
                }
            }
            postDelayed(runnable, 50)
        }
    }
}
