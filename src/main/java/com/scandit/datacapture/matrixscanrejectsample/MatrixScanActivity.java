/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.scandit.datacapture.matrixscanrejectsample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import com.scandit.datacapture.barcode.data.Barcode;
import com.scandit.datacapture.barcode.data.Symbology;
import com.scandit.datacapture.barcode.tracking.capture.BarcodeTracking;
import com.scandit.datacapture.barcode.tracking.capture.BarcodeTrackingListener;
import com.scandit.datacapture.barcode.tracking.capture.BarcodeTrackingSession;
import com.scandit.datacapture.barcode.tracking.capture.BarcodeTrackingSettings;
import com.scandit.datacapture.barcode.tracking.data.TrackedBarcode;
import com.scandit.datacapture.barcode.tracking.ui.overlay.BarcodeTrackingBasicOverlay;
import com.scandit.datacapture.barcode.tracking.ui.overlay.BarcodeTrackingBasicOverlayListener;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.data.FrameData;
import com.scandit.datacapture.core.source.Camera;
import com.scandit.datacapture.core.source.CameraSettings;
import com.scandit.datacapture.core.source.FrameSourceState;
import com.scandit.datacapture.core.source.VideoResolution;
import com.scandit.datacapture.core.ui.DataCaptureView;
import com.scandit.datacapture.core.ui.style.Brush;
import com.scandit.datacapture.matrixscanrejectsample.data.ScanResult;

import java.util.HashSet;

public class MatrixScanActivity extends CameraPermissionActivity implements BarcodeTrackingListener {

    // Enter your Scandit License key here.
    // Your Scandit License key is available via your Scandit SDK web account.
    public static final String SCANDIT_LICENSE_KEY = "AdjQyB8OPnRtGM2PMhypVH8nv9iOBz79tFGjIvwf1xqSeUAyNnn+VCB3dYNRb3mrTkj8r+t4IjUbBaFIo1v6aW9ECbl9YQr+vBwDgxpGH4LQYTpYhzy+/Q9OGvHSdygUhkKRnuxjTWlMGqFFPG51RldVo+j6Z0+x0k2qiNhNC9coAhujFFLXC/hR6t3+bKywyVcO5OBT/51fWT4XKCk0d3xtwSeQRBSJWXuuw0MmUc6hSL0fnkbKtBxKTGfiWsfOiG0GTHFf5KhEZ0gzo0yxhXF65o8fLmDScQgOKCBXZ0FHdz1sYmf/jshphttKJep4V0J1szN0kS7aXIZ9z3CSzINUlWUEQC+NFWwdY3sWrPtYZOhJikY0iHl9eCBYYJNem2ixMrthF/qpVcSso2q3PhtaPNldcPRlyVKvEFhT+7ttT1gzv1t1xbVwUX1MTEFD01R1u6xkx0e6FeMy3nLYkxp5hggFCfj5JlHya9NA6WrPaZLormLqxF18jThUXoG6mRfZpp4obqLTLTIEnSyNbktKvnUOyQXFr7HZ7FVHcA6xsHdJuoHhvLdYihCfAPeI/p1g/xhbiB8e8wpEIG/VPtljd3hMDfk5f6CGqW9ZJNJ/CQOub09tL1GcEZmG4Try2XVF9VvdaNrni3gLA/9sMQa0IKSZgfdsRvVUED1HjHDmF1LkHKHtC2R4UEe6izBAlHuJ96AT7EbCH/E81kesnSBJnqxD4tnIT7RW8UANlKfGDztU7/fN+evKqkfPZSPksEhjxAk4ZtpdorhPdNN6OiozVAdEzh1u+OIy8iZIFuqtbgF15D3rCOxQj9oqk1RATtoH5UV00uZO2gqUaaaXNSwrpF3KM7hh461ninYZmYO7jv6KYZXBTNJV/kwKuDArKY96JeYFoQ3Jt97yoZjETb1Af+4gZa0fwtG8Zj4iD6OFeJlla7fzFRqDcMEzqhGRz1SZTkIrCmmBRK7rmv67rdFUnu2tVXuBaoh2iZoE7uiLBaoJSkOeCXay2BhiS8SkVOONa3GdhbgjVywNJw0QchVcHVa30u1cjM1+oNreZktJ+rBaqjGA5urcj/ggIgQMjpcMxwcoWc92Pi2j9z0qDN8DPwt0a7AeLjEteNzIXo+vve7NGmC8rdffvhqBizJ8fp8wB1p0c6SXClvchzrehP8E47b/0h5ffYMVEUvM9oBFe1qq/T9fkrkC4+B/oWft";
    public static final int REQUEST_CODE_SCAN_RESULTS = 1;

    private Camera camera;
    private BarcodeTracking barcodeTracking;
    private DataCaptureContext dataCaptureContext;

    private final HashSet<ScanResult> scanResults = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matrix_scan);

        setTitle(R.string.app_title);

        // Initialize and start the barcode recognition.
        initialize();

        Button doneButton = findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronized (scanResults) {
                    // Show new screen displaying a list of all barcodes that have been scanned.
                    Intent intent = ResultsActivity.getIntent(
                            MatrixScanActivity.this, scanResults);
                    startActivityForResult(intent, REQUEST_CODE_SCAN_RESULTS);
                }
            }
        });
    }

    private void initialize() {
        // Create data capture context using your license key.
        dataCaptureContext = DataCaptureContext.forLicenseKey(SCANDIT_LICENSE_KEY);

        // Use the recommended camera settings for the BarcodeTracking mode.
        CameraSettings cameraSettings = BarcodeTracking.createRecommendedCameraSettings();
        // Adjust camera settings - set Full HD resolution.
        cameraSettings.setPreferredResolution(VideoResolution.FULL_HD);
        // Use the default camera and set it as the frame source of the context.
        // The camera is off by default and must be turned on to start streaming frames to the data
        // capture context for recognition.
        // See resumeFrameSource and pauseFrameSource below.
        camera = Camera.getDefaultCamera(cameraSettings);
        if (camera != null) {
            dataCaptureContext.setFrameSource(camera);
        } else {
            throw new IllegalStateException(
                    "Sample depends on a camera, which failed to initialize.");
        }

        // The barcode tracking process is configured through barcode tracking settings
        // which are then applied to the barcode tracking instance that manages barcode tracking.
        BarcodeTrackingSettings barcodeTrackingSettings = new BarcodeTrackingSettings();

        // The settings instance initially has all types of barcodes (symbologies) disabled.
        // For the purpose of this sample we enable a very generous set of symbologies.
        // In your own app ensure that you only enable the symbologies that your app requires
        // as every additional enabled symbology has an impact on processing times.
        HashSet<Symbology> symbologies = new HashSet<>();
        symbologies.add(Symbology.EAN13_UPCA);
        symbologies.add(Symbology.EAN8);
        symbologies.add(Symbology.UPCE);
        symbologies.add(Symbology.CODE39);
        symbologies.add(Symbology.CODE128);

        barcodeTrackingSettings.enableSymbologies(symbologies);

        // Create barcode tracking and attach to context.
        barcodeTracking = BarcodeTracking.forDataCaptureContext(dataCaptureContext, barcodeTrackingSettings);

        // Register self as a listener to get informed of tracked barcodes.
        barcodeTracking.addListener(this);

        // To visualize the on-going barcode tracking process on screen, setup a data capture view
        // that renders the camera preview. The view must be connected to the data capture context.
        DataCaptureView dataCaptureView = DataCaptureView.newInstance(this, dataCaptureContext);

        // Add a barcode tracking overlay to the data capture view to render the tracked barcodes on
        // top of the video preview. This is optional, but recommended for better visual feedback.
        BarcodeTrackingBasicOverlay overlay =
                BarcodeTrackingBasicOverlay.newInstance(barcodeTracking, dataCaptureView);

        // Configure how barcodes are highlighted - apply default brush or create your own.
        // final Brush defaultBrush = new Brush(Color.BLUE, Color.RED, 5f);
        final Brush defaultBrush = overlay.getBrush();
        overlay.setBrush(defaultBrush);

        // Modify brush dynamically.
        // Note that modifying a barcode's brush color requires the MatrixScan AR add-on.
        int rejectedFillColor = getResources().getColor(R.color.barcode_rejected);
        int rejectedBorderColor = getResources().getColor(R.color.barcode_rejected_border);
        final Brush rejectedBrush = new Brush(rejectedFillColor, rejectedBorderColor, 1f);
        overlay.setListener(new BarcodeTrackingBasicOverlayListener() {
            @Override
            @NonNull
            public Brush brushForTrackedBarcode(
                    @NonNull BarcodeTrackingBasicOverlay overlay,
                    @NonNull TrackedBarcode trackedBarcode
            ) {
                if (isValidBarcode(trackedBarcode.getBarcode())) {
                    return defaultBrush;
                } else {
                    return rejectedBrush;
                }
            }

            @Override
            public void onTrackedBarcodeTapped(
                    @NonNull BarcodeTrackingBasicOverlay overlay,
                    @NonNull TrackedBarcode trackedBarcode
            ) {
                // Handle barcode click if necessary.
            }
        });

        // Add the DataCaptureView to the container.
        FrameLayout container = findViewById(R.id.data_capture_view_container);
        container.addView(dataCaptureView);
    }

    @Override
    protected void onPause() {
        pauseFrameSource();
        super.onPause();
    }

    private void pauseFrameSource() {
        // Switch camera off to stop streaming frames.
        // The camera is stopped asynchronously and will take some time to completely turn off.
        // Until it is completely stopped, it is still possible to receive further results, hence
        // it's a good idea to first disable barcode tracking as well.
        barcodeTracking.setEnabled(false);
        camera.switchToDesiredState(FrameSourceState.OFF, null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check for camera permission and request it, if it hasn't yet been granted.
        // Once we have the permission the onCameraPermissionGranted() method will be called.
        requestCameraPermission();
    }

    @Override
    public void onCameraPermissionGranted() {
        resumeFrameSource();
    }

    private void resumeFrameSource() {
        // Switch camera on to start streaming frames.
        // The camera is started asynchronously and will take some time to completely turn on.
        barcodeTracking.setEnabled(true);
        camera.switchToDesiredState(FrameSourceState.ON, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SCAN_RESULTS
                && resultCode == ResultsActivity.RESULT_CODE_CLEAN) {
            synchronized (scanResults) {
                scanResults.clear();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onObservationStarted(@NonNull BarcodeTracking barcodeTracking) {
        // Nothing to do.
    }

    @Override
    public void onObservationStopped(@NonNull BarcodeTracking barcodeTracking) {
        // Nothing to do.
    }

    // This function is called whenever objects are updated and it's the right place to react to
    // the tracking results.
    @Override
    public void onSessionUpdated(
            @NonNull BarcodeTracking mode,
            @NonNull BarcodeTrackingSession session,
            @NonNull FrameData data
    ) {
        synchronized (scanResults) {
            for (TrackedBarcode trackedBarcode : session.getAddedTrackedBarcodes()) {
                if (isValidBarcode(trackedBarcode.getBarcode())) {
                    scanResults.add(new ScanResult(trackedBarcode.getBarcode()));
                }
            }
        }
    }

    // Method with custom logic for accepting/rejecting recognized barcodes.
    private boolean isValidBarcode(Barcode barcode) {
        // Reject invalid barcodes.
        if (barcode.getData() == null || barcode.getData().isEmpty()) return false;

        // Reject barcodes based on your logic.
        if (barcode.getData().startsWith("7")) return false;

        return true;
    }

    @Override
    protected void onDestroy() {
        dataCaptureContext.removeMode(barcodeTracking);
        super.onDestroy();
    }
}
