package com.smartcut.ui

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.smartcut.Adapter.HotNowAdapter
import com.smartcut.Adapter.PredictAdapter
import com.smartcut.Api.ApiConfig
import com.smartcut.R
import com.smartcut.Response.HairStyleData
import com.smartcut.Response.PredictData
import com.smartcut.databinding.ActivityPredictBinding
import com.smartcut.ui.CameraActivity.Companion.PHOTO_DATA

class PredictActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPredictBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPredictBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        getList(PHOTO_DATA)

        binding.btnFindBarber.setOnClickListener{
            val intent = Intent(this, BarberActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getList(data: PredictData?) {
        val adapter = PredictAdapter(data)
        binding.recyclerView.adapter = adapter
    }

}