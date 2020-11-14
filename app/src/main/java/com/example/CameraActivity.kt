package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.starrway_androidfinalproject.R
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {
    private var imageCapture: ImageCapture?=null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    companion object{
        private const val TAG="CameraXBasic"
        private const val FILENAME_FORMAT="yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS=10
        private val REQUIRED_PERMISSIONS=arrayOf(Manifest.permission.CAMERA)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        if(allPermissionsGranted()){
            startCamera()
        }
        else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
        outputDirectory=getOutputDirectory()
        cameraExecutor= Executors.newSingleThreadExecutor()
    }
    private fun allPermissionsGranted()= REQUIRED_PERMISSIONS.all{
        ContextCompat.checkSelfPermission(baseContext, it)== PackageManager.PERMISSION_GRANTED
    }
    private fun getOutputDirectory():File{
        val mediaDir=externalMediaDirs.firstOrNull()?.let{
            File (it, resources.getString(R.string.app_name)).apply{mkdir()}

        }
        return if (mediaDir !=null && mediaDir.exists())
            mediaDir else filesDir
    }
    private fun startCamera(){
        val imageCapture=imageCapture?: return
        val photoFile=File(outputDirectory, SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())+".jpg")
        //Create output options which contains files + metadata
        val outputOptions=ImageCapture.OutputFileOptions.Builder(photoFile).build()

        //set up image capture listener, which is triggerred after photo has been taken
        imageCapture.takePicture(outputOptions,
            ContextCompat.getMainExecutor(this), object: ImageCapture.OnImageSavedCallback{
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG,"Photo capture failed ${exc.message}", exc)
                }


                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri= Uri.fromFile(photoFile)
                    val msg="Photo capture succeeded "+ savedUri.toString()
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG,savedUri.toString())
                }
            }
        )
    }
    fun shootPhoto(view: View){
        val imageCapture=imageCapture?: return
        val photoFile=File(outputDirectory, SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())+".jpg")
        //Create output options which contains files + metadata
        val outputOptions=ImageCapture.OutputFileOptions.Builder(photoFile).build()

        //set up image capture listener, which is triggerred after photo has been taken
        imageCapture.takePicture(outputOptions,
            ContextCompat.getMainExecutor(this), object: ImageCapture.OnImageSavedCallback{
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG,"Photo capture failed ${exc.message}", exc)
                }


                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri= Uri.fromFile(photoFile)
                    val msg="Photo capture succeeded "+ savedUri.toString()
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG,savedUri.toString())
                }
            }
        )
    }
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}