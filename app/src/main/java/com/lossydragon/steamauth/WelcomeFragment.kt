package com.lossydragon.steamauth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.lossydragon.steamauth.utils.PrefsManager

/**
 * The first [Fragment] to show when there is no account saved.
 */
class WelcomeFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // If an account was already setup, go right to 2FA view.
        if (PrefsManager.sharedSecret.isNotEmpty())
            findNavController().navigate(R.id.SecondFragment)
    }
}
