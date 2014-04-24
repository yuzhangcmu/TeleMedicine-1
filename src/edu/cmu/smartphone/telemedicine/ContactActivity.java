package edu.cmu.smartphone.telemedicine;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.smartphone.telemedicine.DBLayout.Dao_Sqlite;
import edu.cmu.smartphone.telemedicine.entities.Contact;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AlphabetIndexer;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ContactActivity extends Activity{
    
    private LinearLayout titleLayout;

    private TextView title;

    private ListView contactsListView;

    private ContactAdapter adapter;

    private AlphabetIndexer indexer;

    private List<Contact> contacts = new ArrayList<Contact>();

    private String alphabet = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private int lastFirstVisibleItem = -1;
    
    public void goToAddContactView(View view) {
        Intent intent = new Intent(ContactActivity.this, AddActivity.class);
        startActivity(intent);
    }
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contactview);
        
        adapter = new ContactAdapter(this, R.layout.contact_item, contacts);
        titleLayout = (LinearLayout) findViewById(R.id.title_layout);
        title = (TextView) findViewById(R.id.title);
        
        // get the view which show the list of contacts.
        contactsListView = (ListView) findViewById(R.id.contactSearchListView);
        
        //readContactFromPhone();
        
        readDataFromLocalDb();
        
        if (contacts.size() > 0) {
            setupContactsListView();
        }
    }
    
    private void readDataFromLocalDb() {
        
        // this is just fixed for testing. display the contact list of yuzhang
        Dao_Sqlite dao = new Dao_Sqlite(ContactActivity.this, "yuzhang", null, 1);
        
        // load the contact list to the database.
        dao.loadDataFromCloud("yuzhang");
        
        Cursor cursor = dao.addContactToArrayList(contacts);
        
        //startManagingCursor(cursor);
        indexer = new AlphabetIndexer(cursor, 1, alphabet);
        adapter.setIndexer(indexer);
    }
    
    private void readContactFromPhone() {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = getContentResolver().query(uri,
                new String[] { "display_name", "sort_key" }, null, null, "sort_key");
        
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);
                String sortKey = getSortKey(cursor.getString(1));
                
                Contact contact = new Contact();
                contact.setName(name);
                contact.setSortKey(sortKey);
                
                contacts.add(contact);
            } while (cursor.moveToNext());
        }
        
        //startManagingCursor(cursor);
        indexer = new AlphabetIndexer(cursor, 1, alphabet);
        adapter.setIndexer(indexer);
    }
    
    private void setupContactsListView() {
        contactsListView.setAdapter(adapter);
        contactsListView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                    int totalItemCount) {
                int section = indexer.getSectionForPosition(firstVisibleItem);
                int nextSecPosition = indexer.getPositionForSection(section + 1);
                if (firstVisibleItem != lastFirstVisibleItem) {
                    MarginLayoutParams params = (MarginLayoutParams) titleLayout.getLayoutParams();
                    params.topMargin = 0;
                    titleLayout.setLayoutParams(params);
                    title.setText(String.valueOf(alphabet.charAt(section)));
                }
                
                if (nextSecPosition == firstVisibleItem + 1) {
                    android.view.View childView = view.getChildAt(0);
                    if (childView != null) {
                        int titleHeight = titleLayout.getHeight();
                        int bottom = childView.getBottom();
                        MarginLayoutParams params = (MarginLayoutParams) titleLayout
                                .getLayoutParams();
                        if (bottom < titleHeight) {
                            float pushedDistance = bottom - titleHeight;
                            params.topMargin = (int) pushedDistance;
                            titleLayout.setLayoutParams(params);
                        } else {
                            if (params.topMargin != 0) {
                                params.topMargin = 0;
                                titleLayout.setLayoutParams(params);
                            }
                        }
                    }
                }
                lastFirstVisibleItem = firstVisibleItem;
            }

            @Override
            public void onScrollStateChanged(android.widget.AbsListView view,
                    int scrollState) {
                // TODO Auto-generated method stub
                
            }

        });

    }
    
    private String getSortKey(String sortKeyString) {
        String key = sortKeyString.substring(0, 1).toUpperCase();
        if (key.matches("[A-Z]")) {
            return key;
        }
        return "#";
    }
    
}
