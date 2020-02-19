package com.jp.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.medialisting.R;
import com.medialisting.camera.ImagesActivity;
import com.medialisting.model.ImagesModel;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.MyContactListViewHolder> {
    private Context context;
    private ArrayList<ImagesModel> image_list;
    private ArrayList<ImagesModel> selectedList = new ArrayList<>();
    private ImagesActivity activity;
    private boolean isCustomGalleryActivity;
    private int MAX_ATTACHMENT_COUNT;
    int size;


    public ImagesAdapter(Context context, ImagesActivity imagesActivity, ArrayList<ImagesModel> gallery_list, boolean isCustomGalleryActivity, int size) {

        this.context = context;
        this.activity = imagesActivity;
        this.image_list = gallery_list;
        this.isCustomGalleryActivity = isCustomGalleryActivity;
        this.MAX_ATTACHMENT_COUNT = size;
    }


    @NonNull
    @Override
    public ImagesAdapter.MyContactListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_images_items, parent, false);
        ImagesAdapter.MyContactListViewHolder holder = new ImagesAdapter.MyContactListViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ImagesAdapter.MyContactListViewHolder holder, final int position) {
     //   MAX_ATTACHMENT_COUNT = 6 - size;

        if (!isCustomGalleryActivity)
            holder.mCheckBox.setVisibility(View.GONE);

        Glide.with(context).load("file://" + image_list.get(position).getImage())
                .into(holder.imageView);

        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        int pxWidth = displayMetrics.widthPixels;
        int pxHeight = displayMetrics.heightPixels;
        holder.relative.getLayoutParams().width = (int) ((pxWidth - convertDpToPixel(12)) / 3);
        holder.relative.getLayoutParams().height = (int) ((pxWidth - convertDpToPixel(12)) / 3);

        if (image_list.get(position).getIs_selected() == 1) {
            holder.imageView_select.setVisibility(View.VISIBLE);
            holder.imageView_select.setImageResource(R.drawable.check_box);
            holder.transparent_bg.setVisibility(View.VISIBLE);
        } else {
            holder.imageView_select.setVisibility(View.GONE);
            holder.transparent_bg.setVisibility(View.GONE);

        }
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (image_list.get(holder.getAdapterPosition()).getIs_selected() == 0) {
                    if (MAX_ATTACHMENT_COUNT == notifyActivityCountUpdated()) {
                        if (MAX_ATTACHMENT_COUNT == 5) {
                            Toast.makeText(context, "you can't select more than 5 image", Toast.LENGTH_SHORT).show();
                        }else if (MAX_ATTACHMENT_COUNT == 4) {
                            Toast.makeText(context, "you can't select more than 4 image", Toast.LENGTH_SHORT).show();
                        }
                        else if (MAX_ATTACHMENT_COUNT == 3) {
                            Toast.makeText(context, "you can't select more than 3 image", Toast.LENGTH_SHORT).show();
                        }
                        else if (MAX_ATTACHMENT_COUNT == 2) {
                            Toast.makeText(context, "you can't select more than 2 image", Toast.LENGTH_SHORT).show();
                        } else if (MAX_ATTACHMENT_COUNT == 1) {
                            Toast.makeText(context, "you can't select more than 1 image", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(context, "you can't select more than images", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        image_list.get(holder.getAdapterPosition()).setIs_selected(1);
                    }
                } else {
                    image_list.get(holder.getAdapterPosition()).setIs_selected(0);
                }
                notifyActivityCountUpdated();

                notifyItemChanged(position);
            }
        });

        activity.tv_CreateGroupActivity_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<ImagesModel> dataList = new ArrayList<>();

                for (int i = 0; i < image_list.size(); i++) {
                    if (image_list.get(i).getIs_selected() == 1) {
                        dataList.add(image_list.get(i));
                    }
                }
                Intent intent = activity.getIntent();
                intent.putExtra("croppedImageList", dataList);
                activity.setResult(RESULT_OK, intent);
                activity.finish();
            }
        });
    }

    private void clearSelected() {
        for (int i = 0; i < image_list.size(); i++) {

            image_list.get(i).setIs_selected(0);
            notifyItemChanged(i);
        }
    }

    private int notifyActivityCountUpdated() {

        int count = 0;
        for (int i = 0; i < image_list.size(); i++) {

            if (image_list.get(i).getIs_selected() == 1) {
                count++;
            }
        }

        // activity.showSelectItem(String.valueOf(count));
        return count;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return image_list.size();
    }


    public class MyContactListViewHolder extends RecyclerView.ViewHolder {
        CheckBox mCheckBox;
        ImageView imageView,
                imageView_select;
        View transparent_bg;
        RelativeLayout relative;

        public MyContactListViewHolder(View itemView) {
            super(itemView);

            imageView_select = (ImageView) itemView.findViewById(R.id.imageView_select);
            imageView = (ImageView) itemView.findViewById(R.id.galleryImageView);
            transparent_bg = (View) itemView.findViewById(R.id.transparent_bg);
            relative = (RelativeLayout) itemView.findViewById(R.id.relative);
        }
    }

    private float convertDpToPixel(float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dp * scale;
    }

    private float convertSpToPixel(float sp) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }
}
