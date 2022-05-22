package com.sharkBytesLab.cryptoapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sharkBytesLab.cryptoapp.databinding.ActivityPolicyBinding

class PolicyActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPolicyBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            binding.policyWebView.settings.javaScriptEnabled = true
            binding.policyWebView.loadUrl("file:///android_asset/policy.html")
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    override fun onBackPressed() {
        super.onBackPressed()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()

    }
}