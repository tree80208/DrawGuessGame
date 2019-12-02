package com.example.sketcher;


import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {
    public Activity containerActivity;
    public List<String> contacts = new ArrayList<>();

    private ListView contactsListView;
    ArrayAdapter<String> contactsAdapter = null;

    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        System.out.println("Share Fragment created");
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        getContacts();
    }
    @Override
    public void onResume() {
        super.onResume();
        setUpContactsAdapter();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Share Fragment destroyed");

    }

    public void setContainerActivity(Activity containerActivity) {
        this.containerActivity = containerActivity;
    }
    public void getContacts() {
        int limit = 1000;
        Cursor cursor = containerActivity.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,null,
                null, null, null);
        while (cursor.moveToNext() && limit > 0) {
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String given = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String contact = given + " :: " + contactId;
            contacts.add(contact);
            System.out.println(contact);
            limit--;
        }
        cursor.close();
    }
    private void setUpContactsAdapter(){
        contactsListView = (ListView) containerActivity.findViewById(R.id.contacts_list);
        contactsAdapter = new
                ArrayAdapter<String>(containerActivity, R.layout.contacts_text_row,
                R.id.contacts_row_item,contacts);
        contactsListView.setAdapter(contactsAdapter);
    }

}
