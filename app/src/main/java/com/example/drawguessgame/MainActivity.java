package com.example.drawguessgame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static final String TAG = "EmailPassword";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth =  FirebaseAuth.getInstance();
    }

    @Override
    public void onStart(){
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        //TODO: Launch screen 2
    }

    public void buttonLogin(View v){
        String email = ((EditText)findViewById(R.id.screen1_edit_text_id)).getText().toString();
        String password = ((EditText)findViewById(R.id.screen1_edit_text_pwd)).getText().toString();

        if(email.trim().equals("") || password.trim().equals("")){
            showToast("Please Enter User ID/PASSWORD",Toast.LENGTH_SHORT);
            return;
        }

        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG,"signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            showToast("Login Successful", Toast.LENGTH_SHORT);
                            //TODO: Launch screen 2
                        }else{
                            Log.d(TAG,"signInWithEmail:failure",task.getException());
                            showToast("Login Unsuccessful", Toast.LENGTH_SHORT);
                        }
                    }
                });
    }

    public void showToast(String str, int attr){
        Toast toast = Toast.makeText(MainActivity.this,str,attr);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    public void registerAccount(View v){
        RegisterFragment frag = new RegisterFragment();
        frag.setContainerActivity(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.screen1_frame,frag);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void signUpNewAccount(View v){

    }
}
