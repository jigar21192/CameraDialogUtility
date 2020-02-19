package com.jp.cameradialogutility;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;


import com.jp.cameradialogutility.R;
import com.jp.cameradialogutility.camera.ImageCropActivity;
import com.jp.cameradialogutility.camera.ImagesActivity;
import com.jp.cameradialogutility.model.ImagesModel;
import com.jp.cameradialogutility.utils.BottomSheetPermissionFragment;
import com.jp.cameradialogutility.utils.DialogFragmentForResultReceiverUtils;
import com.jp.cameradialogutility.utils.SdcardUtils;

import java.io.IOException;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class CameraDialog {
    Context context;
    FragmentManager supportFragmentManager;
    String MaximumuSelectedImageCount;
    OnSelectResult result;
    AppCompatActivity appCompatActivity;
    public CameraDialog(final Context context,AppCompatActivity appCompatActivity, final FragmentManager supportFragmentManager, final String MaximumuSelectedImageCount , final OnSelectResult result) {
        {
            this.context=context;
            this.appCompatActivity=appCompatActivity;
            this.supportFragmentManager=supportFragmentManager;
            this.MaximumuSelectedImageCount=MaximumuSelectedImageCount;
            this.result=result;
            permissionDialog();
        }
    }

    private void permissionDialog() {
        new BottomSheetPermissionFragment(appCompatActivity, new BottomSheetPermissionFragment.OnPermissionResult() {
            @Override
            public void onPermissionAllowed() {
                dialog();
                //permission allowed
            }

            @Override
            public void onPermissionDenied() {
                //permission denied
                BottomSheetPermissionFragment.openSettingsDialog(context);
            }
        }, BottomSheetPermissionFragment.CAMERA, BottomSheetPermissionFragment.READ_EXTERNAL_STORAGE, BottomSheetPermissionFragment.WRITE_EXTERNAL_STORAGE).show(supportFragmentManager, "");

    }




    public void dialog() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialouge_image_chooser);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
        dialog.show();
        TextView tv_dialougeSignUpActivity_gallery = dialog.findViewById(R.id.tv_dialougeSignUpActivity_gallery);
        TextView tv_dialougeSignUpActivity_camera = dialog.findViewById(R.id.tv_dialougeSignUpActivity_camera);
        TextView tv_dialougeSignUpActivity_cancel = dialog.findViewById(R.id.tv_dialougeSignUpActivity_cancel);

        tv_dialougeSignUpActivity_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tv_dialougeSignUpActivity_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                new DialogFragmentForResultReceiverUtils(
                        context,
                        new Intent(context, ImagesActivity.class).putExtra("type", "0").putExtra("imgLisrSize", MaximumuSelectedImageCount),
                        new DialogFragmentForResultReceiverUtils.ResultListener() {
                            @Override
                            public void onActivityResult(Intent data, int requestCode, int resultCode) {
                                if (data != null) {

                                    ArrayList<ImagesModel> dataListCurrent = (ArrayList<ImagesModel>) data.getSerializableExtra("croppedImageList");
                                    result.onGalleryResult(dataListCurrent);

                                    //  dataList.addAll(dataListCurrent);
                                    //    Log.e("SelectedImagesSize", ">>>>>>" + dataList.size());
                                    // adapter.notifyDataSetChanged();

                                }
                                // dataList = data;
                            }
                        }
                ).show(supportFragmentManager, "");
            }
        });
        tv_dialougeSignUpActivity_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SdcardUtils.MEDIA_FILE_ORIGINAL = SdcardUtils.returnImageFileName();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    // Do something for lollipop and above versions
                    SdcardUtils.CAMERA_IMAGE_URI = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", SdcardUtils.MEDIA_FILE_ORIGINAL);

                } else {
                    // do something for phones running an SDK before lollipop
                    SdcardUtils.CAMERA_IMAGE_URI = Uri.fromFile(SdcardUtils.MEDIA_FILE_ORIGINAL);
                }
                new DialogFragmentForResultReceiverUtils(
                        context,
                        new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, SdcardUtils.CAMERA_IMAGE_URI),
                        new DialogFragmentForResultReceiverUtils.ResultListener() {
                            @Override
                            public void onActivityResult(Intent data, int requestCode, int resultCode) {
                                if (data != null) {

                                    switch (requestCode) {
                                        case 1:
                                            if (resultCode == RESULT_OK) {
                                                //Camera Take Photo
                                                if (SdcardUtils.CAMERA_IMAGE_URI != null) {
                                                    SdcardUtils.ORIGINAL_IMAGE_PATH = SdcardUtils.CAMERA_IMAGE_URI.getPath();
                                                    getBitmapRotation(SdcardUtils.ORIGINAL_IMAGE_PATH);

                                                    new DialogFragmentForResultReceiverUtils(
                                                            context,
                                                            new Intent(context, ImageCropActivity.class),
                                                            new DialogFragmentForResultReceiverUtils.ResultListener() {
                                                                @Override
                                                                public void onActivityResult(Intent data, int requestCode, int resultCode) {
                                                                    if (requestCode==1) {
                                                                        if (SdcardUtils.CROPED_IMAGE_PATH != null) {
                                                                            ImagesModel imagesModel = new ImagesModel(Uri.fromFile(SdcardUtils.CROPED_IMAGE_PATH).toString(), 0, "", "");
                                                                            // dataList.add(imagesModel);
                                                                            result.onCameraResult(Uri.fromFile(SdcardUtils.CROPED_IMAGE_PATH).toString());

                                                                            //  adapter.notifyDataSetChanged();
                                                                        }
                                                                    }
                                                                    // dataList = data;
                                                                }
                                                            }
                                                    ).show(supportFragmentManager, "");
                                                }
                                            }
                                            break;

                                    }

                                }
                                // dataList = data;
                            }
                        }
                ).show(supportFragmentManager, "");

                dialog.dismiss();
            }
        });
    }

    public interface OnSelectResult {
        void onGalleryResult(ArrayList<ImagesModel> dataListCurrent);

        void onCameraResult(String imageUri);
    }

    private static int getBitmapRotation(String originalImagePath) {
        int rotation = 0;
        switch (getExifOrientation(originalImagePath)) {
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotation = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotation = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotation = 270;
                break;
        }
        return rotation;
    }
    private static int getExifOrientation(String originalImagePath) {
        ExifInterface exif;
        int orientation = 0;
        try {
            exif = new ExifInterface(originalImagePath);
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return orientation;
    }
}
