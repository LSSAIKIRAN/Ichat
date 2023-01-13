package com.example.ichat.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ichat.Adapter.friendAdapter;
import com.example.ichat.Model.User;
import com.example.ichat.Model.Follow;
import com.example.ichat.R;
import com.example.ichat.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ProfileFragment extends Fragment {


//    RecyclerView recyclerView;
    ArrayList<Follow> list;
    FragmentProfileBinding binding;
    FirebaseAuth auth;
    FirebaseStorage storage;
    FirebaseDatabase database;



    public ProfileFragment() {
        // Required empty public constructor

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
//        View view  = inflater.inflate(R.layout.fragment_profile, container, false);
       binding = FragmentProfileBinding.inflate(inflater,container,false);


       database.getReference().child("Users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               if (snapshot.exists()){
                   User user = snapshot.getValue(User.class);
                   Picasso.get()
                           .load(user.getProfileImage())
                           .placeholder(R.drawable.placeholder)
                           .into(binding.profileimage);

                   binding.name.setText(user.getName());
                   binding.profession.setText(user.getProfession());
                   binding.followers.setText(user.getFollowerCount()+"");
               }

           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });

//        recyclerView = view.findViewById(R.id.friendRv);

        list = new ArrayList<>();



        friendAdapter adapter = new friendAdapter(list,getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setAdapter(friendAdapter);
        binding.friendRv.setLayoutManager(layoutManager);
        binding.friendRv.setAdapter(adapter);

        database.getReference().child("Users")
                        .child(auth.getUid())
                        .child("followers").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Follow follow = dataSnapshot.getValue(Follow.class);
                            list.add(follow);
                        }
                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



        binding.profileimage.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent,11);
        });

        return binding.getRoot();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data.getData()!=null){
            Uri uri = data.getData();
            binding.profileimage.setImageURI(uri);

           final StorageReference referance = storage.getReference().child("profileimage").child(FirebaseAuth.getInstance().getUid());
           referance.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
               @Override
               public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                   Toast.makeText(getContext(), "profile Done!", Toast.LENGTH_SHORT).show();
                   referance.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                       @Override
                       public void onSuccess(Uri uri) {
                           database.getReference().child("Users").child(auth.getUid()).child("profileimage").setValue(uri.toString());

                       }
                   });
               }
           });
        }
    }
}