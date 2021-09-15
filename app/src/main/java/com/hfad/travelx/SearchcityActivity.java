package com.hfad.travelx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class SearchcityActivity extends AppCompatActivity {
    private SearchView searchcity;
    private ListView citylist;
    private DatabaseReference cityref;
    ArrayList<String> myarraylist = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    private Toolbar toolbar;
    private ImageButton imgbutton;
    cities city;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchcity);
        imgbutton = (ImageButton)findViewById(R.id.back_button);
        city  = new cities();
        cityref = FirebaseDatabase.getInstance().getReference().child("DestinationCities");
        searchcity = (SearchView)findViewById(R.id.search_city);
        citylist = (ListView)findViewById(R.id.list_city);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myarraylist);
        cityref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    city.setCity(ds.getKey());
                    myarraylist.add(city.getCity());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        citylist.setAdapter(arrayAdapter);
        citylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
             Intent intent = new Intent(SearchcityActivity.this,SaharYaChetraActivity.class);
             String city  = citylist.getAdapter().getItem(i).toString();
             intent.putExtra("city",city);
             startActivity(intent);
            }
        });
        searchcity.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
               SearchcityActivity.this.arrayAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                SearchcityActivity.this.arrayAdapter.getFilter().filter(newText);
                return false;
            }
        });
        imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
    }
}