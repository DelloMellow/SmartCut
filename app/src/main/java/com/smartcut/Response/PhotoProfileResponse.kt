package com.smartcut.Response

import com.google.gson.annotations.SerializedName

data class PhotoProfileResponse(

	@field:SerializedName("data")
	val data: PhotoProfileData,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: String
)

data class PhotoProfileData(

	@field:SerializedName("picture_url")
	val pictureUrl: String
)
