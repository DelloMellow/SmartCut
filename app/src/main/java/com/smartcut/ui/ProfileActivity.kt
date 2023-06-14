package com.smartcut.ui

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.smartcut.Model.UserModel
import com.smartcut.Model.UserPreferences
import com.smartcut.R
import com.smartcut.databinding.ActivityProfileBinding
import com.smartcut.rotateFile
import com.squareup.picasso.Picasso
import java.io.File

class ProfileActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var userPreference: UserPreferences
    private lateinit var userModel: UserModel
    private var getFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        userPreference = UserPreferences(this)
        userModel = userPreference.getUser()

        showProfile()

        fromProfile = true


        val profileImage = binding.ivProfile

        profileImage.setOnClickListener(this)


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.profile_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        } else if (item.itemId == R.id.logout) {
            deleteSession()
            val logout = Intent(this, LoginActivity::class.java)
            startActivity(logout)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteSession() {
        val mUserPreference = UserPreferences(this)
        userModel.token = null
        mUserPreference.setUser(userModel)
    }

    private fun showProfile() {
        val name = binding.edtProfileName
        val email = binding.edtProfileEmail
        val phone = binding.edtProfilePhone
        val profile = binding.ivProfile

        name.setText(userModel.name)
        email.setText(userModel.email)
        phone.setText(userModel.phone)

        val photoUrl = userPreference.getUser().photoUrl

        if (photoUrl.isNullOrEmpty()) {
            profile.setImageResource(R.drawable.donat_kegigit)
        } else {
            userModel.photoUrl = photoUrl
            userPreference.setUser(userModel)
            Picasso.get().load(userModel.photoUrl).into(binding.ivProfile)
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val toMain = Intent(this, MainActivity::class.java)
        startActivity(toMain)
        super.onBackPressed()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_profile -> {
                startCameraX()
            }
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        //camera x
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra("picture", File::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.data?.getSerializableExtra("picture")
            } as? File

            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            myFile?.let { file ->
                rotateFile(file, isBackCamera)
                getFile = file
                binding.ivProfile.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        var fromProfile = true
    }

}