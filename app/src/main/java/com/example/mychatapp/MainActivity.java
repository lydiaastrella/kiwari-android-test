package com.example.mychatapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

        public static class MessageViewHolder extends RecyclerView.ViewHolder {
            TextView messageTextView;
            TextView messengerTextView;
            TextView timeTextView;
            CircleImageView messengerImageView;

            MessageViewHolder(View v) {
                super(v);
                messageTextView = itemView.findViewById(R.id.messageTextView);
                messengerTextView = itemView.findViewById(R.id.messengerTextView);
                timeTextView = itemView.findViewById(R.id.dateTextView);
                messengerImageView = itemView.findViewById(R.id.messengerImageView);
            }
        }

        private static final String TAG = "MainActivity";
        public static final String MESSAGES_CHILD = "messages";
        public static final String ANONYMOUS = "anonymous";
        private String mUsername;
        private String mPhotoUrl;

        private Button mSendButton;
        private RecyclerView mMessageRecyclerView;
        private LinearLayoutManager mLinearLayoutManager;
        private ProgressBar mProgressBar;
        private EditText mMessageEditText;

    // Firebase instance variables
        private FirebaseAuth mFirebaseAuth;
        private FirebaseUser mFirebaseUser;
        private DatabaseReference mFirebaseDatabaseReference;
        private FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder> mFirebaseAdapter;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            // Set default username is anonymous.
            mUsername = ANONYMOUS;

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            mFirebaseAuth = FirebaseAuth.getInstance();
            mFirebaseUser = mFirebaseAuth.getCurrentUser();
            if (mFirebaseUser == null) {
                // Not signed in, launch the Sign In activity
                startActivity(new Intent(this, SignInActivity.class));
                finish();
                return;
            } else {
                mUsername = mFirebaseUser.getDisplayName();
                if (mFirebaseUser.getPhotoUrl() != null) {
                    mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
                }

                if(mUsername.equals("Jarjit Singh")){
                    Objects.requireNonNull(getSupportActionBar()).setTitle("Ismail bin Mail");
                    Drawable dr = getResources().getDrawable(R.drawable.ismail);
                    Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
                    Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 85, 85, true));
                    getSupportActionBar().setLogo(d);
                }else{
                    Objects.requireNonNull(getSupportActionBar()).setTitle("Jarjit Singh");
                    Drawable dr = getResources().getDrawable(R.drawable.jarjit);
                    Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
                    Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 85, 85, true));
                    getSupportActionBar().setLogo(d);
                }
            }

            // Initialize ProgressBar and RecyclerView.
            mProgressBar = findViewById(R.id.progressBar);
            mMessageRecyclerView = findViewById(R.id.messageRecyclerView);
            mLinearLayoutManager = new LinearLayoutManager(this);
            mLinearLayoutManager.setStackFromEnd(true);
            mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

            // New child entries
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
            SnapshotParser<FriendlyMessage> parser = new SnapshotParser<FriendlyMessage>() {
                @NonNull
                @Override
                public FriendlyMessage parseSnapshot(DataSnapshot dataSnapshot) {
                    FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
                    if (friendlyMessage != null) {
                        friendlyMessage.setId(dataSnapshot.getKey());
                    }
                    assert friendlyMessage != null;
                    return friendlyMessage;
                }
            };

            DatabaseReference messagesRef = mFirebaseDatabaseReference.child(MESSAGES_CHILD);
            FirebaseRecyclerOptions<FriendlyMessage> options =
                    new FirebaseRecyclerOptions.Builder<FriendlyMessage>()
                            .setQuery(messagesRef, parser)
                            .build();
            mFirebaseAdapter = new FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>(options) {
                @NonNull
                @Override
                public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                    LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                    return new MessageViewHolder(inflater.inflate(R.layout.item_message, viewGroup, false));
                }

                @Override
                protected void onBindViewHolder(@NonNull final MessageViewHolder viewHolder,
                                                int position,
                                                @NonNull FriendlyMessage friendlyMessage) {
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                    if (friendlyMessage.getText() != null) {
                        viewHolder.messageTextView.setText(friendlyMessage.getText());
                        viewHolder.messageTextView.setVisibility(TextView.VISIBLE);
                    }

                    viewHolder.messengerTextView.setText(friendlyMessage.getName());
                    viewHolder.timeTextView.setText(friendlyMessage.getTime());
                    if (friendlyMessage.getPhotoUrl() == null) {
                        viewHolder.messengerImageView.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,
                                R.drawable.ic_account_circle_black_36dp));
                    } else {
                        Glide.with(MainActivity.this)
                                .load(friendlyMessage.getPhotoUrl())
                                .into(viewHolder.messengerImageView);
                    }

                }
            };

            mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                    int lastVisiblePosition =
                            mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                    // If the recycler view is initially being loaded or the
                    // user is at the bottom of the list, scroll to the bottom
                    // of the list to show the newly added message.
                    if (lastVisiblePosition == -1 ||
                            (positionStart >= (friendlyMessageCount - 1) &&
                                    lastVisiblePosition == (positionStart - 1))) {
                        mMessageRecyclerView.scrollToPosition(positionStart);
                    }
                }
            });

            mMessageRecyclerView.setAdapter(mFirebaseAdapter);

            mMessageEditText = findViewById(R.id.messageEditText);
            mMessageEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.toString().trim().length() > 0) {
                        mSendButton.setEnabled(true);
                    } else {
                        mSendButton.setEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });

            mSendButton = findViewById(R.id.sendButton);
            mSendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Send messages on click.
                    Date currentTime = Calendar.getInstance().getTime();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy (HH-mm-ss)");
                    String formattedTime = df.format(currentTime);
                    FriendlyMessage friendlyMessage = new
                            FriendlyMessage(mMessageEditText.getText().toString(),
                            mUsername,
                            formattedTime,
                            mPhotoUrl);
                    mFirebaseDatabaseReference.child(MESSAGES_CHILD)
                            .push().setValue(friendlyMessage);
                    mMessageEditText.setText("");
                }
            });
        }

        @Override
        public void onStart() {
            super.onStart();
            // Check if user is signed in.
            mFirebaseUser = mFirebaseAuth.getCurrentUser();
            if (mFirebaseUser == null) {
                // Not signed in, launch the Sign In activity
                startActivity(new Intent(this, SignInActivity.class));
                finish();
            } else {
                mUsername = mFirebaseUser.getDisplayName();
                if (mFirebaseUser.getPhotoUrl() != null) {
                    mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
                }

                if(mUsername.equals("Jarjit Singh")){
                    Objects.requireNonNull(getSupportActionBar()).setTitle("Ismail bin Mail");
                    Drawable dr = getResources().getDrawable(R.drawable.ismail);
                    Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
                    Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 85, 85, true));
                    getSupportActionBar().setLogo(d);
                }else{
                    Objects.requireNonNull(getSupportActionBar()).setTitle("Jarjit Singh");
                    Drawable dr = getResources().getDrawable(R.drawable.jarjit);
                    Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
                    Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 85, 85, true));
                    getSupportActionBar().setLogo(d);
                }
            }
        }

        @Override
        public void onPause() {
            mFirebaseAdapter.stopListening();
            super.onPause();
        }

        @Override
        public void onResume() {
            super.onResume();
            mFirebaseAdapter.startListening();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_main, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() == R.id.sign_out_menu) {
                mFirebaseAuth.signOut();
                mUsername = ANONYMOUS;
                startActivity(new Intent(this, SignInActivity.class));
                finish();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            // An unresolvable error has occurred and Google APIs (including Sign-In) will not
            // be available.
            Log.d(TAG, "onConnectionFailed:" + connectionResult);
            Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
        }
}
