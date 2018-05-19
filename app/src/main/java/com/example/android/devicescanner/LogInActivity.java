package com.example.android.devicescanner;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends AppCompatActivity {


    String email;
    String password;
    EditText editEmailTextView;
    EditText editPasswordTextView;
    Button signInButton;
    private FirebaseAuth mAuth;
    String TAG = "SIGN_IN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        mAuth = FirebaseAuth.getInstance();


        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            System.out.println("currentUser" + currentUser.toString());
            moveToAddDeviceActivity();
            return;
        }
        initializeView();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

    }

    private void initializeView(){

        editEmailTextView = findViewById(R.id.text_email_id);
        editPasswordTextView = findViewById(R.id.text_password);
        signInButton = findViewById(R.id.button_sign_in);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if (validateInputFields()){
                   signInUser(email, password);
               }


            }
        });

    }

    private boolean validateInputFields() {

        email = editEmailTextView.getText().toString().trim();
        password = editPasswordTextView.getText().toString();

        if (!isEmailValid()){
            editEmailTextView.setError("Please Enter Valid Email");
        } else {
            editEmailTextView.setError(null);
        }



        if (!isPasswordValid(password)){
            editPasswordTextView.setError("Please Enter Password greater than 6 characters");
        }

        return isEmailValid() && isPasswordValid(password);


    }

    private boolean isPasswordValid(String password) {

        return password.length() > 6;

        }

    private boolean isEmailValid() {

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

    private void moveToAddDeviceActivity() {

        Intent moveToAddDeviceActivity = new Intent(this, AddDeviceActivity.class);
        startActivity(moveToAddDeviceActivity);
        finish();
    }

    private void signInUser(String email,String password ){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            moveToAddDeviceActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LogInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
}
