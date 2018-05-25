package com.example.android.devicescanner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.w3c.dom.Text;

import static com.example.android.devicescanner.Constants.INTENT_DEVICE_ID;
import static com.example.android.devicescanner.Constants.INTENT_USER_ID;
import static com.example.android.devicescanner.Constants.INTENT_USER_NAME;

public class MainActivity extends AppCompatActivity {
    Button btnScan;
    Button editDeviceDetails;
    String assignedTo;
    TextView editassignedTo;
    Button assignDevice;
    public final static int QRcodeWidth = 350;
    String newDeviceId;
    private FirebaseAuth mAuth;
    ProgressDialog progress;
    Button viewHistoryDetails;

    TextView tv_qr_readTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Activity activity = this;
        activity.setTitle("Scan Device");
        btnScan = (Button) findViewById(R.id.btnScan);
        tv_qr_readTxt = (TextView) findViewById(R.id.tv_qr_readTxt);
        mAuth = FirebaseAuth.getInstance();
        newDeviceId = getIntent().getStringExtra(INTENT_DEVICE_ID);
        assignedTo = getIntent().getStringExtra(INTENT_USER_NAME);
        initialiseView();


        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();


            }
        });

        initializeProgressDialogue();
    }

    private void initialiseView() {
        editassignedTo = findViewById(R.id.text_assigned_to);
        editDeviceDetails = findViewById(R.id.edit_device_details);


        editDeviceDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.show();
                moveToAddDeviceActivity();
            }
        });

        assignDevice = findViewById(R.id.button_assign_device);
        tv_qr_readTxt.setText(newDeviceId);
        viewHistoryDetails = findViewById(R.id.view_history_details);
        viewHistoryDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Development is n progress", Toast.LENGTH_SHORT).show();
            }
        });
        if (assignedTo != null) {

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            final DocumentReference docRef = db.collection("devices").document(newDeviceId);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    assignedTo = (String) documentSnapshot.get("assigned_to");
                    editassignedTo.setText(assignedTo);
                }
            });
        } else

        {
            editassignedTo.setText("None");
        }


        assignDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.show();
                moveToAssignDeviceActivity();

            }
        });
    }


    private void moveToAssignDeviceActivity() {

        Intent moveToAssignDeviceActivity = new Intent(this, AssignDeviceActivity.class);
        moveToAssignDeviceActivity.putExtra(Constants.INTENT_DEVICE_ID, newDeviceId);
        startActivity(moveToAssignDeviceActivity);
        progress.dismiss();
    }

    private void moveToAddDeviceActivity() {

        Intent moveToAddDeviceActivity = new Intent(this, AddDeviceActivity.class);
        moveToAddDeviceActivity.putExtra(Constants.INTENT_DEVICE_ID, newDeviceId);
        startActivity(moveToAddDeviceActivity);
        progress.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Log.e("Scan*******", "Cancelled scan");

            } else {
                Log.e("Scan", "Scanned");
                progress.show();
                newDeviceId = result.getContents();
                tv_qr_readTxt.setText(newDeviceId);
                progress.dismiss();
                if (assignedTo == null) {

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    final DocumentReference docRef = db.collection("devices").document(newDeviceId);
                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            assignedTo = (String) documentSnapshot.get("assigned_to");
                            editassignedTo.setText(assignedTo);
                            progress.dismiss();
                        }
                    });
                } else

                {
                    editassignedTo.setText(assignedTo);
                }


                Toast.makeText(this, "Scanned: " + newDeviceId, Toast.LENGTH_LONG).show();
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void initializeProgressDialogue() {
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(true); // disable dismiss by tapping outside of the dialog
    }


}