package com.smartcut.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.smartcut.Api.ApiConfig
import com.smartcut.Model.UserModel
import com.smartcut.Model.UserPreferences
import com.smartcut.Response.PhotoProfileResponse
import com.smartcut.Response.PredictData
import com.smartcut.Response.PredictResponse
import com.smartcut.createFile
import com.smartcut.databinding.ActivityCameraBinding
import com.smartcut.reduceFileImage
import com.smartcut.ui.ProfileActivity.Companion.fromProfile
import com.smartcut.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private var imageCapture: ImageCapture? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
    private var getFile: File? = null
    private lateinit var mUserPreferences: UserPreferences
    private lateinit var mUserModel: UserModel

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        mUserPreferences = UserPreferences(this)
        mUserModel = mUserPreferences.getUser()


        binding.captureImage.setOnClickListener {
            takePhoto()
        }

        binding.switchCamera.setOnClickListener {
            cameraSelector =
                if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) CameraSelector.DEFAULT_BACK_CAMERA
                else CameraSelector.DEFAULT_FRONT_CAMERA
            startCamera()
        }

        binding.gallery.setOnClickListener {
            startGallery()
        }
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUI()
        startCamera()
    }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    //start camera
    private fun startCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )

            } catch (exc: Exception) {
                Toast.makeText(
                    this@CameraActivity,
                    "Gagal memunculkan kamera: ${exc.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    //take photo
    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = createFile(application)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(
                        this@CameraActivity,
                        "Gagal mengambil gambar: : ${exc.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    getFile = photoFile
                    if (!fromProfile) {
                        uploadImage()
                    } else {
                        val intent = Intent()
                        intent.putExtra("picture", photoFile)
                        intent.putExtra(
                            "isBackCamera",
                            cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA
                        )
                        setResult(ProfileActivity.CAMERA_X_RESULT, intent)
                        finish()
                        uploadProfilePicture()
                    }
                }
            }
        )
    }

    //start gallery
    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this)
                getFile = myFile
                if (!fromProfile) {
                    uploadImage()
                } else {
                    val intent = Intent()
                    intent.putExtra("picture", myFile)
                    setResult(ProfileActivity.CAMERA_X_RESULT, intent)
                    finish()
                    uploadProfilePicture()
                }
            }
        }
    }

    private fun uploadImage() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val imageFile = file.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "image",
                file.name,
                imageFile
            )
            val client = ApiConfig.getPredictService().predict(imageMultipart)
            client.enqueue(object : Callback<PredictResponse> {
                override fun onResponse(
                    call: Call<PredictResponse>,
                    response: Response<PredictResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            val intent = Intent(this@CameraActivity, PredictActivity::class.java)
                            PHOTO_DATA = responseBody.data
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Toast.makeText(
                            this@CameraActivity,
                            response.errorBody()?.string(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<PredictResponse>, t: Throwable) {
                    Toast.makeText(this@CameraActivity, t.message, Toast.LENGTH_SHORT).show()
                }

            })
        } else {
            Toast.makeText(this, "Silahkan masukkan berkas gambar dahulu", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun uploadProfilePicture() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val imageFile = file.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "picture",
                file.name,
                imageFile
            )
            val client = ApiConfig.getApiService()
                .editPhoto(
                    authorization = "bearer ${mUserModel.token}",
                    mUserModel.id!!,
                    imageMultipart
                )
            client.enqueue(object : Callback<PhotoProfileResponse> {
                override fun onResponse(
                    call: Call<PhotoProfileResponse>,
                    response: Response<PhotoProfileResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            mUserModel.photoUrl = responseBody.data.pictureUrl
                            mUserPreferences.setUser(mUserModel)
                            val intent = Intent(this@CameraActivity, ProfileActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Toast.makeText(
                            this@CameraActivity,
                            response.errorBody()?.string(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<PhotoProfileResponse>, t: Throwable) {
                    Toast.makeText(this@CameraActivity, "onfailure", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
        var PHOTO_DATA: PredictData? = null
    }
}