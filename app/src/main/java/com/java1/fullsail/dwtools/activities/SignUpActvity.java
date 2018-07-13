package com.java1.fullsail.dwtools.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.java1.fullsail.dwtools.R;

public class SignUpActvity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity --> ";

    EditText email, password, userName, phone;
    Button submit;

    LinearLayout lProgress;

    ProgressDialog dialog;
    ProgressBar codeBar, phoneBar;

    LinearLayout phoneLay, codeLay;

    FirebaseAuth auth;
    FirebaseFirestore fireStoreDB;

    Boolean isValid;
    String user, eMail, passWord, phones;

    FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_screen);

        auth = FirebaseAuth.getInstance();
        fireStoreDB = FirebaseFirestore.getInstance();

        initViews();
        setUpClicks();
    }

    public void initViews() {
        userName = findViewById(R.id.userName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        phoneLay = findViewById(R.id.phoneLay);
        phone = findViewById(R.id.phoneNum);
        phoneBar = findViewById(R.id.progress1);

        codeLay = findViewById(R.id.codeLay);
        codeBar = findViewById(R.id.progress2);

        submit = findViewById(R.id.submit);
        dialog = new ProgressDialog(SignUpActvity.this);
        lProgress=findViewById(R.id.llProgress_Sign);
    }

    public void setUpClicks() {

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckIfIsEmptyOrNot();
                if (isValid) {
                    UserRegistration();
                } else {
                    Toast.makeText(SignUpActvity.this, "Please fill all form fields.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    if (isValid) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(userName.getText().toString()).build();
                        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.e(TAG, "Display name --"+FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                                }
                            }
                        });
                    }
                }
            }
        };
        auth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(mAuthListener);
    }

    public void UserRegistration() {
        user = userName.getText().toString();
        eMail = email.getText().toString();
        passWord = password.getText().toString();
        phones = phone.getText().toString();

        lProgress.setVisibility(View.VISIBLE);

        //dialog.setMessage("Please Wait, We are Registering Your Data on Server");
        //dialog.show();

        auth.createUserWithEmailAndPassword(eMail, passWord).addOnCompleteListener(SignUpActvity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                lProgress.setVisibility(View.GONE);
                //dialog.dismiss();

                if (task.isSuccessful()) {
                    // If user registered successfully then show this toast message.
                    Toast.makeText(SignUpActvity.this, "User Registration Successfully", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(SignUpActvity.this, VerifyActivity.class);
                    intent.putExtra("phone", phones);
                    startActivity(intent);
                    clearData();

                } else {
                    // If something goes wrong.
                    Toast.makeText(SignUpActvity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    clearData();
                }

            }
        });
    }


    public void CheckIfIsEmptyOrNot() {
        user = userName.getText().toString().trim();
        phones = phone.getText().toString();
        eMail = email.getText().toString().trim();
        passWord = password.getText().toString().trim();

        isValid = !TextUtils.isEmpty(eMail) && !TextUtils.isEmpty(passWord) &&
                !TextUtils.isEmpty(phones) && !TextUtils.isEmpty(user);
    }

    public void clearData(){
        userName.setText("");
        email.setText("");
        password.setText("");
        phone.setText("");
    }
}
