package com.example.drawguessgame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    RegisterFragment frag;
    Uri imageUri = null;
    private static final String TAG = "EmailPassword";
    final int RESULT_LOAD_IMG = 1;



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
//        System.out.println("OnStart: "+currentUser.toString());
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
                            launchChooseGameActivity();


                        }else{
                            Log.d(TAG,"signInWithEmail:failure",task.getException());
                            showToast("Login Unsuccessful", Toast.LENGTH_SHORT);
                        }
                    }
                });
    }

    //TODO: NEED to send Authentication information
    public void launchChooseGameActivity(){
        Intent intent = new Intent(this, ChooseGameActivity.class);
        startActivity(intent);
    }

    public void showToast(String str, int attr){
        Toast toast = Toast.makeText(MainActivity.this,str,attr);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    public void registerAccount(View v){
        frag = new RegisterFragment();
        frag.setContainerActivity(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.screen1_frame,frag,"REGISTER");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void signUpNewAccount(View view) {
        String email = ((EditText)findViewById(R.id.screen1_register_edit_text_id)).getText().toString();
        String password = ((EditText)findViewById(R.id.screen1_register_edit_text_pwd)).getText().toString();

        final String name = ((EditText)findViewById(R.id.screen1_register_edit_text_name)).getText().toString();

        if(email.trim().equals("") || password.trim().equals("") || name.trim().equals("")){
            showToast("Please fill out all information", Toast.LENGTH_SHORT);
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            currentUser = mAuth.getCurrentUser();
                            setProfile(name);
                            showToast("Sign up successful", Toast.LENGTH_SHORT);
                            exitFragment();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            showToast("Register failed.\nPassword should be at least 6 characters.", Toast.LENGTH_LONG);
                        }
                    }
                });
    }

    public void setProfile(String name){

        if(imageUri == null){
            imageUri = Uri.parse("android.resource://com.example.drawguessgame/drawable/cat.jpg");
        }

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(imageUri)
                .build();

        currentUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "User profile updated");
                        }
                    }
                });
    }

    /**
     * Launches Gallery to fetch Image
     */
    public void getImageFromGallery(View v){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, RESULT_LOAD_IMG);
    }

    /**
     * Executed when returning from Gallery Activity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(resultCode == RESULT_OK){
            try{
                imageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap image = BitmapFactory.decodeStream(imageStream);
                //TODO: RESIZE IMAGE
                ImageView imageView = (ImageView)findViewById(R.id.screen1_register_profile_img);
                imageView.setImageBitmap(image);
            } catch (FileNotFoundException e) {
                showToast("File Not Found",Toast.LENGTH_SHORT);
            }
        }else{
            showToast("Image not picked",Toast.LENGTH_SHORT);
        }
    }

    /**
     * Exits the register fragment if the sign up was successful
     */
    public void exitFragment(){
        imageUri = null;
        String id = ((EditText)findViewById(R.id.screen1_register_edit_text_id)).getText().toString();
        String pwd = ((EditText)findViewById(R.id.screen1_register_edit_text_pwd)).getText().toString();

        EditText id_placeholder = (EditText) findViewById(R.id.screen1_edit_text_id);
        EditText pwd_placeholder = (EditText) findViewById(R.id.screen1_edit_text_pwd);

        id_placeholder.setText(id);
        pwd_placeholder.setText(pwd);


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.remove(frag);
        transaction.commit();
    }
}