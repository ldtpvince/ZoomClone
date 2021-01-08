package us.zoom.sdksample.otherfeatures;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import us.zoom.sdksample.databinding.CameraPreviewViewBinding;
import us.zoom.sdksample.ui.EmailUserLoginActivity;

public class MyScanQRActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final int MY_CAMERA_REQUEST_CODE = 100;

    @Nullable
    private ProcessCameraProvider cameraProvider;
    @Nullable
    private Preview previewUseCase;

    CameraSelector cameraSelector;

    private CameraPreviewViewBinding binding;

    @Override
    protected void onCreate(Bundle saveInstancedState) {
        super.onCreate(saveInstancedState);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }

        cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();

        binding = CameraPreviewViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        new ViewModelProvider((ViewModelStoreOwner) this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(CameraXViewModel.class)
                .getProcessCameraProvider()
                .observe(
                        (LifecycleOwner) this,
                        provider -> {
                            cameraProvider = provider;
                        });

        previewUseCase = new Preview.Builder().build();
        previewUseCase.setSurfaceProvider(binding.previewView.createSurfaceProvider());
        cameraProvider.bindToLifecycle(this, cameraSelector, previewUseCase);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult) {
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(this, EmailUserLoginActivity.class);
                startActivity(intent);
            }
        }
    }
}
