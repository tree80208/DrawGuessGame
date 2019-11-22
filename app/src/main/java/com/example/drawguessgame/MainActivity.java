package com.example.drawguessgame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    public EditText emailID, password;
    public Button login,signin;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth =  FirebaseAuth.getInstance();
        emailID = findViewById(R.id.screen1_edit_text_id);
        password = findViewById(R.id.screen1_edit_text_pwd);
        login = findViewById(R.id.screen1_button_login);
        signin = findViewById(R.id.screen1_button_sign_in);
    }

    public void mainButtonClicked(View v){
        String email = emailID.getText().toString();
        String pwd = password.getText().toString();
        if(email.isEmpty()){
            emailID.setError("Please enter email id");
            emailID.requestFocus();
        }else if(pwd.isEmpty()){
            password.setError("Please enter password");
            password.requestFocus();
        }else if(email.isEmpty() && pwd.isEmpty()){
            Toast.makeText(this,"Cannot sign in. Please enter email and password",Toast.LENGTH_LONG);
        }else if(!(email.isEmpty() && pwd.isEmpty())){
            if(v.getId() == signin.getId()){
                mFirebaseAuth.createUserWithEmailAndPassword(email,pwd)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(!task.isSuccessful()){
                                    Toast.makeText(MainActivity.this,"SignUp Unsuccessful, Please Try Again",Toast.LENGTH_LONG);
                                }else{
                                    Toast.makeText(MainActivity.this,"SignUp Successful",Toast.LENGTH_LONG);
                                }
                            }
                        });

            }else if(v.getId() == login.getId()){

                mFirebaseAuth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(MainActivity.this,"Login Unsuccessful",Toast.LENGTH_LONG);
                        }else{
                            Toast.makeText(MainActivity.this,"Login Successful",Toast.LENGTH_LONG);
                        }
                    }
                });

//                mAuthStateListener = new FirebaseAuth.AuthStateListener() {
//                    @Override
//                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
//                        if(mFirebaseUser != null){
//                            Toast.makeText(MainActivity.this,"Login Successful",Toast.LENGTH_LONG);
//                        }else{
//                            Toast.makeText(MainActivity.this,"Login Unsuccessful",Toast.LENGTH_LONG);
//                        }
//
//
//                    }
//                };
            }
        }else{
            Toast.makeText(MainActivity.this,"Error occurred",Toast.LENGTH_LONG);
        }
    }
}
