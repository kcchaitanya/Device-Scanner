package com.example.android.devicescanner;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import static com.example.android.devicescanner.Constants.INTENT_DEVICE_ID;
;
public class AssignDeviceActivity extends AppCompatActivity {

    EditText editAssigneeName;
    String assigneeName;
    Button submitButton;
    private FirebaseAuth mAuth;
    String newDeviceId;
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    Date date = new Date();
    String assigned_date = formatter.format(date);
    String TAG = "User_Derails";
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_device);
        mAuth = FirebaseAuth.getInstance();
        initializeViews();

    }

    private void initializeViews(){

        editAssigneeName = findViewById(R.id.edit_assignee_name);
        submitButton = findViewById(R.id.button_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateAssigneeName()){
                    initializeFirestore();

                }
            }
        });

    }

    private boolean validateAssigneeName() {
        assigneeName = editAssigneeName.getText().toString();

        if(!isAssignedNameValid()){
            editAssigneeName.setError("Please add the assignee details.");
        }
        return isAssignedNameValid();
    }

    private boolean isAssignedNameValid() {
        return assigneeName.length() > 0;

    }


    private void initializeFirestore() {

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final Map<String, Object> user = new HashMap<>();
        newDeviceId = getIntent().getStringExtra(INTENT_DEVICE_ID);
        user.put("assigned_to", assigneeName);
        user.put("assigned_date", assigned_date);
        db.collection("devices").document(newDeviceId).collection("user").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                userId =  documentReference.getId();
                Log.d(TAG, "DocumentSnapshot added with ID: " + userId);
            }

        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }

}
