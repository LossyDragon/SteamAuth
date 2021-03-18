package com.lossydragon.steamauth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.lossydragon.steamauth.utils.PrefsManager

/**
 * The first [Fragment] to show when there is no account saved.
 */
class WelcomeFragment : Fragment(R.layout.fragment_welcome) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // If an account was already setup, go right to 2FA view.
        if (PrefsManager.sharedSecret.isNotEmpty())
            findNavController().navigate(R.id.SecondFragment)
    }
}
