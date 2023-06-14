package com.smartcut.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.smartcut.Adapter.HotNowAdapter
import com.smartcut.Api.ApiConfig
import com.smartcut.Model.UserModel
import com.smartcut.Model.UserPreferences
import com.smartcut.R
import com.smartcut.Response.HairStyleData
import com.smartcut.Response.HairStyleResponse
import com.smartcut.databinding.FragmentMainBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var mUserPreferences: UserPreferences
    private lateinit var mUserModel: UserModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        mUserPreferences = UserPreferences(requireContext())
        mUserModel = mUserPreferences.getUser()
        binding.tvWelcomeName.text = mUserModel.name

        showCarousel()

        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        showList()

        return binding.root
    }

    //image slider
    private fun showCarousel() {
        val imageSlider = binding.imageSlider
        val slideModelList: MutableList<SlideModel> = mutableListOf()
        slideModelList.add(SlideModel(R.drawable.carousel1))
        slideModelList.add(SlideModel(R.drawable.carousel2))
        slideModelList.add(SlideModel(R.drawable.carousel3))

        imageSlider.setImageList(slideModelList, ScaleTypes.FIT)
    }

    private fun showList() {
        val client =
            ApiConfig.getApiService().getHairStyle(authorization = "Bearer ${mUserModel.token}")
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
            HotNowAdapter(listId, listImage, listName, listStyle, description, requireContext())
        binding.recyclerView.adapter = adapter
    }

}