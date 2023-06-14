package com.smartcut.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.smartcut.Model.UserModel
import com.smartcut.Model.UserPreferences
import com.smartcut.R
import com.smartcut.databinding.ActivityMainBinding
import com.smartcut.ui.ProfileActivity.Companion.fromProfile

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var userPreference: UserPreferences

    companion object {
        lateinit var userModel: UserModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreferences(this)
        userModel = userPreference.getUser()

        replaceFragment(HomeFragment())

        fromProfile = false

        val bottomNavView = binding.bottomNavView
        bottomNavView.background = null
        bottomNavView.menu.getItem(2).isEnabled = false
        bottomNavView.setOnNavigationItemSelectedListener(this)

        binding.fab.setOnClickListener {
            val toPhoto = Intent(this, CameraActivity::class.java)
            startActivity(toPhoto)
        }

    }

    //bottom navigation
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                replaceFragment(HomeFragment())
                val bottomNavView = binding.bottomNavView
                bottomNavView.menu.findItem(R.id.home).isChecked = true
                return true
            }

            R.id.hairstyle -> {
                replaceFragment(HairStyleFragment())
                val bottomNavView = binding.bottomNavView
                bottomNavView.menu.findItem(R.id.hairstyle).isChecked = true
                return true
            }

            R.id.barber -> {
                val toBarber = Intent(this, BarberActivity::class.java)
                startActivity(toBarber)
                return true
            }

            R.id.profile -> {
                val toProfile = Intent(this, ProfileActivity::class.java)
                startActivity(toProfile)
                return true
            }
        }
        return false
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransactions = fragmentManager.beginTransaction()
        fragmentTransactions.replace(R.id.main_layout, fragment)
        fragmentTransactions.addToBackStack(null)
        fragmentTransactions.commit()
    }

}