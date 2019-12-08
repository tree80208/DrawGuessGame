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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    RegisterFragment frag;
    String playername =null;

    private StorageReference mStorageRef;
    Uri imageUri = null;
    private static final String TAG = "EmailPassword";
    final int RESULT_LOAD_IMG = 1;
    SoundRunnable soundRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpFullScreenMode();
        setContentView(R.layout.activity_main);
        mStorageRef = FirebaseStorage.getInstance().getReference("pics");
        StorageReference riversRef = mStorageRef.child("images/rivers.jpg");

        mAuth =  FirebaseAuth.getInstance();
//        //FOR TESTING PURPOSE
//        Intent intent = new Intent(this,LeaderBoardActivity.class);
//        startActivity(intent);

    }

    private void setUpFullScreenMode(){
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    @Override
    public void onResume(){
        super.onResume();
        System.out.println("MainActivity: onResume");
//        playBackgroundMusic();
    }

    @Override
    public void onPause(){
        super.onPause();
        System.out.println("MainActivity: onPause");
//        stopBackgroundMusic();
    }

    public void stopBackgroundMusic(){
        soundRunnable.stopMusic();
    }

    public void playBackgroundMusic(){
        soundRunnable = new SoundRunnable(
                this, R.raw.scary_island,250);

        (new Thread(soundRunnable)).start();
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
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.translate_right_left, R.anim.end_right_left
                    ,R.anim.translate_left_right, R.anim.end_left_right)
                .add(R.id.screen1_frame,frag,"REGISTER")
                .addToBackStack(null)
                .commit();
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
                            createUserInDatabase(name);
                            exitFragment();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            showToast("Register failed.\nPassword should be at least 6 characters.", Toast.LENGTH_LONG);
                        }
                    }
                });
    }

    public void exitButton(View view){
//        frag.onDestroy();
        getSupportFragmentManager().popBackStack();

    }

    public void createUserInDatabase(String name){
        this.playername = name;
        if(imageUri == null){
            imageUri = Uri.parse("android.resource://com.example.drawguessgame/" + R.drawable.cat);
        }
        final StorageReference riversRef = mStorageRef.child("pics/"+currentUser.getEmail()+".png");
        riversRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content

                        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                System.out.println("NEWESTPRINT: "+uri);
                                updateChildren(uri);
                            }
                        });
                        System.out.println("SUCCESS");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });


    }
    public void updateChildren(Uri uri){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        String path = "UserInfo/"+playername;

        Map<String, Object> users = new HashMap<>();
        users.put(path, new User(playername, "0","",uri.toString()));

        databaseReference.updateChildren(users);
        System.out.println("DATABASE UPDATED");
    }
    public void setProfile(String name){


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