package com.smartcut.ui

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.smartcut.Api.ApiConfig
import com.smartcut.CustomView.EditTextPassword
import com.smartcut.CustomView.EditTextUsername
import com.smartcut.Model.UserModel
import com.smartcut.Model.UserPreferences
import com.smartcut.R
import com.smartcut.Response.LoginRegisterDataClass
import com.smartcut.Response.LoginResponse
import com.smartcut.Response.ProfileResponse
import com.smartcut.databinding.ActivityLoginBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity(), View.OnClickListener,
    CompoundButton.OnCheckedChangeListener {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var usernameEditText: EditTextUsername
    private lateinit var passwordEditText: EditTextPassword


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        usernameEditText = binding.edtUsername
        passwordEditText = binding.edtPassword
        binding.cbShowPassword.setOnCheckedChangeListener(this)

        binding.btnLogin.setOnClickListener(this)
        binding.tvRegister.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_login -> {
                val username = usernameEditText.text.toString()
                val password = passwordEditText.text.toString()

                showLoading(true)

                if (usernameEditText.error.isNullOrEmpty() && passwordEditText.error.isNullOrEmpty()) {
                    val dataLogin = createLoginRegisterDataClass(username, password)
                    val client = ApiConfig.getApiService().login(dataLogin)
                    client.enqueue(object : Callback<LoginResponse> {
                        override fun onResponse(
                            call: Call<LoginResponse>,
                            response: Response<LoginResponse>
                        ) {
                            if (response.isSuccessful) {
                                val responseBody = response.body()
                                if (responseBody != null) {
                                    val token = responseBody.data?.token!!
                                    showLoading(false)

                                    saveUser(token, username)

                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Login success",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    toMain()
                                    finish()
                                }
                            } else {
                                try {
                                    val data = response.errorBody()?.string()
                                    val jsonObject = JSONObject(data!!)
                                    Toast.makeText(
                                        this@LoginActivity,
                                        jsonObject.getString("message"),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    showLoading(false)
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        e.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }

                        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                            Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_SHORT).show()
                        }

                    })
                } else {
                    Toast.makeText(this, "Check again your email or password!", Toast.LENGTH_SHORT)
                        .show()
                }

            }
            R.id.tv_register -> {
                val toRegister = Intent(this, RegisterActivity::class.java)
                startActivity(toRegister)
                finish()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun saveUser(token: String, username: String) {
        val client = ApiConfig.getApiService().getProfile(
            authorization = "Bearer $token",
            username
        )
        client.enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(
                call: Call<ProfileResponse>,
                response: Response<ProfileResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val id = responseBody.data?.user?.id!!
                        val name = responseBody.data.user.name!!
                        val email = responseBody.data.user.email!!
                        val phone = responseBody.data.user.phone!!
                        val photoUrl = responseBody.data.user.picture
                        saveUserData(token, id, name, username, email, phone, photoUrl)
                    }
                } else {
                    try {
                        val data = response.errorBody()?.string()
                        val jsonObject = JSONObject(data!!)
                        Toast.makeText(
                            this@LoginActivity,
                            jsonObject.getString("message"),
                            Toast.LENGTH_SHORT
                        ).show()
                        showLoading(false)
                    } catch (e: Exception) {
                        Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun saveUserData(
        token: String,
        id: String,
        name: String,
        username: String,
        email: String,
        phone: String,
        photoUrl: String?
    ) {
        val userPreferences = UserPreferences(this)
        val userModel = UserModel()
        userModel.token = token
        userModel.id = id
        userModel.name = name
        userModel.username = username
        userModel.email = email
        userModel.phone = phone
        userModel.photoUrl = photoUrl
        userPreferences.setUser(userModel)
    }

    private fun createLoginRegisterDataClass(
        username: String,
        password: String
    ): LoginRegisterDataClass {
        return LoginRegisterDataClass(username = username, password = password)
    }

    private fun toMain() {
        val toMain = Intent(this, MainActivity::class.java)
        startActivity(toMain)
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (isChecked) {
            passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
        }
    }

}