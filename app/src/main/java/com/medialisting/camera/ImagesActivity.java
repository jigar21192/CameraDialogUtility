package com.medialisting.camera;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.medialisting.R;
import com.medialisting.adapter.ImagesAdapter;
import com.medialisting.model.ImagesModel;

import java.io.File;
import java.util.ArrayList;

public class ImagesActivity extends AppCompatActivity implements View.OnClickListener{


    private RecyclerView rv_CameraFragment;
    private ImagesAdapter imagesAdapter;
    public static ArrayList<File> fileList = new ArrayList<File>();
    private ArrayList<ImagesModel> gallery_list;
    public TextView tv_CreateGroupActivity_ok;
    ImageView img_left_Toolbar_CreateGroupActivity;
    String type;
    int imgListSize;
    File dir;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
        type = getIntent().getStringExtra("type");
        imgListSize = Integer.parseInt(getIntent().getStringExtra("imgLisrSize"));


        initViews();

        switch (type) {
            case "0":
                fetchGalleryImages();

                break;
            case "1":
                fetchGalleryVideos();
                break;
            case "2":

                break;
        }
    }


    private void initViews() {

        rv_CameraFragment = findViewById(R.id.rv_CameraActivity);
        tv_CreateGroupActivity_ok = findViewById(R.id.tv_CameraActivity_ok);
        img_left_Toolbar_CreateGroupActivity = findViewById(R.id.iv_CameraActivity_back);

        img_left_Toolbar_CreateGroupActivity.setOnClickListener(this);

    }

    private void fetchGalleryImages() {
        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media._ID, MediaStore.Video.Thumbnails.DATA};//get all columns of type images
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        Cursor imagecursor = managedQuery(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy + " DESC");

        gallery_list = new ArrayList<>();

        for (int i = 0; i < imagecursor.getCount(); i++) {
            imagecursor.moveToPosition(i);
            int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);//get column index
            ImagesModel cameraModel = new ImagesModel("",0,"","");
            cameraModel.setImage(imagecursor.getString(dataColumnIndex));
            gallery_list.add(cameraModel);//get Image from column index
        }
        setUpGridView();
    }

    private void fetchGalleryVideos() {
        int int_position = 0;
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name,column_id,thum;

        String absolutePathOfImage = null;
        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media._ID, MediaStore.Video.Thumbnails.DATA};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
        column_id = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
        thum = cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA);
        //  thum = cursor.getType(MediaStore.Video.Thumbnails.MINI_KIND);
        gallery_list = new ArrayList<>();
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);

        }

        setUpGridView();

    }


    private void setUpGridView() {
        rv_CameraFragment.setLayoutManager(new GridLayoutManager(this, 3));
        imagesAdapter = new ImagesAdapter(this, ImagesActivity.this, gallery_list, true,imgListSize);
        rv_CameraFragment.setAdapter(imagesAdapter);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.iv_CameraActivity_back:
                onBackPressed();
                break;
        }
    }

}
