package com.smartcut.ui

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.smartcut.Api.ApiConfig
import com.smartcut.CustomView.EditTextEmail
import com.smartcut.CustomView.EditTextPassword
import com.smartcut.CustomView.EditTextPhoneNumber
import com.smartcut.CustomView.EditTextUsername
import com.smartcut.R
import com.smartcut.Response.LoginRegisterDataClass
import com.smartcut.Response.RegisterResponse
import com.smartcut.databinding.ActivityRegisterBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity(), View.OnClickListener,
    CompoundButton.OnCheckedChangeListener {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var nameEditText: EditTextUsername
    private lateinit var usernameEditText: EditTextUsername
    private lateinit var emailEditText: EditTextEmail
    private lateinit var phoneNumberEditText: EditTextPhoneNumber
    private lateinit var passwordEditText: EditTextPassword

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        nameEditText = binding.edtName
        usernameEditText = binding.edtUsernameRegister
        emailEditText = binding.edtEmail
        phoneNumberEditText = binding.edtPhone
        passwordEditText = binding.edtPasswordRegister
        binding.cbShowPassword.setOnCheckedChangeListener(this)

        binding.btnRegister.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_register -> {
                val name = nameEditText.text.toString()
                val username = usernameEditText.text.toString()
                val password = passwordEditText.text.toString()
                val email = emailEditText.text.toString()
                val phone = phoneNumberEditText.text.toString()

                showLoading(true)

                if (nameEditText.error.isNullOrEmpty() && usernameEditText.error.isNullOrEmpty()
                    && isEmailValid(email) && phoneNumberEditText.error.isNullOrEmpty()
                    && passwordEditText.error.isNullOrEmpty()
                ) {
                    val dataRegister =
                        createLoginRegisterDataClass(name, username, password, email, phone)
                    val client = ApiConfig.getApiService().register(dataRegister)
                    client.enqueue(object : Callback<RegisterResponse> {
                        override fun onResponse(
                            call: Call<RegisterResponse>,
                            response: Response<RegisterResponse>
                        ) {
                            if (response.isSuccessful) {
                                if (response.body() != null) {
                                    showLoading(false)
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "Register account success",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    toLogin()
                                    finish()
                                }
                            } else {
                                showLoading(false)
                                try {
                                    val data = response.errorBody()?.string()
                                    val jsonObject = JSONObject(data!!)
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        jsonObject.getString("message"),
                                        Toast.LENGTH_SHORT
                                    ).show()
//                                    Toast.makeText(
//                                        this@RegisterActivity,
//                                        name+""+username+""+email+""+phone+""+password,
//                                        Toast.LENGTH_LONG
//                                    ).show()
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        e.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }

                        override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                            showLoading(false)
                            Toast.makeText(this@RegisterActivity, t.message, Toast.LENGTH_SHORT)
                                .show()
                        }

                    })
                } else {
                    Toast.makeText(
                        this,
                        "Check again your data!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            R.id.tv_toLogin -> {
                toLogin()
            }
        }
    }

    private fun toLogin() {
        val toLogin = Intent(this, LoginActivity::class.java)
        startActivity(toLogin)
    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (isChecked) {
            passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
        }
    }

    private fun createLoginRegisterDataClass(
        name: String,
        username: String,
        password: String,
        email: String,
        phone: String
    ): LoginRegisterDataClass {
        return LoginRegisterDataClass(
            name = name,
            username = username,
            password = password,
            email = email,
            phone = phone
        )
    }
}