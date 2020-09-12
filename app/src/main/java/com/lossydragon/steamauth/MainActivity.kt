package com.lossydragon.steamauth

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.navigation.findNavController
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.lossydragon.steamauth.steamauth.MaFileLoader
import com.lossydragon.steamauth.utils.PrefsManager
import com.lossydragon.steamauth.utils.deleteAccount
import com.lossydragon.steamauth.utils.requestPermissionsReasoning
import com.lossydragon.steamauth.utils.showInfo
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.io.InputStream


/**
 * Main Activity to contain the fragments and provide basic functions
 */
class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE = 447
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val currentUri: Uri

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            if (data != null) {
                currentUri = data.data!!

                try {
                    val jsonString: String = readFileContent(currentUri)
                    val result = MaFileLoader.importMaFile(jsonString)

                    if (result) {
                        findNavController(R.id.nav_host_fragment).navigate(R.id.SecondFragment)
                    }

                } catch (e: IOException) {
                    // Handle error here
                }
            }
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) ==
            PermissionChecker.PERMISSION_GRANTED
        ) {
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

    private fun addAccount() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json" // This is just dumb
        }
        startActivityForResult(intent, REQUEST_CODE)
    }

    private fun readFileContent(uri: Uri): String {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        return inputStream!!.bufferedReader().readText()
    }

    // Return the FAB view object to a fragment
    fun getFloatingActionButton(): ExtendedFloatingActionButton? = fab
}
