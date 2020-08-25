package com.example.instaclone.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instaclone.Adapter.TagAdapter;
import com.example.instaclone.Adapter.UserAdapter;
import com.example.instaclone.Model.User;
import com.example.instaclone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView,recyclerViewTags;
    private List<User> mUsers;
    private SocialAutoCompleteTextView searchBar;
    private UserAdapter userAdapter;

    private List<String> mHashTags,mHashTagsCount;
    private TagAdapter tagAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.fragment_search, container, false);
       recyclerView=view.findViewById(R.id.recycler_view_users);
       recyclerView.setHasFixedSize(true);
       recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

       recyclerViewTags=view.findViewById(R.id.recycler_view_tags);
       recyclerViewTags.setHasFixedSize(true);
       recyclerViewTags.setLayoutManager(new LinearLayoutManager(getContext()));


       mUsers=new ArrayList<>();
       userAdapter=new UserAdapter(getContext(),mUsers,true);
       recyclerView.setAdapter(userAdapter);

       mHashTags=new ArrayList<>();
       mHashTagsCount=new ArrayList<>();
       tagAdapter=new TagAdapter(getContext(),mHashTags,mHashTagsCount);
       recyclerViewTags.setAdapter(tagAdapter);

       searchBar=view.findViewById(R.id.search_bar);

       readUsers();
       readTags();

       searchBar.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {
               searchUser(s.toString());
           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {
               filter(s.toString());

           }

           @Override
           public void afterTextChanged(Editable s) {

           }
       });

        return view;
    }

    private void readTags() {
        FirebaseDatabase.getInstance().getReference().child("HashTags").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mHashTags.clear();
                mHashTagsCount.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    mHashTags.add(snapshot.getKey());
                    mHashTagsCount.add(snapshot.getChildrenCount() +"");
                }
                tagAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readUsers() {

        DatabaseReference refernce= FirebaseDatabase.getInstance().getReference().child("Users");
        refernce.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(TextUtils.isEmpty(searchBar.getText().toString())){
                    mUsers.clear();
                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                        User user=snapshot.getValue(User.class);
                        mUsers.add(user);
                    }

                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private  void searchUser(String s){
        Query query=FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("username").startAt(s).endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    User user=snapshot.getValue(User.class);
                    mUsers.add(user);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void filter(String text){
        List<String> mSearchTags = new ArrayList<>();
        List<String> mSearchTagsCount=new ArrayList<>();

        for (String s: mHashTags){
            if(s.toLowerCase().contains(text.toLowerCase())){
                mSearchTags.add(s);
                mSearchTagsCount.add(mHashTagsCount.get(mHashTags.indexOf(s)));
            }
        }

        tagAdapter.filter(mSearchTags,mSearchTagsCount);

    }


}
