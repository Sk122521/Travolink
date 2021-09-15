package com.hfad.travelx;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class CommentActivity extends AppCompatActivity {
    private String Current_user_id;
    private String Postkey,type;
    private RecyclerView comment_list;
    private ImageButton comment_send_button;
    private Toolbar commenttoolbar;
    private EditText input_comment_text;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference postref;
    private ProgressBar progressBar;
    private DatabaseReference userref;

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Postkey = getIntent().getExtras().get("post_id").toString();
        type = getIntent().getExtras().get("from").toString();
        userref = FirebaseDatabase.getInstance().getReference().child("Users");
        if (type.equals("video")){
            postref = FirebaseDatabase.getInstance().getReference().child("VideoPost").child(this.Postkey).child("Comments");
        }else{
            postref = FirebaseDatabase.getInstance().getReference().child("Posts").child(this.Postkey).child("Comments");
        }
        Current_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        input_comment_text = (EditText) findViewById(R.id.comment_text);
        comment_send_button = (ImageButton) findViewById(R.id.comment_button);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.comments_list);
        comment_list = recyclerView;
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        this.linearLayoutManager = linearLayoutManager2;
        linearLayoutManager2.setReverseLayout(true);
        this.linearLayoutManager.setStackFromEnd(true);
        this.comment_list.setLayoutManager(this.linearLayoutManager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.comments_toolbar);
        this.commenttoolbar = toolbar;
        toolbar.setTitle("Comments");
        this.commenttoolbar.setNavigationIcon(R.drawable.back);
        this.commenttoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            /* class com.hfad.travelx.CommentActivity.AnonymousClass1 */

            public void onClick(View v) {
                CommentActivity.this.startActivity(new Intent(CommentActivity.this.getApplicationContext(), MainActivity.class));
            }
        });
        this.comment_send_button.setOnClickListener(new View.OnClickListener() {
            /* class com.hfad.travelx.CommentActivity.AnonymousClass2 */

            public void onClick(View view) {
                CommentActivity.this.userref.child(CommentActivity.this.Current_user_id).addValueEventListener(new ValueEventListener() {
                    /* class com.hfad.travelx.CommentActivity.AnonymousClass2.AnonymousClass1 */

                    @Override // com.google.firebase.database.ValueEventListener
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        CommentActivity.this.ValidateComment(dataSnapshot.child("name").getValue().toString().trim(), dataSnapshot.child("image").getValue().toString().trim());
                    }

                    @Override // com.google.firebase.database.ValueEventListener
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });
    }

    /* access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onStart() {
        super.onStart();
        displayComments();
    }

    private void displayComments() {
        FirebaseRecyclerOptions<comment> options = new FirebaseRecyclerOptions.Builder().setQuery(this.postref, comment.class).build();
        final FirebaseRecyclerAdapter<comment, commentviewholder> adapter = new FirebaseRecyclerAdapter<comment, commentviewholder>(options) {
            /* class com.hfad.travelx.CommentActivity.AnonymousClass3 */

            /* access modifiers changed from: protected */
            public void onBindViewHolder(commentviewholder holder, int position, final comment model) {
                CommentActivity.this.progressBar.setVisibility(View.INVISIBLE);
                TextView textView = holder.name_comment;
                textView.setText(model.getName_of_commentee().substring(0, 1).toUpperCase() + model.getName_of_commentee().substring(1).toLowerCase());
                holder.comment_comment.setText(model.getComment());
                holder.time_comment.setText(model.getTime());
                holder.date_comment.setText(model.getDate());
                holder.setprofileimage(model.getProfileimage_of_commentee());
                holder.name_comment.setOnClickListener(new View.OnClickListener() {
                    /* class com.hfad.travelx.CommentActivity.AnonymousClass3.AnonymousClass1 */

                    public void onClick(View view) {
                        Intent intent = new Intent(CommentActivity.this, ProfileActivity.class);
                        intent.putExtra("visit_user_id", model.getUid());
                        CommentActivity.this.startActivity(intent);
                    }
                });
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public commentviewholder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new commentviewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cooment_layout, parent, false));
            }
        };
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            /* class com.hfad.travelx.CommentActivity.AnonymousClass4 */

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = adapter.getItemCount();
                int lastVisiblePosition = CommentActivity.this.linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 || (positionStart >= friendlyMessageCount - 1 && lastVisiblePosition == positionStart - 1)) {
                    CommentActivity.this.comment_list.scrollToPosition(positionStart);
                }
            }
        });
        this.comment_list.setAdapter(adapter);
        adapter.updateOptions(options);
        adapter.startListening();
        this.comment_list.getLayoutManager().smoothScrollToPosition(this.comment_list, new RecyclerView.State(), this.comment_list.getAdapter().getItemCount());
    }

    public static class commentviewholder extends RecyclerView.ViewHolder {
        TextView comment_comment;
        TextView date_comment;
        View mview;
        TextView name_comment;
        CircleImageView profileimage_comment;
        TextView time_comment;

        public commentviewholder(View itemView) {
            super(itemView);
            this.mview = itemView;
            this.name_comment = (TextView) itemView.findViewById(R.id.username_comment);
            this.comment_comment = (TextView) itemView.findViewById(R.id.comment_comment);
            this.date_comment = (TextView) itemView.findViewById(R.id.date_comment);
            this.time_comment = (TextView) itemView.findViewById(R.id.time_comment);
            this.profileimage_comment = (CircleImageView) itemView.findViewById(R.id.profileimage_comment);
        }

        public void setprofileimage(String image) {
            if (TextUtils.isEmpty(image)) {
                this.profileimage_comment.setImageResource(R.drawable.profile);
            } else {
                Picasso.get().load(image).into(this.profileimage_comment);
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void ValidateComment(String name, String image) {
        String commenttext = this.input_comment_text.getText().toString();
        if (TextUtils.isEmpty(commenttext)) {
            Toast.makeText(this, "Write something..", 0).show();
            return;
        }
        SimpleDateFormat dates = new SimpleDateFormat("d MMM yyyy");
        SimpleDateFormat times = new SimpleDateFormat("HH:mm:ss");
        String date = dates.format(Calendar.getInstance().getTime());
        String time = times.format(Calendar.getInstance().getTime());
        String time2 = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
        HashMap commentmap = new HashMap();
        commentmap.put("uid", this.Current_user_id);
        commentmap.put("name_of_commentee", name);
        commentmap.put("profileimage_of_commentee", image);
        commentmap.put("comment", commenttext);
        commentmap.put("date", date);
        commentmap.put("time", time2);
        this.postref.child(this.Current_user_id + "," + date + "," + time).updateChildren(commentmap).addOnCompleteListener(new OnCompleteListener() {
            /* class com.hfad.travelx.CommentActivity.AnonymousClass5 */

            @Override // com.google.android.gms.tasks.OnCompleteListener
            public void onComplete(Task task) {
                if (task.isSuccessful()) {
                    Toast.makeText(CommentActivity.this, "Your Comments added on posts...", Toast.LENGTH_LONG).show();
                    CommentActivity.this.input_comment_text.setText("");
                    return;
                }
                String message = task.getException().getMessage();
                CommentActivity commentActivity = CommentActivity.this;
                Toast.makeText(commentActivity, "ERROR : " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
