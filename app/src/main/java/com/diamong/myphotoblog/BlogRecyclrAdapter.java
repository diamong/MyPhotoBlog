package com.diamong.myphotoblog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class BlogRecyclrAdapter extends RecyclerView.Adapter<BlogRecyclrAdapter.ViewHolder> {

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;


    public List<BlogPost> blogPosts;

    public Context context;

    public BlogRecyclrAdapter(List<BlogPost> blogPosts) {
        this.blogPosts = blogPosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.blog_list_item,viewGroup,false);

        context = viewGroup.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        viewHolder.setIsRecyclable(false);

        final String blogPostId = blogPosts.get(i).BlogPostId;
        final String currendUserId = firebaseAuth.getCurrentUser().getUid();

        viewHolder.setDescText(blogPosts.get(i).getDesc());

        String blogPostImageUrl = blogPosts.get(i).getImage_url();
        String blogThumbImageUrl = blogPosts.get(i).getThumb_image_url();
        viewHolder.setBlogImage(blogPostImageUrl, blogThumbImageUrl);

        firebaseFirestore.collection("Users").document(blogPosts.get(i).getUser_id()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {

                            String userName = task.getResult().getString("name");
                            String userImage = task.getResult().getString("image");
                            viewHolder.setUserData(userName, userImage);

                        } else {

                        }
                    }
                });


        long blogTime = blogPosts.get(i).getTime_stamp().getTime();
        String dateString = DateFormat.format("MM/dd/yyyy", new Date(blogTime)).toString();
        viewHolder.setTime(dateString);

        firebaseFirestore
                .collection("Posts/" + blogPostId + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (!queryDocumentSnapshots.isEmpty()){

                    int count = queryDocumentSnapshots.size();
                    viewHolder.updateBlogLikeCount(count);
                }else{

                    viewHolder.updateBlogLikeCount(0);
                }
            }
        });

        firebaseFirestore
                .collection("Posts/" + blogPostId + "/Likes")
                .document(currendUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()){
                    viewHolder.blogLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.add_like_accent));
                }else {
                    viewHolder.blogLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.add_like_gray));
                }
            }
        });

        viewHolder.blogLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseFirestore
                        .collection("Posts/" + blogPostId + "/Likes")
                        .document(currendUserId)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (!task.getResult().exists()){
                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());


                            firebaseFirestore
                                    .collection("Posts/" + blogPostId + "/Likes")
                                    .document(currendUserId)
                                    .set(likesMap);
                        } else {
                            firebaseFirestore
                                    .collection("Posts/" + blogPostId + "/Likes")
                                    .document(currendUserId)
                                    .delete();
                        }
                    }
                });


            }
        });
    }

    @Override
    public int getItemCount() {
        return blogPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView descView, blogUserName, blogTime;
        private ImageView blogImageView, blogUserImage;
        //private FirebaseAuth mAuth;

        private ImageView blogLikeBtn;
        private TextView blogLikeCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;

            blogLikeBtn = mView.findViewById(R.id.blog_like_btn);
        }

        public void setDescText(String descText){
            descView=mView.findViewById(R.id.blog_post_description);
            descView.setText(descText);
        }

        public void setBlogImage(String downloadUri, String thumbImageUri) {
            blogImageView = mView.findViewById(R.id.blog_post_image);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.image_sunset);

            Glide.with(context)
                    .applyDefaultRequestOptions(requestOptions)
                    .load(downloadUri)
                    .thumbnail(Glide.with(context).load(thumbImageUri))
                    .into(blogImageView);
        }


        public void setTime(String dateString) {
            blogTime = mView.findViewById(R.id.blog_post_date);
            blogTime.setText(dateString);
        }

        public void setUserData(String userName, String userImage) {
            blogUserName = mView.findViewById(R.id.blog_post_user_name);
            blogUserImage = mView.findViewById(R.id.blog_post_profile_image);

            blogUserName.setText(userName);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.baseline_person_black_24dp);
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(userImage).into(blogUserImage);
        }

        public void updateBlogLikeCount(int count){
            blogLikeCount=mView.findViewById(R.id.blog_like_count);
            blogLikeCount.setText(count + "   Likes");
        }
    }
}
