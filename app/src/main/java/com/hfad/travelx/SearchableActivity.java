package com.hfad.travelx;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

public class SearchableActivity extends AppCompatActivity {
    private String curFilter;
    private MaterialButton filterbutton;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<friendlist, searchviewholder> recycleradapter;
    private SearchView searchView;
    private DatabaseReference userref;

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        this.userref = FirebaseDatabase.getInstance().getReference().child("Users");
        this.filterbutton = (MaterialButton) findViewById(R.id.filter_button);
        this.recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        SearchView searchView2 = (SearchView) findViewById(R.id.search_view);
        this.searchView = searchView2;
        searchView2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SearchableActivity.this.searchView.setIconified(false);
            }
        });
        SearchedResults("name", searchView2.getQuery().toString());
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void SearchedResults(final String filtervalue, String queri) {
        this.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            /* class com.hfad.travelx.SearchableActivity.AnonymousClass2 */
            @Override // androidx.appcompat.widget.SearchView.OnQueryTextListener
            public boolean onQueryTextSubmit(String query) {
                Query startAt = SearchableActivity.this.userref.orderByChild(filtervalue).startAt(query.toLowerCase());
                FirebaseRecyclerOptions<friendlist> options = new FirebaseRecyclerOptions.Builder().setQuery(startAt.endAt(query.toLowerCase() + ""), friendlist.class).build();
                SearchableActivity.this.recycleradapter = new FirebaseRecyclerAdapter<friendlist, searchviewholder>(options) {
                    /* class com.hfad.travelx.SearchableActivity.AnonymousClass2.AnonymousClass1 */

                    /* access modifiers changed from: protected */
                    public void onBindViewHolder(searchviewholder holder, int position, friendlist model) {
                        final String postkey = model.getUid();
                        TextView textView = holder.name;
                        textView.setText(model.getName().substring(0, 1).toUpperCase() + model.getName().substring(1).toLowerCase());
                        if (!model.getAddress().isEmpty()) {
                            holder.place.setVisibility(View.VISIBLE);
                            TextView textView2 = holder.place;
                            textView2.setText(model.getAddress().substring(0, 1).toUpperCase() + model.getAddress().substring(1));
                        }
                        String images = model.getImage();
                        if (images.isEmpty()) {
                            holder.image.setImageResource(R.drawable.profile);
                        } else {
                            Picasso.get().load(images).into(holder.image);
                        }
                        holder.view.setOnClickListener(new View.OnClickListener() {
                            /* class com.hfad.travelx.SearchableActivity.AnonymousClass2.AnonymousClass1.AnonymousClass1 */

                            public void onClick(View view) {
                                Intent intent = new Intent(SearchableActivity.this, ProfileActivity.class);
                                intent.putExtra("visit_user_id", postkey);
                                SearchableActivity.this.startActivity(intent);
                            }
                        });
                    }

                    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
                    public searchviewholder onCreateViewHolder(ViewGroup parent, int viewType) {
                        return new searchviewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_holder, parent, false));
                    }
                };
                SearchableActivity.this.recyclerView.setAdapter(SearchableActivity.this.recycleradapter);
                SearchableActivity.this.recycleradapter.startListening();
                return true;
            }

            @Override // androidx.appcompat.widget.SearchView.OnQueryTextListener
            public boolean onQueryTextChange(String newText) {
                Query startAt = SearchableActivity.this.userref.orderByChild(filtervalue).startAt(newText.toLowerCase());
                FirebaseRecyclerOptions<friendlist> options = new FirebaseRecyclerOptions.Builder().setQuery(startAt.endAt(newText.toLowerCase() + ""), friendlist.class).build();
                SearchableActivity.this.recycleradapter = new FirebaseRecyclerAdapter<friendlist, searchviewholder>(options) {
                    /* class com.hfad.travelx.SearchableActivity.AnonymousClass2.AnonymousClass2 */

                    /* access modifiers changed from: protected */
                    public void onBindViewHolder(searchviewholder holder, int position, friendlist model) {
                        final String postkey = model.getUid();
                        TextView textView = holder.name;
                        textView.setText(model.getName().substring(0, 1).toUpperCase() + model.getName().substring(1));
                        if (!model.getAddress().isEmpty()) {
                            holder.place.setVisibility(View.VISIBLE);
                            TextView textView2 = holder.place;
                            textView2.setText(model.getAddress().substring(0, 1).toUpperCase() + model.getAddress().substring(1));
                        }
                        String images = model.getImage();
                        if (images.isEmpty()) {
                            holder.image.setImageResource(R.drawable.profile);
                        } else {
                            Picasso.get().load(images).into(holder.image);
                        }
                        holder.view.setOnClickListener(new View.OnClickListener() {
                            /* class com.hfad.travelx.SearchableActivity.AnonymousClass2.AnonymousClass2.AnonymousClass1 */

                            public void onClick(View view) {
                                Intent intent = new Intent(SearchableActivity.this, ProfileActivity.class);
                                intent.putExtra("visit_user_id", postkey);
                                SearchableActivity.this.startActivity(intent);
                            }
                        });
                    }

                    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
                    public searchviewholder onCreateViewHolder(ViewGroup parent, int viewType) {
                        return new searchviewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_holder, parent, false));
                    }
                };
                SearchableActivity.this.recyclerView.setAdapter(SearchableActivity.this.recycleradapter);
                SearchableActivity.this.recycleradapter.startListening();
                return true;
            }
        });
    }

    public void Showfilteroption(View v) {
        final String queri = this.searchView.getQuery().toString();
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            /* class com.hfad.travelx.SearchableActivity.AnonymousClass3 */

            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.by_name /*{ENCODED_INT: 2131361939}*/:
                        SearchableActivity.this.SearchedResults("name", queri);
                        searchView.setQuery("",true);
                        return true;
                    case R.id.by_place /*{ENCODED_INT: 2131361940}*/:
                        SearchableActivity.this.SearchedResults("address", queri);
                        searchView.setQuery("",true);
                        return true;
                    default:
                        return true;
                }
            }
        });
    }

    /* access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onStart() {
        super.onStart();
    }

    public class searchviewholder extends RecyclerView.ViewHolder {
        CircleImageView image;
        TextView name;
        TextView place;
        View view;

        public searchviewholder(View itemView) {
            super(itemView);
            this.view = itemView;
            this.name = (TextView) itemView.findViewById(R.id.search_name);
            this.place = (TextView) itemView.findViewById(R.id.search_place);
            this.image = (CircleImageView) itemView.findViewById(R.id.search_image);
        }
    }
}
