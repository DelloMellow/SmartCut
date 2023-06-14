package com.smartcut.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.smartcut.Adapter.HairStyleAdapter
import com.smartcut.Api.ApiConfig
import com.smartcut.Model.UserModel
import com.smartcut.Model.UserPreferences
import com.smartcut.R
import com.smartcut.Response.HairStyleData
import com.smartcut.Response.HairStyleResponse
import com.smartcut.databinding.FragmentHairStyleBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HairStyleFragment : Fragment() {

    private var _binding: FragmentHairStyleBinding? = null
    private val binding get() = _binding!!
    private lateinit var userPreference: UserPreferences
    private lateinit var userModel: UserModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHairStyleBinding.inflate(inflater, container, false)

        userPreference = UserPreferences(requireContext())
        userModel = userPreference.getUser()

        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back)
        }

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        showList()

        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {

            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()

            return true
        }
        return super.onOptionsItemSelected(item)
    }


    private fun showList() {
        val client =
            ApiConfig.getApiService().getHairStyle(authorization = "Bearer ${userModel.token}")
        client.enqueue(object : Callback<HairStyleResponse> {
            override fun onResponse(
                call: Call<HairStyleResponse>,
                response: Response<HairStyleResponse>
            ) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        getList(response.body()!!.data)
                    }
                } else {
                    try {
                        val data = response.errorBody()?.string()
                        val jsonObject = JSONObject(data!!)
                        Toast.makeText(
                            requireContext(),
                            jsonObject.getString("message"),
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<HairStyleResponse>, t: Throwable) {
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getList(data: HairStyleData?) {
        val listId = ArrayList<Int?>()
        val listImage = ArrayList<String?>()
        val listName = ArrayList<String?>()
        val listStyle = ArrayList<String?>()
        val description = ArrayList<String>()
        if (data != null) {
            val hairstyles = data.hairstyles
            if (hairstyles != null) {
                for (list in hairstyles) {
                    listId.add(list?.id)
                    listImage.add(list?.picture)
                    listName.add(list?.name)
                    listStyle.add(list?.category)
                    description.add(list?.description!!)
                }
            }
        }

        val adapter =
            HairStyleAdapter(listId, listImage, listName, listStyle, description, requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun getDescription(id: Int) {

    }

}