package com.smartcut.Response

import com.google.gson.annotations.SerializedName

data class HairStyleResponse(

	@field:SerializedName("data")
	val data: HairStyleData? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class HairStyleData(

	@field:SerializedName("hairstyles")
	val hairstyles: List<HairstylesItem?>? = null
)

data class HairstylesItem(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("category")
	val category: String? = null,

	@field:SerializedName("picture")
	val picture: String? = null
)
