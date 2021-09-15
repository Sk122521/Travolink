package com.hfad.travelx;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.test.InstrumentationRegistry.getContext;

public class MessageActivity extends AppCompatActivity {
    private Toolbar mtoolbar;
    private String Currentuserid;
    private RecyclerView chatlistview;
    private DatabaseReference chatref;
    private ProgressBar progressBar;
    private DatabaseReference userref;

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.message_toolbar);
        this.mtoolbar = toolbar;
        setSupportActionBar(toolbar);
        this.mtoolbar.setTitle("Messages");
        getSupportActionBar().setTitle("Messages");
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            /* class com.hfad.travelx.like_and_seen.AnonymousClass1 */

            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
        this.Currentuserid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.chatref = FirebaseDatabase.getInstance().getReference().child("Chats").child(this.Currentuserid);
        this.userref = FirebaseDatabase.getInstance().getReference().child("Users");
        this.progressBar = (ProgressBar) findViewById(R.id.chatBar);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.all_user_chat_list);
        this.chatlistview = recyclerView;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, RecyclerView.VERTICAL, false);
        this.chatlistview.setLayoutManager(gridLayoutManager);
        Displaychatlist(gridLayoutManager);
    }
    private void Displaychatlist(final GridLayoutManager gridLayoutManager) {
        final FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<likes, ChatViewHolder>(new FirebaseRecyclerOptions.Builder().setQuery(this.chatref, likes.class).build()) {
            /* class com.hfad.travelx.MessageFragment.AnonymousClass1 */

            /* access modifiers changed from: protected */
            public void onBindViewHolder(final ChatViewHolder holder, int position, likes model) {
                progressBar.setVisibility(View.INVISIBLE);
          /*      if (Build.VERSION.SDK_INT >= 23) {
                    TypedArray typedArray = getContext().obtainStyledAttributes(new int[]{R.attr.selectableItemBackground});
                    int selectableItemBackground = typedArray.getResourceId(0, 0);
                    typedArray.recycle();
                    holder.mview.setForeground(getContext().getDrawable(selectableItemBackground));
                    holder.mview.setClickable(true);
                }*/
                final String uid = model.getUid();
                userref.child(uid).addValueEventListener(new ValueEventListener() {
                    /* class com.hfad.travelx.MessageFragment.AnonymousClass1.AnonymousClass1 */

                    @Override // com.google.firebase.database.ValueEventListener
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String name = dataSnapshot.child("name").getValue().toString().trim();
                        final String image = dataSnapshot.child("image").getValue().toString().trim();
                        ChatViewHolder chatViewHolder = holder;
                        chatViewHolder.setChat_name(name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase());
                        holder.setChat_profile(dataSnapshot.child("image").getValue().toString().trim());
                        holder.mview.setOnClickListener(new View.OnClickListener() {
                            /* class com.hfad.travelx.MessageFragment.AnonymousClass1.AnonymousClass1.AnonymousClass1 */

                            public void onClick(View view) {
                                Intent chatintent = new Intent(MessageActivity.this, ChatActivity.class);
                                chatintent.putExtra("chat_user_id", uid);
                                chatintent.putExtra("chat_name", name);
                                chatintent.putExtra("chat_image", image);
                                startActivity(chatintent);
                            }
                        });
                        if (dataSnapshot.child("Chatstate").hasChild("onlinestatus")) {
                          //  dataSnapshot.child("Userstate").child("date").getValue().toString();
                            //dataSnapshot.child("Userstate").child("time").getValue().toString();
                            String state = dataSnapshot.child("Chatstate").child("onlinestatus").getValue().toString();
                            if (state.equals("Chaton")) {
                                holder.online_status.setVisibility(View.VISIBLE);
                            } else if (state.equals("Chatoff")) {
                                holder.online_status.setVisibility(View.INVISIBLE);
                            }
                        }
                    }

                    @Override // com.google.firebase.database.ValueEventListener
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ChatViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.individualchatui, parent, false));
            }
        };
        firebaseRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            /* class com.hfad.travelx.MessageFragment.AnonymousClass2 */

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = firebaseRecyclerAdapter.getItemCount();
                int lastVisiblePosition = gridLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 || (positionStart >= friendlyMessageCount - 1 && lastVisiblePosition == positionStart - 1)) {
                   chatlistview.scrollToPosition(positionStart);
                }
            }
        });
        this.chatlistview.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView chat_name;
        CircleImageView chat_profile;
        View mview;
        CircleImageView online_status;

        public ChatViewHolder(View itemview) {
            super(itemview);
            this.mview = itemview;
            this.online_status = (CircleImageView) itemview.findViewById(R.id.online_status);
            this.chat_profile = (CircleImageView) itemview.findViewById(R.id.chat_image);
            this.chat_name = (TextView) itemview.findViewById(R.id.chat_name);
        }

        public void setChat_profile(String profileimage) {
            if (TextUtils.isEmpty(profileimage)) {
                this.chat_profile.setImageResource(R.drawable.profile);
            } else {
                Picasso.get().load(profileimage).into(this.chat_profile);
            }
        }

        public void setChat_name(String name) {
            this.chat_name.setText(name);
        }
    }
}
