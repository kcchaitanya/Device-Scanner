package com.example.android.devicescanner;

import android.app.Activity;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import static com.example.android.devicescanner.Constants.INTENT_DEVICE_ID;

public class MainActivity extends AppCompatActivity {
    Button btnScan;
    Button editDeviceDetails;
    Button assignDevice;
    public final static int QRcodeWidth = 350 ;
    String newDeviceId;
    private FirebaseAuth mAuth;


    TextView tv_qr_readTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Activity activity = this;

        btnScan = (Button)findViewById(R.id.btnScan);
        tv_qr_readTxt = (TextView) findViewById(R.id.tv_qr_readTxt);
        mAuth = FirebaseAuth.getInstance();
        newDeviceId = getIntent().getStringExtra(INTENT_DEVICE_ID);




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

        initialiseView();

    }

    private void initialiseView() {

        editDeviceDetails = findViewById(R.id.edit_device_details);
        editDeviceDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToAddDeviceActivity();
            }
        });

        assignDevice = findViewById(R.id.button_assign_device);
        assignDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToAssignDeviceActivity();
            }
        });
    }

    private void moveToAssignDeviceActivity() {

        Intent moveToAssignDeviceActivity = new Intent(this, AssignDeviceActivity.class);
        moveToAssignDeviceActivity.putExtra(Constants.INTENT_DEVICE_ID, newDeviceId);
        startActivity(moveToAssignDeviceActivity);
    }

    private void moveToAddDeviceActivity() {

        Intent  moveToAddDeviceActivity = new Intent(this, AddDeviceActivity.class);
        startActivity(moveToAddDeviceActivity);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.e("Scan*******", "Cancelled scan");

            } else {
                Log.e("Scan", "Scanned");

                newDeviceId = result.getContents();
                tv_qr_readTxt.setText(newDeviceId);

                Toast.makeText(this, "Scanned: " + newDeviceId, Toast.LENGTH_LONG).show();
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }





}