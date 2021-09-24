package com.example.HelluApp.Community;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.HelluApp.DailyStamp.daily_stamp_write;
import com.example.HelluApp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class community_chatting extends Fragment {
    private DatabaseReference rDatabase = FirebaseDatabase.getInstance().getReference("User");
    String Nickname;
    String Email;
    String Profile;
    String Uid;
    List<String> users_uids = new ArrayList<>();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();
    private String currentUid = user.getUid();

    int i = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view =   inflater.inflate(R.layout.fragment_community_chatting,container,false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(new community_chatting.CommunityChattingRecyclerViewAdapter());


        return view;
    }

    class CommunityChattingRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public List<users_model> users_models;
        //users_model users_models2;

        public CommunityChattingRecyclerViewAdapter(){
            users_models = new ArrayList<>();
            FirebaseDatabase.getInstance().getReference("User").addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    users_models.clear();
                    for(DataSnapshot snapshot :dataSnapshot.getChildren()){
                        users_uids.add(snapshot.getKey());
                        users_models.add(snapshot.child(users_uids.get(i)).getValue(users_model.class));

                        i++;
                    }
                    users_uids.remove(currentUid);
                    notifyDataSetChanged();
                    for(int k = 0;k < users_uids.size(); k++){
                        if(users_uids.get(k).equals(currentUid)) {
                            users_uids.remove(currentUid);
                            users_uids.add(0, currentUid);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatting_list_item,parent,false);
            return new community_chatting.CommunityChattingRecyclerViewAdapter.CustomViewHolder(view);
        }
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position){
            FirebaseDatabase.getInstance().getReference("User").addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Nickname = dataSnapshot.child(users_uids.get(position)).child("Nickname").getValue(String.class);
                    Email = dataSnapshot.child(users_uids.get(position)).child("Email").getValue(String.class);
                    Profile = dataSnapshot.child(users_uids.get(position)).child("ProfileUrl").getValue(String.class);
                    Uid = dataSnapshot.child(users_uids.get(position)).child("Uid").getValue(String.class);
                    Glide.with(holder.itemView.getContext())
                                .load(Profile).apply(new RequestOptions().circleCrop()).into(((community_chatting.CommunityChattingRecyclerViewAdapter.CustomViewHolder) holder).imageView);
                    ((CustomViewHolder) holder).textView_name.setText(Nickname);


                    /*
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view){
                            Intent intent = new Intent(view.getContext(), community_message.class);
                            intent.putExtra("destinationUid", users_uids.get(position));
                            ActivityOptions activityOptions = null;
                            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                                activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(), R.anim.fromleft, R.anim.fromleft);
                                startActivity(intent, activityOptions.toBundle());
                            }
                        }
                    });

                     */
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        @Override
        public int getItemCount(){
            return users_models.size()-1;
        }
        private class CustomViewHolder extends RecyclerView.ViewHolder{
            public ImageView imageView;
            public TextView textView_name;
            public TextView textView_msg;

            public CustomViewHolder(View view){
                super(view);

                imageView = (ImageView) view.findViewById(R.id.user_photo);
                textView_name = (TextView) view.findViewById(R.id.user_name);
                textView_msg = (TextView) view.findViewById(R.id.user_msg);
            }
        }
    }

}