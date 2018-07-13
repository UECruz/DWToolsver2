package com.java1.fullsail.dwtools.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.java1.fullsail.dwtools.R;

public class LoginActivtiy extends AppCompatActivity {
    EditText email, password;
    Button login;
    TextView signUp;
    ProgressDialog dialog;
    FirebaseAuth auth;
    String eMail;
    String passWord;
    Boolean isNone;
    LinearLayout llProgress;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.submit);
        signUp = (TextView) findViewById(R.id.sign);
        auth = FirebaseAuth.getInstance();
        llProgress=findViewById(R.id.llProgress_login);
        dialog = new ProgressDialog(LoginActivtiy.this);

        eMail = email.getText().toString();
        passWord = password.getText().toString();

        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivtiy.this, HomePage.class);
            startActivity(intent);
        }


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckIfIsEmptyOrNot();

                if (isNone) {
                    LoginIn();
                } else {
                    Toast.makeText(LoginActivtiy.this, "Please Fill All the Fields", Toast.LENGTH_LONG).show();
                }
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivtiy.this, SignUpActvity.class);
                startActivity(intent);
            }
        });
    }


    public void LoginIn() {

        /*dialog.setMessage("Please Wait");
        dialog.show();*/
        llProgress.setVisibility(View.VISIBLE);

        auth.signInWithEmailAndPassword(eMail, passWord).addOnCompleteListener(LoginActivtiy.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    // Hiding the progress dialog.
                    //dialog.dismiss();
                    llProgress.setVisibility(View.GONE);

                    // Closing the current Login Activity.
                    finish();

                    // Opening the UserProfileActivity.
                    Intent intent = new Intent(LoginActivtiy.this, HomePage.class);
                    startActivity(intent);
                }
            }
        });

    }

    public void CheckIfIsEmptyOrNot() {

        eMail = email.getText().toString().trim();
        passWord = password.getText().toString().trim();
        isNone = !TextUtils.isEmpty(eMail) && !TextUtils.isEmpty(passWord);
    }
}
