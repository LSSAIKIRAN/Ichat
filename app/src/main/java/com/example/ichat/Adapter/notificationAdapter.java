package com.example.ichat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ichat.CommentActivity;
import com.example.ichat.Model.Notification;
import com.example.ichat.Model.User;
import com.example.ichat.R;
import com.example.ichat.databinding.Notification2sampleBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class notificationAdapter extends RecyclerView.Adapter<notificationAdapter.viewHolder> {

    ArrayList<Notification> list;
    Context context;

    public notificationAdapter(ArrayList<Notification> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification2sample, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        Notification notificaton = list.get(position);

        String type = notificaton.getType();

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(notificaton.getNotificationBy())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        Picasso.get()
                                .load(user.getProfileImage())
                                .placeholder(R.drawable.placeholder)
                                .into(holder.binding.profileImage);

                        if (type.equals("like")) {
                            holder.binding.notification.setText(Html.fromHtml("<b>" + user.getName() + "</b>" + " liked your Post"));
                        } else if (type.equals("comment")) {
                            holder.binding.notification.setText(Html.fromHtml("<b>" + user.getName() + "</b>" + " Commented on your Post"));
                        } else {
                            holder.binding.notification.setText(Html.fromHtml("<b>" + user.getName() + "</b>" + " Started following you."));
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.binding.openNotification.setOnClickListener(v -> {

            if (!type.equals("follow")) {

                FirebaseDatabase.getInstance().getReference()
                        .child("notification")
                        .child(notificaton.getPostedBy())
                        .child(notificaton.getNotificationID())
                        .child("checkOpen")
                        .setValue(true);

                holder.binding.openNotification.setBackgroundColor(Color.parseColor("#FFFFFF"));
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId", notificaton.getPostID());
                intent.putExtra("postedBy", notificaton.getPostedBy());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }

        });

        Boolean checkopen = notificaton.isCheckOpen();
        if (checkopen == true){
            holder.binding.openNotification.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        else {}


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {


        Notification2sampleBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            binding = Notification2sampleBinding.bind(itemView);

        }
    }
}
