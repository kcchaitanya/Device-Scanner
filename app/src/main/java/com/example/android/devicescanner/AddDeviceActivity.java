package com.example.android.devicescanner;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import static com.example.android.devicescanner.Constants.INTENT_DEVICE_ID;
public class AddDeviceActivity extends AppCompatActivity {

    String manfName;
    String modelName;
    String versionName;
    String memorySize;


    EditText editMafTextView;
    EditText editModelTextView;
    EditText editVersionTextView;
    EditText editSizeTextView;
    Button nextButton;
    Button skipButton;
    private FirebaseAuth mAuth;
    String newDeviceId;
    String TAG = "Device_id";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        mAuth = FirebaseAuth.getInstance();
        initializeView();
    }

    private void initializeView() {

        editMafTextView = findViewById(R.id.text_manf_name);
        editModelTextView = findViewById(R.id.text_model_name);
        editVersionTextView = findViewById(R.id.text_version_name);
        editSizeTextView = findViewById(R.id.text_storage_size);
        nextButton = findViewById(R.id.button_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputDetails()){
                    initializeFireStone();
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



    }

    private boolean validateInputDetails(){
        manfName = editMafTextView.getText().toString();
        modelName = editModelTextView.getText().toString();
        versionName = editVersionTextView.getText().toString();
        memorySize = editSizeTextView.getText().toString();

        if (!isDeviceManfValid(manfName)){

            editMafTextView.setError("Please enter Manufacturer details.");

        }

        if(!isDeviceModelValid(modelName)){
            editModelTextView.setError("Please enter Model details.");
        }

        if (!isDeviceVersionValid(versionName)){
            editVersionTextView.setError("Please enter Version details.");
        }

        if (!isDeviceMemoryValid(memorySize)){
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
        db.collection("devices").add(device).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                newDeviceId =  documentReference.getId();
                Log.d(TAG, "DocumentSnapshot added with ID: " + newDeviceId);
                moveToScannerActivity();
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
        moveToScannerActivity.putExtra(Constants.INTENT_DEVICE_ID, newDeviceId);
        startActivity(moveToScannerActivity);
    }
}
