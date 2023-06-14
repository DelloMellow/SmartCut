package com.smartcut.Api

import com.smartcut.Response.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    //untuk login
    @Headers("Content-Type: application/json")
    @POST("users/login")
    fun login(
        @Body body: LoginRegisterDataClass
    ): Call<LoginResponse>

    //untuk register
    @Headers("Content-Type: application/json")
    @POST("users")
    fun register(
        @Body body: LoginRegisterDataClass
    ): Call<RegisterResponse>

    //untuk list hairstyle
    @Headers("Content-Type: application/json")
    @GET("hairstyles")
    fun getHairStyle(
        @Header("Authorization") authorization: String,
        @Query("name") name: String? = null,
        @Query("category") category: String? = null
    ): Call<HairStyleResponse>

    //untuk profile user
    @Headers("Content-Type: application/json")
    @GET("users/{username}")
    fun getProfile(
        @Header("authorization")authorization: String,
        @Path("username") username: String
    ): Call<ProfileResponse>

    //untuk upload foto predict
    @Multipart
    @POST("detect-model")
    fun predict(
        @Part file: MultipartBody.Part
    ): Call<PredictResponse>

    //untuk edit data profile user
    @Headers("Content-Type: application/json")
    @GET("users/{id}")
    fun editProfile(
        @Header("authorization")authorization: String,
        @Path("id") id: String
    ): Call<ProfileResponse>

    //untuk upload foto predict
    @Multipart
    @PUT("users/{id}/profile-picture")
    fun editPhoto(
        @Header("authorization")authorization: String,
        @Path("id") id: String,
        @Part file: MultipartBody.Part
    ): Call<PhotoProfileResponse>
}