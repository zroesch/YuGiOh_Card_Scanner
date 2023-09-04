import androidx.camera.core.CameraSelector
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier.fillMaxSize(),
    cameraSelector: CameraSelector,
    onCameraOpened: () -> Unit,
    onError: (String) -> Unit
) {
    val context = LocalContext.current
    val density = LocalDensity.current.density

    val previewView = remember {
        PreviewView(context).apply {
            layoutParams = android.widget.FrameLayout.LayoutParams(
                android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                android.widget.FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { previewView },
        update = {
            val cameraProviderFuture = androidx.camera.lifecycle.ProcessCameraProvider.getInstance(context)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = androidx.camera.core.Preview.Builder()
                    .build()

                preview.setSurfaceProvider(previewView.surfaceProvider)

                try {
                    val camera = cameraProvider.bindToLifecycle(
                        context as LifecycleOwner,
                        cameraSelector,
                        preview
                    )
                    onCameraOpened()
                } catch (ex: Exception) {
                    onError("Error opening camera: ${ex.message}")
                }
            }, ContextCompat.getMainExecutor(context))
        }
    )
}
