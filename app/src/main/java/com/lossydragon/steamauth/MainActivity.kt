package com.lossydragon.steamauth

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.lossydragon.steamauth.utils.*
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Main Activity to contain the fragments and provide basic functions
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // Init our preferences
        PrefsManager.init(applicationContext)

        fab.setOnClickListener {
            checkPermissions()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                deleteAccount()
                true
            }
            R.id.action_info -> {
                showInfo()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) ==
                PermissionChecker.PERMISSION_GRANTED) {
            //Permission already Granted, go to file chooser dialog
            addAccount()
        } else {
            //Ask for permissions.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)) {
                //Ask for permissions with reasoning.
                requestPermissionsReasoning()
            } else {
                //Ask for permissions, normal
                ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE), 42)
            }
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 42) {
            if (PrefsManager.firstTime) {
                showInfo()
            }
        }
    }

    // Return the FAB view object to a fragment
    fun getFloatingActionButton(): ExtendedFloatingActionButton? = fab

}
