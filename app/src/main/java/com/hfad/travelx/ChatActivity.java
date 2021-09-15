package com.hfad.travelx;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.Constants;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private static final int TYPE_DOCUMENT_MY = 3333;
    private static final int TYPE_DOCUMENT_HIS = 6666;
    private static final int TYPE_IMAGE_MY = 1111;
    private static final int TYPE_IMAGE_HIS = 5555;
    private static final int TYPE_TEXT_MY = 2222;
    private static final int TYPE_TEXT_HIS = 4444;
    private String Currentuserid;
    private String MessagepushId;
    private Toolbar chatToolbar;
    private ImageButton chat_add_button;
    private EditText chatbox;
    private String checkr = "";
    private FirebaseRecyclerAdapter<message, RecyclerView.ViewHolder> firebaseRecyclerAdapter;
    private ProgressDialog loadbar;
    private LinearLayoutManager mLinearlayoutMannager;
    private RecyclerView mMessagesList;
    private String messagereceiverid;
    private String messagereceiverimage;
    private String messagereceivername;
    private ProgressBar progressBar;
    private MaterialButton send_message_button;
    private SharedPreferences sharedPreferences;
    private CircleImageView userimage;
    private TextView usersname;
    private DatabaseReference chatref;
    private TextView usersonlinestatus;

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initialiseFields();
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        loadbar = new ProgressDialog(this);
        Currentuserid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        messagereceiverid = getIntent().getExtras().get("chat_user_id").toString();
        messagereceivername = getIntent().getExtras().get("chat_name").toString();
        messagereceiverimage = getIntent().getExtras().get("chat_image").toString();
        mMessagesList = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mLinearlayoutMannager = linearLayoutManager;
        linearLayoutManager.setStackFromEnd(true);
        mMessagesList.setLayoutManager(mLinearlayoutMannager);
        DisplayLastSeen();
        TextView textView = usersname;
        textView.setText(messagereceivername.substring(0, 1).toUpperCase() +messagereceivername.substring(1).toLowerCase());
        if (messagereceiverimage.isEmpty()) {
            userimage.setImageResource(R.drawable.profile);
        } else {
            Picasso.get().load(messagereceiverimage).placeholder(R.drawable.profile).into(userimage);
        }
        send_message_button.setOnClickListener(new View.OnClickListener() {
            /* class com.hfad.travelx.ChatActivity.AnonymousClass1 */

            public void onClick(View view) {
                SendMessage();
                mMessagesList.smoothScrollToPosition(firebaseRecyclerAdapter.getItemCount());
                chatbox.setText("");
            }
        });
        Loadmessages();
        chat_add_button.setOnClickListener(new View.OnClickListener() {
            /* class com.hfad.travelx.ChatActivity.AnonymousClass2 */

            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("Select the file");
                builder.setItems(new CharSequence[]{"Images","Pdf files","Send your current location"}, new DialogInterface.OnClickListener() {
                    /* class com.hfad.travelx.ChatActivity.AnonymousClass2.AnonymousClass1 */

                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            checkr = "image";
                            Intent intent = new Intent("android.intent.action.GET_CONTENT");
                            intent.setType("image/*");
                            startActivityForResult(intent, 1);
                        }
                        if (i == 1) {
                            checkr = "pdf";
                            Intent intent2 = new Intent("android.intent.action.OPEN_DOCUMENT");
                            intent2.addCategory("android.intent.category.OPENABLE");
                            intent2.setType("application/pdf");
                            startActivityForResult(intent2, 2);
                        }
                        if (i == 3) {
                        }
                    }
                });
                builder.show();
            }
        });
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void SendMessage() {
        String chatmessage = chatbox.getText().toString();
        if (TextUtils.isEmpty(chatmessage)) {
            Toast.makeText(this, "write message", Toast.LENGTH_LONG).show();
            return;
        }
        Currentuserid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference push = FirebaseDatabase.getInstance().getReference().child("Messages").child(Currentuserid).child(messagereceiverid).push();
        chatref = push;
        String MessagepushId2 = push.getKey();
        String time = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
        HashMap hashMap = new HashMap();
        hashMap.put("message", chatmessage);
        hashMap.put("type","text");
        hashMap.put("from",Currentuserid);
        hashMap.put("time", time);
        hashMap.put("messageid", MessagepushId2);
        hashMap.put("to", this.messagereceiverid);
        hashMap.put("filename","k");
        Map messageBodydetails = new HashMap();
        messageBodydetails.put(("Messages/" + Currentuserid + "/" + messagereceiverid) + "/" + MessagepushId2, hashMap);
        messageBodydetails.put(("Messages/" + messagereceiverid + "/" + Currentuserid) + "/" + MessagepushId2, hashMap);
        FirebaseDatabase.getInstance().getReference().updateChildren(messageBodydetails).addOnCompleteListener(new OnCompleteListener() {
            /* class com.hfad.travelx.ChatActivity.AnonymousClass3 */

            @Override // com.google.android.gms.tasks.OnCompleteListener
            public void onComplete(Task task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ChatActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ChatActivity.this, "Error in sending message!", Toast.LENGTH_SHORT).show();
                }
                ChatActivity.this.chatbox.setText("");
            }
        });
    }

    /* access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String str = checkr;
        boolean z = true;
        if (str.equals("image")) {
            if (requestCode == 1 && resultCode == -1) {
                DatabaseReference push = FirebaseDatabase.getInstance().getReference().child("Messages").child(Currentuserid).child(messagereceiverid).push();
                chatref = push;
                MessagepushId = push.getKey();
                StorageReference imagefolder = FirebaseStorage.getInstance().getReference().child("chatimage").child(Currentuserid);
                final Uri Individualimage = data.getData();
                final StorageReference imagename = imagefolder.child(MessagepushId + Individualimage.getLastPathSegment());
                imagename.putFile(Individualimage).addOnSuccessListener((OnSuccessListener) new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    /* class com.hfad.travelx.ChatActivity.AnonymousClass5 */

                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            /* class com.hfad.travelx.ChatActivity.AnonymousClass5.AnonymousClass1 */

                            public void onSuccess(Uri uri) {
                                String url = String.valueOf(uri);
                                String time = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
                                HashMap hashMap = new HashMap();
                                hashMap.put("message", url);
                                hashMap.put("name", Individualimage.getLastPathSegment());
                                hashMap.put("type", checkr);
                                hashMap.put("from", Currentuserid);
                                hashMap.put("time", time);
                                hashMap.put("messageid", MessagepushId);
                                hashMap.put("to", messagereceiverid);
                                hashMap.put("filename","k");
                                Map messageBodydetails = new HashMap();
                                messageBodydetails.put(("Messages/" + Currentuserid + "/" +messagereceiverid) + "/" + MessagepushId, hashMap);
                                messageBodydetails.put(("Messages/" + messagereceiverid + "/" + Currentuserid) + "/" +MessagepushId, hashMap);
                                FirebaseDatabase.getInstance().getReference().updateChildren(messageBodydetails).addOnCompleteListener(new OnCompleteListener() {
                                    /* class com.hfad.travelx.ChatActivity.AnonymousClass5.AnonymousClass1.AnonymousClass1 */

                                    @Override // com.google.android.gms.tasks.OnCompleteListener
                                    public void onComplete(Task task) {
                                        if (task.isSuccessful()) {
                                            loadbar.dismiss();
                                        }
                                    }
                                });
                            }
                        });
                    }
                }).addOnProgressListener((OnProgressListener) new OnProgressListener<UploadTask.TaskSnapshot>() {
                    /* class com.hfad.travelx.ChatActivity.AnonymousClass4 */

                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double p = (((double) taskSnapshot.getBytesTransferred()) * 100.0d) / ((double) taskSnapshot.getTotalByteCount());
                        loadbar.setTitle("Sending Image");
                        ProgressDialog progressDialog = loadbar;
                        progressDialog.setMessage(((int) p) + " % sending....");
                       loadbar.setCanceledOnTouchOutside(false);
                       loadbar.show();
                    }
                });
            }
        } else if (!str.equals("image")) {
            boolean z2 = (requestCode == 2) & (resultCode == -1) & (data != null);
            if (data == null) {
                z = false;
            }
            if (z2 && z) {
                final Uri fileuri = data.getData();
                String filename = getfileName(fileuri);
                DatabaseReference push2 = FirebaseDatabase.getInstance().getReference().child("Messages").child(Currentuserid).child(messagereceiverid).push();
                chatref = push2;
                MessagepushId = push2.getKey();
                final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("chatfile").child(Currentuserid).child(MessagepushId + fileuri.getLastPathSegment());
                filepath.putFile(fileuri).addOnSuccessListener((OnSuccessListener) new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    /* class com.hfad.travelx.ChatActivity.AnonymousClass7 */

                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            /* class com.hfad.travelx.ChatActivity.AnonymousClass7.AnonymousClass1 */

                            public void onSuccess(Uri uri) {
                                String url = String.valueOf(uri);
                                String time = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
                                HashMap hashMap = new HashMap();
                                hashMap.put("message", url);
                                hashMap.put("name", fileuri.getLastPathSegment());
                                hashMap.put("type", checkr);
                                hashMap.put("from", Currentuserid);
                                hashMap.put("time", time);
                                hashMap.put("messageid",MessagepushId);
                                hashMap.put("to",messagereceiverid);
                                hashMap.put("filename",filename);
                                Map messageBodydetails = new HashMap();
                                messageBodydetails.put(("Messages/" +Currentuserid + "/" +messagereceiverid) + "/" + MessagepushId, hashMap);
                                messageBodydetails.put(("Messages/" + messagereceiverid + "/" + Currentuserid) + "/" +MessagepushId, hashMap);
                                FirebaseDatabase.getInstance().getReference().updateChildren(messageBodydetails).addOnCompleteListener(new OnCompleteListener() {
                                    /* class com.hfad.travelx.ChatActivity.AnonymousClass7.AnonymousClass1.AnonymousClass2 */

                                    @Override // com.google.android.gms.tasks.OnCompleteListener
                                    public void onComplete(Task task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ChatActivity.this, "file sent", Toast.LENGTH_SHORT).show();
                                            loadbar.dismiss();
                                            return;
                                        }
                                        Toast.makeText(ChatActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    /* class com.hfad.travelx.ChatActivity.AnonymousClass7.AnonymousClass1.AnonymousClass1 */

                                    @Override // com.google.android.gms.tasks.OnFailureListener
                                    public void onFailure(Exception e) {
                                        Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }).addOnProgressListener((OnProgressListener) new OnProgressListener<UploadTask.TaskSnapshot>() {
                    /* class com.hfad.travelx.ChatActivity.AnonymousClass6 */

                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double p = (((double) taskSnapshot.getBytesTransferred()) * 100.0d) / ((double) taskSnapshot.getTotalByteCount());
                        loadbar.setTitle("Sending File");
                        ProgressDialog progressDialog =loadbar;
                        progressDialog.setMessage(((int) p) + " % sending....");
                        loadbar.setCanceledOnTouchOutside(false);
                        loadbar.show();
                    }
                });
            }
        }
    }

    private void Loadmessages() {
       Currentuserid = FirebaseAuth.getInstance().getCurrentUser().getUid();
         FirebaseRecyclerAdapter r2 = new FirebaseRecyclerAdapter<message, RecyclerView.ViewHolder>(new FirebaseRecyclerOptions.Builder().setQuery(FirebaseDatabase.getInstance().getReference().child("Messages").child(this.Currentuserid).child(this.messagereceiverid), message.class).build()) {
            public int getItemViewType(int position) {
                String type = ((message) getItem(position)).getType();
                String from = ((message) getItem(position)).getFrom();
                if (from.equals(Currentuserid)) {
                    switch (type) {
                        case "pdf":
                            return TYPE_DOCUMENT_MY;
                        case "text":
                            return TYPE_TEXT_MY;
                        case "image":
                            return TYPE_IMAGE_MY;
                        default:
                            return -1;
                    }
                } else if (from.equals(messagereceiverid)) {
                    switch (type) {
                        case "pdf":
                            return TYPE_DOCUMENT_HIS;
                        case "text":
                            return TYPE_TEXT_HIS;
                        case "image":
                            return TYPE_IMAGE_HIS;
                        default:
                            return -1;
                    }
                }
                return  -1;
            }
            public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position, final message model) {
              progressBar.setVisibility(View.INVISIBLE);
                final String fromuserid = model.getFrom();
                String fromMessagetype = model.getType();
                final String idofmessage = getRef(position).getKey();
                if (fromMessagetype.equals("text")) {
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            Toast.makeText(ChatActivity.this, "Press long to delete.", Toast.LENGTH_SHORT).show();
                        }
                    });
                        ((TextTypeViewHolder) holder).textmessage.setText(model.getMessage());
                        ((TextTypeViewHolder) holder).textmessagetime.setText(model.getTime());
                }
                else if (fromMessagetype.equals("image")) {
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        /* class com.hfad.travelx.ChatActivity.AnonymousClass8.AnonymousClass2 */

                        public void onClick(View view) {
                            Toast.makeText(ChatActivity.this, "Press long to delete.", Toast.LENGTH_SHORT).show();
                        }
                    });
                        ((ImageTypeViewHolder) holder).photomessagetime.setText(model.getTime());
                        Picasso.get().load(model.getMessage()).into(((ImageTypeViewHolder) holder).photomessage);

                }
               else if (fromMessagetype.equals("pdf")) {
                        ((DocumentTypeViewHolder) holder).pdfmessage.setText(model.getFilename());
                        ((DocumentTypeViewHolder) holder).pdfmessagetime.setText(model.getTime());
                        ((DocumentTypeViewHolder) holder).pdfmessage.setOnClickListener(new View.OnClickListener() {
                            /* class com.hfad.travelx.ChatActivity.AnonymousClass8.AnonymousClass3 */

                            public void onClick(View view) {
                                holder.itemView.getContext().startActivity(new Intent("android.intent.action.VIEW", Uri.parse(model.getMessage())));
                            }
                        });
                }
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    /* class com.hfad.travelx.ChatActivity.AnonymousClass8.AnonymousClass5 */

                    public boolean onLongClick(View view) {
                        holder.itemView.setBackgroundColor(getResources().getColor(R.color.lightbue));
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(40);
                        if (fromuserid.equals(messagereceiverid)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this).setCancelable(false);
                            builder.setTitle("Delete Message");
                            builder.setItems(new CharSequence[]{"delete for me", "cancel"}, new DialogInterface.OnClickListener() {
                                /* class com.hfad.travelx.ChatActivity.AnonymousClass8.AnonymousClass5.AnonymousClass1 */

                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (i == 0) {
                                        FirebaseDatabase.getInstance().getReference().child("Messages").child(Currentuserid).child(messagereceiverid).child(idofmessage).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            /* class com.hfad.travelx.ChatActivity.AnonymousClass8.AnonymousClass5.AnonymousClass1.AnonymousClass1 */

                                            @Override
                                            // com.google.android.gms.tasks.OnCompleteListener
                                            public void onComplete(Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    holder.itemView.setBackgroundColor(0);
                                                    return;
                                                }
                                                String exception = task.getException().getMessage();
                                                ChatActivity chatActivity = ChatActivity.this;
                                                Toast.makeText(chatActivity, "ERROR : " + exception, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    if (i == 1) {
                                        dialogInterface.cancel();
                                        holder.itemView.setBackgroundColor(0);
                                    }
                                }
                            });
                            builder.show();
                            return true;
                        } else if (fromuserid.equals(Currentuserid)) {
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(ChatActivity.this);
                            builder2.setTitle("Delete Message");
                            builder2.setItems(new CharSequence[]{"delete for me", "delete  for both", "cancel"}, new DialogInterface.OnClickListener() {
                                /* class com.hfad.travelx.ChatActivity.AnonymousClass8.AnonymousClass5.AnonymousClass2 */

                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (i == 0) {
                                        FirebaseDatabase.getInstance().getReference().child("Messages").child(ChatActivity.this.Currentuserid).child(ChatActivity.this.messagereceiverid).child(idofmessage).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            /* class com.hfad.travelx.ChatActivity.AnonymousClass8.AnonymousClass5.AnonymousClass2.AnonymousClass1 */

                                            @Override
                                            // com.google.android.gms.tasks.OnCompleteListener
                                            public void onComplete(Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    holder.itemView.setBackgroundColor(0);
                                                    return;
                                                }
                                                String exception = task.getException().getMessage();
                                                ChatActivity chatActivity = ChatActivity.this;
                                                Toast.makeText(chatActivity, "ERROR : " + exception, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    if (i == 1) {
                                        FirebaseDatabase.getInstance().getReference().child("Messages").child(Currentuserid).child(messagereceiverid).child(idofmessage).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            /* class com.hfad.travelx.ChatActivity.AnonymousClass8.AnonymousClass5.AnonymousClass2.AnonymousClass2 */

                                            @Override
                                            // com.google.android.gms.tasks.OnCompleteListener
                                            public void onComplete(Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    holder.itemView.setBackgroundColor(0);
                                                    return;
                                                }
                                                String exception = task.getException().getMessage();
                                                ChatActivity chatActivity = ChatActivity.this;
                                                Toast.makeText(chatActivity, "ERROR : " + exception, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        FirebaseDatabase.getInstance().getReference().child("Messages").child(messagereceiverid).child(Currentuserid).child(idofmessage).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            /* class com.hfad.travelx.ChatActivity.AnonymousClass8.AnonymousClass5.AnonymousClass2.AnonymousClass3 */

                                            @Override
                                            // com.google.android.gms.tasks.OnCompleteListener
                                            public void onComplete(Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    holder.itemView.setBackgroundColor(0);
                                                    return;
                                                }
                                                String exception = task.getException().getMessage();
                                                ChatActivity chatActivity = ChatActivity.this;
                                                Toast.makeText(chatActivity, "ERROR : " + exception, Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                    if (i == 2) {
                                        dialogInterface.cancel();
                                        holder.itemView.setBackgroundColor(0);
                                    }
                                }
                            });
                            builder2.show();
                            builder2.setCancelable(false);
                            return true;
                        }
                        return true;
                    }
                });
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                switch (viewType){
                    case TYPE_IMAGE_MY:
                        return new ImageTypeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_format_2, parent, false));
                    case TYPE_IMAGE_HIS:
                        return new ImageTypeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_format_1, parent, false));
                    case TYPE_TEXT_MY:
                        return new TextTypeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_sent_format, parent, false));
                    case TYPE_TEXT_HIS:
                        return new TextTypeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_received_format, parent, false));
                    case TYPE_DOCUMENT_MY:
                        return new DocumentTypeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.pdf_message, parent, false));
                    case TYPE_DOCUMENT_HIS:
                        return new DocumentTypeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.pdf_message_his, parent, false));
                    default:
                        return  null;
                }
               // Log.d(ChatActivity.this.TAG, Integer.toString(viewType));
            }
        };
        this.firebaseRecyclerAdapter = r2;
        r2.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            /* class com.hfad.travelx.ChatActivity.AnonymousClass9 */

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount =firebaseRecyclerAdapter.getItemCount();
                int lastVisiblePosition =mLinearlayoutMannager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 || (positionStart >= friendlyMessageCount - 1 && lastVisiblePosition == positionStart - 1)) {
                    mMessagesList.smoothScrollToPosition(friendlyMessageCount);
                }
            }
        });
       mMessagesList.setAdapter(this.firebaseRecyclerAdapter);
     firebaseRecyclerAdapter.startListening();
       chatbox.setFilters(new InputFilter[]{new InputFilter.LengthFilter(sharedPreferences.getInt(CodelabPreferences.FRIENDLY_MSG_LENGTH,5000000))});
        chatbox.addTextChangedListener(new TextWatcher() {
            /* class com.hfad.travelx.ChatActivity.AnonymousClass10 */

            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    send_message_button.setEnabled(true);
                } else {
                   send_message_button.setEnabled(false);
                }
            }

            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public static class TextTypeViewHolder extends RecyclerView.ViewHolder {
        View mview;
        TextView textmessagetime;
        TextView textmessage;

        public TextTypeViewHolder(View itemview) {
            super(itemview);
            this.mview = itemview;
            this.textmessage = (TextView) itemview.findViewById(R.id.text_message);
            this.textmessagetime = (TextView) itemview.findViewById(R.id.text_message_time);
        }
    }

    public static class ImageTypeViewHolder extends RecyclerView.ViewHolder {
        PhotoView photomessage;
        View mview;
        TextView photomessagetime;

        public ImageTypeViewHolder(View itemview) {
            super(itemview);
            this.mview = itemview;
            this.photomessage = (PhotoView) itemview.findViewById(R.id.photo_message);
            this.photomessagetime = (TextView) itemview.findViewById(R.id.photo_message_time);
        }
    }

    public static class DocumentTypeViewHolder extends RecyclerView.ViewHolder {
        MaterialButton pdfmessage;
        View mview;
        TextView pdfmessagetime;

        public DocumentTypeViewHolder(View itemview) {
            super(itemview);
            this.mview = itemview;
            this.pdfmessage = (MaterialButton) itemview.findViewById(R.id.pdf_message);
            this.pdfmessagetime = (TextView) itemview.findViewById(R.id.pdf_message_time);
        }
    }

    private void initialiseFields() {

        chatToolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(chatToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        setSupportActionBar(chatToolbar);
        LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionbarview =layoutInflater.inflate(R.layout.custom_chat_bar,null);
        actionBar.setCustomView(actionbarview);
        usersname = (TextView) actionbarview.findViewById(R.id.custom_profile_name);
        usersonlinestatus = (TextView) actionbarview.findViewById(R.id.custom_online_status);
        userimage = (CircleImageView) actionbarview.findViewById(R.id.custom_profile_image);
        chatbox = (EditText) findViewById(R.id.edittext_chatbox);
        send_message_button = (MaterialButton) findViewById(R.id.chat_send_button);
        chat_add_button = (ImageButton) findViewById(R.id.chat_add_button);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    private void DisplayLastSeen() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(messagereceiverid).addValueEventListener(new ValueEventListener() {
            /* class com.hfad.travelx.ChatActivity.AnonymousClass11 */

            @Override // com.google.firebase.database.ValueEventListener
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Chatstate").child("onlinestatus").exists()) {
                 //   String date = dataSnapshot.child("Userstate").child("date").getValue().toString();
                 //   String time = dataSnapshot.child("Userstate").child("time").getValue().toString();
                    String state = dataSnapshot.child("Chatstate").child("onlinestatus").getValue().toString();
                    if (state.equals("Chaton")) {
                        usersonlinestatus.setText("Online");
                    } else if (state.equals("Chatoff")) {
                        TextView textView = usersonlinestatus;
                        if (dataSnapshot.child("Chatstate").hasChild(Currentuserid)){
                            String times= dataSnapshot.child("Chatstate").child(Currentuserid).child("time").getValue().toString();
                            usersonlinestatus.setText("Last reached here at "+times );
                        }else{
                            usersonlinestatus.setText("Didn't talked yet" );
                        }
                        //textView.setText("Last Seen: " + date + " " + time);
                    }
                }
            }

            @Override // com.google.firebase.database.ValueEventListener
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /* access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onStart() {
        super.onStart();
        String time = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
        UpdatechatStatus(time);
    }

    @Override // androidx.fragment.app.FragmentActivity
    public void onPause() {
        firebaseRecyclerAdapter.stopListening();
        super.onPause();
      String time = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
        UpdatechatStatus(time);
    }

    @Override // androidx.fragment.app.FragmentActivity
    public void onResume() {
        super.onResume();
        firebaseRecyclerAdapter.startListening();
        String time = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
        UpdatechatStatus(time);
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onDestroy() {
        super.onDestroy();
       String time = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
        UpdatechatStatus(time);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onStop() {
        super.onStop();
        String time = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
        UpdatechatStatus(time);
    }

    private void UpdatechatStatus(String time) {
        HashMap<String, Object> OnlineStateMap = new HashMap<>();
        OnlineStateMap.put("time",time);
        FirebaseDatabase.getInstance().getReference().child("Users").child(Currentuserid).child("Chatstate").child(messagereceiverid).updateChildren(OnlineStateMap);
    }
    public String getfileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")){
            Cursor cursor =  getContentResolver().query(uri,null,null,null,null);
            try {
                if (cursor != null && cursor.moveToFirst()){
                    result  = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }finally {
                cursor.close();
            }
        }
        if (result == null){
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1){
                result = result.substring(cut+1);
            }
        }
        return  result;
    }
}
