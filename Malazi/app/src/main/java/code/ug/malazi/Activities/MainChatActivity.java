package code.ug.malazi.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import code.ug.malazi.Adapters.ChatListAdapter;
import code.ug.malazi.Models.InstantMessage;
import code.ug.malazi.R;


public class MainChatActivity extends AppCompatActivity {

    // TODO: Add member variables here:
    private String mDisplayName;
    private ListView mChatListView;
    private EditText mInputText;
    private ImageButton mSendButton;
    private DatabaseReference mDatabaseReference;
    private ChatListAdapter mAdapter;
    //private String mChatName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);
        String msgId = getIntent().getStringExtra("MESSAGE_ID");
        String msgName = getIntent().getStringExtra("MESSAGE_NAME");
        String msg = getIntent().getStringExtra("MESSAGE");
        setTitle(msg);


        mDisplayName = msgName;

        mDatabaseReference = FirebaseDatabase.getInstance().getReference(msgId);

        // Link the Views in the layout to the Java code
        mInputText = findViewById(R.id.messageInput);
        mSendButton = findViewById(R.id.sendButton);
        mChatListView = findViewById(R.id.chat_list_view);

        // TODO: Send the message when the "enter" button is pressed
        mInputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String msgId = getIntent().getStringExtra("MESSAGE_ID");
                Log.d("Fire", "sent "+ mInputText.getText().toString());
                Log.d("FireBase ", msgId);
                if (!mInputText.getText().toString().equals("")) {
                    InstantMessage chat = new InstantMessage(mInputText.getText().toString(), mDisplayName);
                    mDatabaseReference.child(msgId).push().setValue(chat);
                    mInputText.setText("");
                }
                return false;
            }
        });

        // TODO: Add an OnClickListener to the sendButton to send a message
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msgId = getIntent().getStringExtra("MESSAGE_ID");
                Log.d("Fire", "sent "+ mInputText.getText().toString());
                Log.d("FireBase ", msgId);
                if (!mInputText.getText().toString().equals("")) {
                    InstantMessage chat = new InstantMessage(mInputText.getText().toString(), mDisplayName);
                    mDatabaseReference.child(msgId).push().setValue(chat);
                    mInputText.setText("");
                }
            }
        });

    }


    // TODO: Override the onStart() lifecycle method. Setup the adapter here.
    @Override
    public void onStart() {
        super.onStart();
        String msgId = getIntent().getStringExtra("MESSAGE_ID");
        mAdapter = new ChatListAdapter(this, mDatabaseReference,mDisplayName, msgId);
        mChatListView.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();

        // TODO: Remove the Firebase event listener on the adapter.
        mAdapter.cleanup();

    }

}
