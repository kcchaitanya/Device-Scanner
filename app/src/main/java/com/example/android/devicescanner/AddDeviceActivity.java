package com.example.android.devicescanner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static com.example.android.devicescanner.Constants.INTENT_DEVICE_ID;

public class AddDeviceActivity extends AppCompatActivity {

    String manfName;
    String modelName;
    String versionName;
    String memorySize;
    String imeiNumber;
    String serialNumber;
    String mmId;
    String platformName;
    String macAddress;
    ProgressDialog progress;
    EditText editMafTextView;
    EditText editModelTextView;
    EditText editVersionTextView;
    EditText editSizeTextView;
    EditText editImeiNumberTextView;
    EditText editSerialNumberTextView;
    EditText editMmIdTextView;
    EditText editPlatformNameTextView;
    EditText editMacAddressTextView;
    Button nextButton;
    Button skipButton;
    String deviceId;
    String TAG = "Device_id";
    boolean isInEditMode;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        mAuth = FirebaseAuth.getInstance();
        final Activity activity = this;
        activity.setTitle("Device Details");
        initializeView();
        initializeProgressDialogue();
        deviceId = getIntent().getStringExtra(INTENT_DEVICE_ID);
        isInEditMode = (deviceId != null);
        if (isInEditMode) {
            fetchDeviceDetails(deviceId);
            progress.show();
        }

    }

    private void fetchDeviceDetails(@NonNull String deviceId) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("devices").document(deviceId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                manfName = (String) documentSnapshot.get("device_manf");
                modelName = (String) documentSnapshot.get("device_model");
                versionName = (String) documentSnapshot.get("device_version");
                memorySize = (String) documentSnapshot.get("device_memory");
                imeiNumber = (String) documentSnapshot.get("imei_number");
                serialNumber = (String) documentSnapshot.get("serial_number");
                mmId = (String) documentSnapshot.get("mm_id");
                platformName = (String) documentSnapshot.get("platform_name");
                macAddress = (String) documentSnapshot.get("mac_address");
                editMafTextView.setText(manfName);
                editModelTextView.setText(modelName);
                editVersionTextView.setText(versionName);
                editSizeTextView.setText(memorySize);
                editImeiNumberTextView.setText(imeiNumber);
                editSerialNumberTextView.setText(serialNumber);
                editMmIdTextView.setText(mmId);
                editPlatformNameTextView.setText(platformName);
                editMacAddressTextView.setText(macAddress);
                progress.dismiss();

            }
        });

    }

    private void initializeView() {

        editMafTextView = findViewById(R.id.text_manf_name);
        editModelTextView = findViewById(R.id.text_model_name);
        editVersionTextView = findViewById(R.id.text_version_name);
        editSizeTextView = findViewById(R.id.text_storage_size);
        editImeiNumberTextView = findViewById(R.id.text_imei_number);
        editSerialNumberTextView = findViewById(R.id.text_serial_number);
        editMmIdTextView = findViewById(R.id.text_mm_id);
        editPlatformNameTextView = findViewById(R.id.text_platform);
        editMacAddressTextView = findViewById(R.id.text_mac_address);
        nextButton = findViewById(R.id.button_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputDetails()) {
                    if (!isInEditMode) {
                        progress.show();
                        initializeFireStone();
                    } else {
                        updateFirestoreDetails();
                    }


                }

            }
        });

        skipButton = findViewById(R.id.button_skip);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToScannerActivity();

            }
        });
        getDeviceDetails();
        editVersionTextView.setText("" + Build.VERSION.RELEASE);
        editMafTextView.setText("" + Build.MANUFACTURER);
        editSerialNumberTextView.setText("" + Build.SERIAL);
        editModelTextView.setText("" + Build.MODEL);
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        imeiNumber = telephonyManager.getDeviceId();
        editImeiNumberTextView.setText(imeiNumber);


    }


    private void getDeviceDetails() {

        String details = "VERSION.RELEASE : " + Build.VERSION.RELEASE
                + "\nVERSION.INCREMENTAL : " + Build.VERSION.INCREMENTAL
                + "\nVERSION.SDK.NUMBER : " + Build.VERSION.SDK_INT
                + "\nBOARD : " + Build.BOARD
                + "\nBOOTLOADER : " + Build.BOOTLOADER
                + "\nBRAND : " + Build.BRAND
                + "\nCPU_ABI : " + Build.CPU_ABI
                + "\nCPU_ABI2 : " + Build.CPU_ABI2
                + "\nDISPLAY : " + Build.DISPLAY
                + "\nFINGERPRINT : " + Build.FINGERPRINT
                + "\nHARDWARE : " + Build.HARDWARE
                + "\nHOST : " + Build.HOST
                + "\nID : " + Build.ID
                + "\nMANUFACTURER : " + Build.MANUFACTURER
                + "\nMODEL : " + Build.MODEL
                + "\nPRODUCT : " + Build.PRODUCT
                + "\nSERIAL : " + Build.SERIAL
                + "\nTAGS : " + Build.TAGS
                + "\nTIME : " + Build.TIME
                + "\nTYPE : " + Build.TYPE
                + "\nUNKNOWN : " + Build.UNKNOWN
                + "\nUSER : " + Build.USER;

        Log.d("Device Details", details);
    }

    private void updateFirestoreDetails() {

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference updatedDocumentReference = db.collection("devices").document(deviceId);
        updatedDocumentReference.update("device_manf", manfName);
        updatedDocumentReference.update("device_model", modelName);
        updatedDocumentReference.update("device_version", versionName);
        updatedDocumentReference.update("device_memory", memorySize);
        updatedDocumentReference.update("imei_number", imeiNumber);
        updatedDocumentReference.update("serial_number", serialNumber);
        updatedDocumentReference.update("mm_id", mmId);
        updatedDocumentReference.update("platform_name", platformName);
        updatedDocumentReference.update("mac_address", macAddress).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        moveToScannerActivity();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });


    }


    private boolean validateInputDetails() {
        manfName = editMafTextView.getText().toString();
        modelName = editModelTextView.getText().toString();
        versionName = editVersionTextView.getText().toString();
        memorySize = editSizeTextView.getText().toString();
        imeiNumber = editImeiNumberTextView.getText().toString();
        serialNumber = editSerialNumberTextView.getText().toString();
        mmId = editMmIdTextView.getText().toString();
        platformName = editPlatformNameTextView.getText().toString();
        macAddress = editMacAddressTextView.getText().toString();


        if (!isDeviceManfValid(manfName)) {

            editMafTextView.setError("Please enter Manufacturer details.");

        }

        if (!isDeviceModelValid(modelName)) {
            editModelTextView.setError("Please enter Model details.");
        }

        if (!isDeviceVersionValid(versionName)) {
            editVersionTextView.setError("Please enter Version details.");
        }

        if (!isDeviceMemoryValid(memorySize)) {
            editSizeTextView.setError("Please enter Memory details.");
        }

        return isDeviceManfValid(manfName) && isDeviceModelValid(modelName) && isDeviceVersionValid(versionName) && isDeviceMemoryValid(memorySize);


    }

    private boolean isDeviceManfValid(String manfName) {

        return manfName.length() > 0;
    }

    private boolean isDeviceModelValid(String modelName) {

        return modelName.length() > 0;
    }

    private boolean isDeviceVersionValid(String versionName) {

        return versionName.length() > 0;
    }

    private boolean isDeviceMemoryValid(String memorySize) {

        return memorySize.length() > 0;
    }

    private void initializeFireStone() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final Map<String, Object> device = new HashMap<>();
        device.put("device_manf", manfName);
        device.put("device_model", modelName);
        device.put("device_version", versionName);
        device.put("device_memory", memorySize);
        device.put("imei_number", imeiNumber);
        device.put("serial_number", serialNumber);
        device.put("mm_id", mmId);
        device.put("platform_name", platformName);
        device.put("mac_address", macAddress);
        db.collection("devices").add(device).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                deviceId = documentReference.getId();
                Log.d(TAG, "DocumentSnapshot added with ID: " + deviceId);
                moveToScannerActivity();
                progress.dismiss();
            }

        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });


    }

    private void moveToScannerActivity() {
        Intent moveToScannerActivity = new Intent(this, MainActivity.class);
        moveToScannerActivity.putExtra(Constants.INTENT_DEVICE_ID, deviceId);
        startActivity(moveToScannerActivity);
    }

    public void initializeProgressDialogue() {
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(true); // disable dismiss by tapping outside of the dialog
    }
}
