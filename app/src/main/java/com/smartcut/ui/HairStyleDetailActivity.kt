package com.smartcut.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.smartcut.Model.UserModel
import com.smartcut.Model.UserPreferences
import com.smartcut.R
import com.smartcut.databinding.ActivityHairStyleDetailBinding
import com.squareup.picasso.Picasso

class HairStyleDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHairStyleDetailBinding
    private lateinit var photoUrl: String
    private lateinit var name: String
    private lateinit var style: String
    private lateinit var description: String
    private lateinit var userPreference: UserPreferences
    private lateinit var userModel: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHairStyleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreferences(this)
        userModel = userPreference.getUser()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        val id: String = intent.getStringExtra(EXTRA_ID).toString()
        photoUrl = intent.getStringExtra(EXTRA_PHOTO_URL).toString()
        name = intent.getStringExtra(EXTRA_NAME).toString()
        style = intent.getStringExtra(EXTRA_STYLE).toString()
        description = intent.getStringExtra(EXTRA_DESCRIPTION).toString()

        setHairStyleDetail(id, photoUrl, name, style, description)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setHairStyleDetail(id: String, photoUrl: String, name: String, style: String, description: String) {
        Picasso.get().load(photoUrl).into(binding.ivHairDetail)
        binding.tvHairDetail.text = name
        binding.tvStyle.text = style
        binding.tvDescription.text = description
    }

    companion object {
        const val EXTRA_ID = "id"
        const val EXTRA_PHOTO_URL = "photoUrl"
        const val EXTRA_NAME = "name"
        const val EXTRA_STYLE = "style"
        const val EXTRA_DESCRIPTION = "description"
    }
}