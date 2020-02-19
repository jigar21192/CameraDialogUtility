package com.medialisting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jp.loggerutils.Logger;
import com.medialisting.camera.ImageCropActivity;
import com.medialisting.camera.ImagesActivity;
import com.medialisting.model.ImagesModel;
import com.medialisting.utils.BottomSheetPermissionFragment;
import com.medialisting.utils.DialogFragmentForResultReceiverUtils;
import com.medialisting.utils.SdcardUtils;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<ImagesModel> dataList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button dialog=findViewById(R.id.dialog);
        dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new  CameraDialog(MainActivity.this,MainActivity.this, MainActivity.this.getSupportFragmentManager(),"5", new CameraDialog.OnSelectResult() {
                    @Override
                    public void onGalleryResult(ArrayList<ImagesModel> dataListCurrent) {
                        Log.e("SizeImage",">>>>>>"+dataListCurrent.size());
                    }

                    @Override
                    public void onCameraResult(String imageUri) {
                        Log.e("CropImage",">>>>>>"+imageUri);
                    }
                });
            }
        });



    }

    
}
