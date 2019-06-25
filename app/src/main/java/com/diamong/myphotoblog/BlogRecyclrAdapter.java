package com.diamong.myphotoblog;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class BlogRecyclrAdapter extends RecyclerView.Adapter<BlogRecyclrAdapter.ViewHolder> {

    public List<BlogPost> blogPosts;

    public BlogRecyclrAdapter(List<BlogPost> blogPosts) {
        this.blogPosts = blogPosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.blog_list_item,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String desc_data = blogPosts.get(i).getDesc();
        viewHolder.setDescText(desc_data);
    }

    @Override
    public int getItemCount() {
        return blogPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView descView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setDescText(String descText){
            descView=mView.findViewById(R.id.blog_post_description);
            descView.setText(descText);
        }
    }
}
