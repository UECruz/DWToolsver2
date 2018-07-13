package com.java1.fullsail.dwtools.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.java1.fullsail.dwtools.R;

import java.util.concurrent.TimeUnit;

public class VerifyActivity extends AppCompatActivity {

    private static final String TAG = "VerifyActivity --> ";

    //UI field
    String phone,otp;
    EditText ed_verify;
    Button btn_verify;
    LinearLayout llProgress;

    //check isValid or Not
    Boolean isValid = true;

    //verifying data field
    String mVerfityID;
    PhoneAuthProvider.ForceResendingToken token;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        Intent i=getIntent();
        phone=i.getStringExtra("phone");

        init_phoneAuth();
        startPhoneNumberVerification(phone);
        initViews();
        setupClicks();
    }

    public void initViews(){
        ed_verify=findViewById(R.id.code);
        btn_verify=findViewById(R.id.verify);
        llProgress=findViewById(R.id.llProgress_verify);
    }

    public void setupClicks(){

        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otp=ed_verify.getText().toString();
                CheckIfIsEmptyOrNot();
                if(isValid){
                    verifyPhoneNumberWithCode(mVerfityID,otp);
                }else {
                    Toast.makeText(getApplicationContext(), "Please enter OTP.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void startPhoneNumberVerification(String phones) {
        Log.e(TAG,"phoneNumber -- "+phones);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phones,
                120,
                TimeUnit.SECONDS,
                VerifyActivity.this,
                callbacks
        );
    }

    public void init_phoneAuth(){
        callbacks =new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.e(TAG, "onVerificationCompleted:" + phoneAuthCredential);
                //success
                Intent intent=new Intent(VerifyActivity.this,LoginActivtiy.class);
                startActivity(intent);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.e(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(getApplicationContext(),"Invalid Phone number", Toast.LENGTH_LONG).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                Log.e(TAG,"onCodeSent:"+s);
                mVerfityID = s;
                token = forceResendingToken;
            }
        };
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        llProgress.setVisibility(View.VISIBLE);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential creditial) {
        auth.signInWithCredential(creditial)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            llProgress.setVisibility(View.GONE);
                            Log.e(TAG, "signInWithCredential:success");
                            auth.signOut();
                            //success
                            Intent intent=new Intent(VerifyActivity.this,LoginActivtiy.class);
                            startActivity(intent);
                        } else {
                            Log.e(TAG, "signInWithCredential:failure", task.getException());

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getApplicationContext(), "Invalid Code", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    public void CheckIfIsEmptyOrNot() {
        otp=ed_verify.getText().toString().trim();

        isValid = !TextUtils.isEmpty(otp) ;
    }
}
