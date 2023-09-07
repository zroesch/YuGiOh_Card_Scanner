package com.example.yugiohcardscanner

import CameraPreview
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.yugiohcardscanner.ui.theme.YuGiOhCardScannerTheme
import androidx.activity.compose.setContent
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var imageCapture: ImageCapture
    private val textRecognitionHelper = TextRecognitionHelper()

    companion object {
        const val CAMERA_PERMISSION_REQUEST_CODE = 1001
    }

    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isCameraPermissionGranted()) {
            setupContent()
        } else {
            requestCameraPermission()
        }

    }


    private fun setupContent() {
        setContent {
            YuGiOhCardScannerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        textRecognitionHelper = textRecognitionHelper
                    )
                }
            }
        }
    }

    private fun isCameraPermissionGranted() =
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MainScreen(
    textRecognitionHelper: TextRecognitionHelper
) {
    var isCameraOpen by remember { mutableStateOf(false) }
    var recognizedText by remember { mutableStateOf("") }

    val keyboardController = LocalSoftwareKeyboardController.current

    val cameraSelector = remember { CameraSelector.DEFAULT_BACK_CAMERA }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = {
                isCameraOpen = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text(text = "Open Camera")
        }

        BasicTextField(
            value = recognizedText,
            onValueChange = {},
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .border(1.dp, Color.Black)
                .padding(4.dp)
        )

        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            cameraSelector = cameraSelector,
            onCameraOpened = { /* Camera is opened, you can capture images or do other camera-related tasks here */ },
            onError = { errorMessage ->
                // Handle camera errors
                recognizedText = errorMessage
            }
        )
    }
}