package edu.cmu.smartphone.telemedicine;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.smartphone.telemedicine.DBLayout.Dao_Sqlite;
import edu.cmu.smartphone.telemedicine.DBLayout.Dao_parse;
import edu.cmu.smartphone.telemedicine.entities.Contact;
import edu.cmu.smartphone.telemedicine.ws.remote.Notification;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AlphabetIndexer;
import android.widget.EditText;
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
    
    private EditText searchContactEditText;
    
    public void goToAddContactView(View view) {
        //loadContact();
        
        Intent intent = new Intent(ContactActivity.this, AddActivity.class);
        startActivity(intent);
    }
    
    // show dialog to the user and let user to determine if he want to add the contact.
    private void addContactAlert(final String userID, String message) {
        new AlertDialog.Builder(ContactActivity.this)
                .setIcon(R.drawable.ic_launcher)
                .// the icon
                setTitle("Friend add request")
                .// title
                setMessage(message)
                .// info
                setPositiveButton("Yes", new DialogInterface.OnClickListener() {// ok
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                // add contact. add contact to local database and cloud.
                                addContactToLocalAndCloud(ContactActivity.this, userID);
                                
                                Notification noti = new Notification(ContactActivity.this);
                                noti.sendNotification(userID, Contact.getCurrentUserID() +
                                        " approved your friend request.", 1);
                            }
                        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {// cancel
                            @Override
                            public void onClick(DialogInterface arg1, int witch) {
                                Notification noti = new Notification(ContactActivity.this);
                                noti.sendNotification(userID, Contact.getCurrentUserID() +
                                        " rejected your friend request.", 1);
                            }
                        }).show();
    }
    
    private void addContactToLocalAndCloud(Context context, String userID) {
        // add a new friend.
        Contact contact = new Contact(userID, userID);
        contact.setSortKey(userID);
        
        // add the new friend to local database. 
        Dao_Sqlite dao = new Dao_Sqlite(context);
        dao.addContact(contact);
        
        // also add the new friend to the cloud service.
        Dao_parse daoparse = new Dao_parse(context);
        daoparse.addContactCloud(contact);
    }
    
    private void loadContact() {
        adapter = new ContactAdapter(this, R.layout.contact_item, contacts);
        
        //readContactFromPhone();
        
        // load data from www.parse.com and store it in the local database.
        readDataFromLocalDb();
        
        if (contacts.size() > 0) {
            setupContactsListView();
        }
    }
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contactview);
        
        searchContactEditText = (EditText) findViewById(R.id.contactSearchEditText);
        
        titleLayout = (LinearLayout) findViewById(R.id.title_layout);
        title = (TextView) findViewById(R.id.title);
        
        // hide the keyboard.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        // get the view which show the list of contacts.
        contactsListView = (ListView) findViewById(R.id.contactSearchListView);
        
        /**
         * added by yu zhang, when another user add this user, I can see a 
         * alert window to decide whether or not add him/her.
         */
        String messType = getIntent().getStringExtra("messType");
        String userID = getIntent().getStringExtra("username");
        String message = getIntent().getStringExtra("message");
        
        if (messType != null && messType.equals("addContactRequest")) {
            // add friend request.
            addContactAlert(userID, message);
        }
        
        // load data to the view.
        loadContact();
    }
    
    private void cleanLocalDB(String userID) {
        // this is just fixed for testing. display the contact list of yuzhang
        Dao_Sqlite dao = new Dao_Sqlite(ContactActivity.this, userID, null, 1);
        
        // drop the contact table.
        dao.cleanTable();
    }
    
    private void readDataFromLocalDb() {
        // the database is named by the userID.
        String userID = Contact.getCurrentUserID();
        
        // this is just fixed for testing. display the contact list of yuzhang
        Dao_Sqlite dao = new Dao_Sqlite(ContactActivity.this, userID, null, 1);
        
        // load the contact list of the specific user to the database.
        dao.loadDataFromCloud(userID);
        
        Cursor cursor = dao.addContactToArrayList(contacts);
        
        //startManagingCursor(cursor);
        indexer = new AlphabetIndexer(cursor, 1, alphabet);
        adapter.setIndexer(indexer);
        
        //cursor.close();
        //dao.close(); 
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
        
        
        contactsListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id) {
                // TODO Auto-generated method stub
                Contact contact = contacts.get(position);
                
                // show the profile of the contact.
                Intent intent = new Intent(ContactActivity.this, InfoActivity.class);
                intent.putExtra("username", contact.getUserID());
                intent.putExtra("fullname", contact.getName());
                intent.putExtra("email", contact.getEmail());
                ContactActivity.this.startActivity(intent);
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
