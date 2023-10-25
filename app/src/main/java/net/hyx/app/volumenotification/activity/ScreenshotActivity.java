package net.hyx.app.volumenotification.activity;

//import android.os.Bundle;
import static androidx.core.app.ServiceCompat.stopForeground;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ServiceCompat;

import net.hyx.app.volumenotification.service.ScreenshotForegroundService;

public class ScreenshotActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SCREENSHOT = 1;

    private MediaProjection mMediaProjection;
    private MediaProjectionManager mMediaProjectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Start the foreground service
        Intent serviceIntent = new Intent(this, ScreenshotForegroundService.class);
        serviceIntent.setAction(ScreenshotForegroundService.ACTION_START_FOREGROUND_SERVICE);
        startForegroundService(serviceIntent);

        mMediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent permissionIntent = mMediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(permissionIntent, REQUEST_CODE_SCREENSHOT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("DEBUG", "RESULT::");

//        stopForeground(ServiceCompat.STOP_FOREGROUND_REMOVE);

        if (requestCode == REQUEST_CODE_SCREENSHOT && resultCode == RESULT_OK) {
            // Get the MediaProjection
            MediaProjection mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);

            Log.d("DEBUG", "CAPTURED::");

            // Create a VirtualDisplay using the MediaProjection
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int density = metrics.densityDpi;
            int flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR;
            VirtualDisplay virtualDisplay = mMediaProjection.createVirtualDisplay("Screenshot",
                    metrics.widthPixels, metrics.heightPixels, density, flags, null, null, null);

            // Create a ImageReader to capture the screen
            ImageReader imageReader = ImageReader.newInstance(metrics.widthPixels, metrics.heightPixels, PixelFormat.RGBA_8888, 1);
            Surface surface = imageReader.getSurface();

            // Create a HandlerThread to handle the ImageReader callbacks
            HandlerThread handlerThread = new HandlerThread("Screenshot");
            handlerThread.start();
            Handler handler = new Handler(handlerThread.getLooper());

            // Capture the screen
            imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {

                    Log.d("DEBUG", "CAPTURED::onImageAvailable");

                    Image image = reader.acquireLatestImage();
                    if (image != null) {
                        // Process the image
                        // ...
                        // Release the image
                        image.close();
                    }
                }
            }, handler);


        }
    }

}
