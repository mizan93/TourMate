package com.pro.firebasepro;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageViewAdapter extends RecyclerView.Adapter<ImageViewAdapter.ImageViewHolder>{
    private Context context;
    private List<Upload> uploadList;
    private UserSingleImageClicked listenerim;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference rootRef;
    private DatabaseReference userRef;
    private DatabaseReference uploadRef;

    public ImageViewAdapter(Context context, List<Upload> uploadList) {
        this.context = context;
        this.uploadList = uploadList;
        listenerim = (UserSingleImageClicked) context;

    }


    @NonNull
    @Override
    public ImageViewAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(
                R.layout.image_item, parent, false);
        return new ImageViewAdapter.ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewAdapter.ImageViewHolder holder, final int position) {
        holder.title.setText(uploadList.get(position).getImageName());
        //holder.image_view_upload.setText(String.valueOf(uploadList.get(position).getImageUrl()));

        Picasso.get()
                .load(uploadList.get(position).getImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.image_view_upload);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imageUrl = uploadList.get(position).getImageUrl();
                if(imageUrl.isEmpty()){
                    Toast.makeText(context, "Error in Url", Toast.LENGTH_SHORT).show();

                }else{
                    listenerim.onSingleImageClicked(imageUrl);
                }

            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirmation!");
                builder.setMessage("Do you want to delete this?");
                LayoutInflater inflater = LayoutInflater.from(context);

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        //Toast.makeText(context, "Long Clicked Yes", Toast.LENGTH_SHORT).show();
                        rootRef = FirebaseDatabase.getInstance().getReference();
                        auth = FirebaseAuth.getInstance();
                        user = auth.getCurrentUser();
                        userRef = rootRef.child(user.getUid());
                        uploadRef = userRef.child("Upload");
                        String eventId = uploadList.get(position).getEventId();
                        String imageId = uploadList.get(position).getImageId();
                        uploadRef.child(eventId).child(imageId).removeValue();

/*                        Toast.makeText(context, deleteUri, Toast.LENGTH_SHORT).show();
                        String deleteUri = uploadList.get(position).getImageUrl();

                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReference();
                        StorageReference desertRef = storageRef.child("images/"+deleteUri);
                        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Delete from Storage", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(context, "Failed to Delete from Storage"+exception, Toast.LENGTH_LONG).show();
                            }
                        });*/

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return uploadList.size();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView image_view_upload;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text_view_name);
            image_view_upload = itemView.findViewById(R.id.image_view_upload);

        }
    }

    public void updateList(List<Upload> uploads){
        this.uploadList = uploads;
        notifyDataSetChanged();
    }

    interface UserSingleImageClicked{
        void onSingleImageClicked(String imageUrl);
    }
}

