package com.sharkBytesLab.cryptoapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.sharkBytesLab.cryptoapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
        val navController = navHostFragment!!.findNavController()

        val popupMenu = PopupMenu(this, null)
        popupMenu.inflate(R.menu.bottom_nav_menu)
        binding.bottomBar.setupWithNavController(popupMenu.menu, navController)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
        return true
    }

    fun rating()
    {
        val uri =
            Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())
        val i = Intent(Intent.ACTION_VIEW, uri)

        try {
            startActivity(i)
        } catch (e: Exception) {
            Toast.makeText(this, "Error :" + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun sharing()
    {
        val shareBody =          """
            Hey, I'm using CoinNet to track all crypto currency details, Its easy to use. Have a look to this amazing app. 
            Download from Play Store
            https://play.google.com/store/apps/details?id=${getPackageName()}
            """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, shareBody)
        startActivity(intent)
    }

    fun checkPolicy()
    {
        val intent = Intent(this, PolicyActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId)
        {
            R.id.rate -> rating()
            R.id.share -> sharing()
            R.id.policy -> checkPolicy()
        }
        return super.onOptionsItemSelected(item)
    }


}