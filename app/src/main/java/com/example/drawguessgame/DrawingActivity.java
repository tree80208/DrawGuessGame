package com.example.drawguessgame;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DrawingActivity extends AppCompatActivity {
    public static DrawingFragment drawingFragment = null;
    // Color available, default = red
    int[] color_arr = {R.color.colorRed, R.color.colorGreen, R.color.colorBlue,
            R.color.colorPurple, R.color.aqua, R.color.pink, R.color.grey, R.color.orange,
            R.color.colorWhite};
    float[] size_arr = {15,30,60};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        this.drawingFragment = new DrawingFragment();
        drawingFragment.setContainerActivity(this);
        updateFields();
        timer();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.draw_layout,drawingFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void timer(){
        new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                TextView tv = (TextView) findViewById(R.id.timerText);
                tv.setText(" Seconds Remaining: " + millisUntilFinished/1000);
            }

            @Override
            public void onFinish() {
            //TODO: DO something even timer is up.
//                finish();
            }
        }.start();


    }



    public void updateFields(){
        //TODO:fetch and update player names, images, scores, and word
        String username = getIntent().getStringExtra("p1");
        TextView p1 = (TextView)findViewById(R.id.playerOneName);
        p1.setText(username);



//        String word = getIntent().getStringExtra("wordsList");

        ArrayList<String> guessingwords = getIntent().getExtras().getStringArrayList("guessingWords");
        System.out.println("guessingWords: " + guessingwords);
        TextView guessWord = (TextView) findViewById(R.id.guessWord);
        guessWord.setText("Draw: " + guessingwords.get(0));
//        ArrayList<String> test = getIntent().getExtras().getStringArrayList("wordsList");

//        TextView wordDisplay = (TextView) findViewById(R.id.guessWord);
//        wordDisplay.setText(wordsList.get(0));
//        System.out.println("words list from previous intent" + wordsList);

    }



    public void clearDrawing(View v){
        DrawingView dv = drawingFragment.getDrawingView();
        dv.startNew();
    }

    public void changeBrushSize(View v){
        DrawingView dv = drawingFragment.getDrawingView();

        int viewID = v.getId();
        if(viewID == findViewById(R.id.button_small).getId()){
            dv.changePaintSize(size_arr[0]);
        }else if(viewID == findViewById(R.id.button_medium).getId()){
            dv.changePaintSize(size_arr[1]);
        }else if(viewID == findViewById(R.id.button_large).getId()){
            dv.changePaintSize(size_arr[2]);
        }

    }
    

    /**
     * This function changes the paint brush color
     * @param v
     */
    public void changePaintColor(View v){
        DrawingView dv = drawingFragment.getDrawingView();

        int viewID = v.getId();
        if(viewID == findViewById(R.id.button_red).getId()){
            dv.changePaintColor(color_arr[0]);
        }else if(viewID == findViewById(R.id.button_green).getId()){
            dv.changePaintColor(color_arr[1]);
        }else if(viewID == findViewById(R.id.button_blue).getId()){
            dv.changePaintColor(color_arr[2]);
        }else if(viewID == findViewById(R.id.button_purple).getId()){
            dv.changePaintColor(color_arr[3]);
        }else if(viewID == findViewById(R.id.button_aqua).getId()){
            dv.changePaintColor(color_arr[4]);
        }else if(viewID == findViewById(R.id.button_pink).getId()){
            dv.changePaintColor(color_arr[5]);
        }else if(viewID == findViewById(R.id.button_grey).getId()){
            dv.changePaintColor(color_arr[6]);
        }else if(viewID == findViewById(R.id.button_orange).getId()){
            dv.changePaintColor(color_arr[7]);
        }else if(viewID == findViewById(R.id.button_erase).getId()){
            dv.changePaintColor(color_arr[8]);
        }




    }

    /**
     * This function clicks the contact and launches mail app
     *  to share the bitmap image
     * @param v
     */
    public void clickContact(View v){
        ListView listView = (ListView) v.getParent();
        int contactId = listView.getPositionForView(v)+1;

        System.out.println("CONTACT_ID: "+contactId);

        Cursor emails = getContentResolver().query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID
                        + " = " + contactId, null, null);

        List<String> emailsList = new ArrayList<>();
        if(emails.moveToNext()) {
            String email = emails
                    .getString(emails.getColumnIndex(
                            ContactsContract.CommonDataKinds.Email.ADDRESS));
            System.out.println(email);
            if(!email.trim().equals("")){
                emailsList.add(email);
                launchMail(email);
            }
        }
        emails.close();

//        launchMail(emailsList);
    }

    /**
     * This function calls intent with email address
     * @param address
     */
    public void launchMail(String address){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("vnd.android.cursor.dir/email");

        intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { address });
        System.out.println(BuildConfig.APPLICATION_ID +".provider");

        Uri uri = saveImageToFile(drawingFragment.getDrawingView().getBitmap());
        intent.putExtra(android.content.Intent.EXTRA_STREAM, uri);

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    public Uri saveImageToFile(Bitmap bitmap){
        //creates bitmap to draw on
        Canvas canvas = new Canvas(bitmap);

        Uri ImageUri=null;
        try{
            File imageFile = createImageFile();
            FileOutputStream out = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,out);

            //Saves the uri for the view group image file
            ImageUri = FileProvider.getUriForFile(
                    this,"com.example.Sketcher.FileProvider",
                    imageFile);
        }catch(Exception e){
            e.printStackTrace();
        }
        return ImageUri;
    }

    // This function creates empty file directory to save image
    private File createImageFile() throws Exception{
        String timeStamp = new SimpleDateFormat(
                "yyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_"+timeStamp+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,".jpg",storageDir);

        return image;
    }

}
