package com.hfad.travelx;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OnlineFragment extends Fragment {
    private DatabaseReference Chatref;
    private String Currentuserid;
    private RecyclerView chatlistRecyclerview;
    private DatabaseReference friendref;

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.friendref = FirebaseDatabase.getInstance().getReference().child("Friends");
        this.Chatref = FirebaseDatabase.getInstance().getReference().child("Chats");
        this.Currentuserid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return inflater.inflate(R.layout.fragment_online, container, false);
    }
}
