package com.hfad.travelx;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class notification_fragment extends Fragment {
    private Toolbar ntool;
    private RecyclerView nrv;
    private DatabaseReference nref;
    private String Currentuid;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View v = inflater.inflate(R.layout.fragment_notification_fragment, container, false);
       Currentuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
       nref = FirebaseDatabase.getInstance().getReference().child("Notifications").child(Currentuid);
        ntool = v.findViewById(R.id.ntool);
        ((MainActivity)getActivity()).setSupportActionBar(ntool);
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nrv = v.findViewById(R.id.nrv);
        LinearLayoutManager ll = new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        nrv.setLayoutManager(ll);
        ShowNotifications();
        return v;
    }

    private void ShowNotifications() {
        final FirebaseRecyclerAdapter r1 = new FirebaseRecyclerAdapter<nmodel,NotificationViewHolder>(new FirebaseRecyclerOptions.Builder().setQuery(nref, nmodel.class).build()) {
            public void onBindViewHolder(final NotificationViewHolder holder, int position, nmodel model) {
                final String uid = model.getSenderid();
                String type = model.getType();
                holder.notification.setText(model.getNotification());
                FirebaseDatabase.getInstance().getReference().child("Users").child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                       String  name = snapshot.child("name").getValue().toString();
                       String image = snapshot.child("image").getValue().toString();
                       holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (type.equals("friend_req")){
                                    Intent intent = new Intent(getContext(),ProfileActivity.class);
                                    intent.putExtra("visit_user_id",uid);
                                    startActivity(intent);
                                }else if(type.equals("message")){
                                    Intent intent = new Intent(getContext(), ChatActivity.class);
                                    intent.putExtra("chat_user_id", uid);
                                    intent.putExtra("chat_name", name);
                                    intent.putExtra("chat_image", image);
                                    startActivity(intent);
                                }
                            }
                        });
                        if (image.isEmpty()){
                            Picasso.get().load(R.drawable.profile).into(holder.n_profile);
                        }else{
                            Picasso.get().load(image).into(holder.n_profile);
                        }
                    }
                    @Override public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
                    }
            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new NotificationViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.notification, parent, false));
            }
        };
        this.nrv.setAdapter(r1);
        r1.startListening();
    }
    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView notification;
        CircleImageView n_profile;
        public NotificationViewHolder(View itemview) {
            super(itemview);
           notification = itemview.findViewById(R.id.n_tv);
           n_profile = itemview.findViewById(R.id.n_profile);
        }
    }
}