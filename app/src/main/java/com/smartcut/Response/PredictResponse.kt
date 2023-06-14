package com.smartcut.Response

import com.google.gson.annotations.SerializedName

data class PredictResponse(

	@field:SerializedName("data")
	val data: PredictData? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class PredictData(

	@field:SerializedName("reccomendations")
	val reccomendations: List<String?>? = null
)
