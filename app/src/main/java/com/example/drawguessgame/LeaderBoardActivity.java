package com.example.drawguessgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class LeaderBoardActivity extends AppCompatActivity {

    String firebase_url = "https://drawguessgame-19fe2.firebaseio.com/UserInfo.json";
    List<UserProfile> profiles;
    Activity itself;

    String currentPhotoPath = null;
    Uri currentPhotoUri = null;
    Uri currentViewGroupUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);
        itself = this;

        new FetchDatabaseTask().execute();
    }

    /**
     * This function calls the contacts list fragment
     *  which shows lists of contact and lets the user select
     *  which person to share with
     * @param v
     */
    public void onClickShare(View v){
        saveImageGroupToUri(); //Save the leader board before you call fragment

        ContactsFragment frag = new ContactsFragment();
        frag.setContainerActivity(this);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();

        transaction.add(R.id.leader_board_fragment,frag);

        transaction.addToBackStack(null);
        transaction.commit();
    }


    // AsyncTask that performs fetching the news articles from the keyword enetered
    private class FetchDatabaseTask extends AsyncTask<Object, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Object[] objects){
            String json="";
            String line;

            try{
                System.out.println(firebase_url);
                URL url = new URL(firebase_url);

                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                while((line = in.readLine()) != null){
                    System.out.println("JSON LINE"+line);
                    json += line;
                }
                in.close();

                JSONObject jsonObject = new JSONObject(json);

                return jsonObject;

            }catch(Exception e){
                System.out.println("Error on URL");
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject json){
            profiles = new ArrayList<>();
//            profiles.add(new UserProfile("User Name", "Score"));

            int index = 0;
            String name;
            int score;
            try {
                Iterator<String> iterator = json.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    JSONObject curr = json.getJSONObject(key);
                    name = curr.getString("name");
                    score = curr.getInt("score");
                    profiles.add(new UserProfile(name, Integer.toString(score)));
                }

            } catch (JSONException e) {
                System.out.println("CANNOT FETCH THE INFORMATION FROM JSON FILE");
            }

            LeaderBoardAdapter newsAdapter = new LeaderBoardAdapter(itself, R.layout.leader_board_row, profiles);
            ListView boardListView = findViewById(R.id.leader_board_list_view);
            boardListView.setAdapter(newsAdapter);
        }
    }



    public void shareImage(View view){
        System.out.println("ActionSend");
        String email = getEmailAddress(view);

        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("vnd.android.cursor.dir/email");
        intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { email });
        intent.putExtra(Intent.EXTRA_STREAM,currentViewGroupUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/png");
        startActivity(intent);
    }

    public String getEmailAddress(View view){
        ListView listView = (ListView) view.getParent();
        int contactId = listView.getPositionForView(view)+1;

        System.out.println("CONTACT_ID: "+contactId);

        Cursor emails = getContentResolver().query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID
                        + " = " + contactId, null, null);

        String email = "";
        if(emails.moveToNext()) {
            email = emails
                    .getString(emails.getColumnIndex(
                            ContactsContract.CommonDataKinds.Email.ADDRESS));
            System.out.println(email);
        }
        emails.close();
        return email;
    }

    // This function creates empty file directory to save image
    private File createImageFile() throws Exception{
        String timeStamp = new SimpleDateFormat(
                "yyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_"+timeStamp+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,".jpg",storageDir);
        currentPhotoPath = image.getAbsolutePath();

        return image;
    }
    public void saveImageGroupToUri(){
        ViewGroup constraintLayout = findViewById(R.id.leader_board);
        //creates bitmap to draw on
        Bitmap bitmap = Bitmap.createBitmap(
                constraintLayout.getWidth(), constraintLayout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        //This draws the view inside the bitmap
        constraintLayout.draw(canvas);

        try{
            File imageFile = createImageFile();
            FileOutputStream out = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,out);

            //Saves the uri for the view group image file
            currentViewGroupUri = FileProvider.getUriForFile(
                    this,"com.example.DrawGuessGame.FileProvider",
                    imageFile);
            System.out.println("IMAGE_URI: "+currentViewGroupUri);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


}
