package com.example.sketcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.ContactsContract;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static DrawingFragment drawingFragment = null;
    int[] color_arr = {R.color.colorRed, R.color.colorGreen, R.color.colorBlue,
            R.color.colorPurple, R.color.aqua, R.color.pink, R.color.grey, R.color.orange,
            R.color.colorWhite};
    float[] size_arr = {15,30,60};
    CountDownTimer cTimer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.drawingFragment = new DrawingFragment();
        drawingFragment.setContainerActivity(this);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        transaction.add(R.id.draw_layout,drawingFragment);

        transaction.addToBackStack(null);
        transaction.commit();

        new CountDownTimer(60000, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                TextView tv = (TextView) findViewById(R.id.timerText);
                tv.setText("seconds remaining: " + millisUntilFinished/1000);
            }

            @Override
            public void onFinish() {

            }
        }.start();

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
     * This function calls the contacts list fragment
     *  which shows lists of contact and lets the user select
     *  which person to share with
     * @param v
     */
    public void onClickShare(View v){
        ContactsFragment frag = new ContactsFragment();
        frag.setContainerActivity(this);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();

        transaction.add(R.id.screen_layout, frag);

        transaction.addToBackStack(null);
        transaction.commit();
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
        //ss
        return ImageUri;
    }

    private File createImageFile() throws Exception{
        String timeStamp = new SimpleDateFormat(
                "yyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_"+timeStamp+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,".jpg",storageDir);

        return image;
    }


}
