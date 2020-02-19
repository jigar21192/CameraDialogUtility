package com.medialisting.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.edmodo.cropper.CropImageView;
import com.medialisting.R;
import com.medialisting.utils.ScalingUtilities;
import com.medialisting.utils.SdcardUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

public class ImageCropActivity extends AppCompatActivity implements OnClickListener {
    // ===========================================================
    // Widgets
    // ===========================================================

    public static final int RESIZE_BITMAP_SIZE = 1000;
    private static final int DEFAULT_ASPECT_RATIO_VALUES = 10;
    //	private static final int ROTATE_NINETY_DEGREES = 90;
    private static final String ASPECT_RATIO_X = "ASPECT_RATIO_X";
    private static final String ASPECT_RATIO_Y = "ASPECT_RATIO_Y";
    // ===========================================================
    // Fields/Variables
    // ===========================================================
    public String TAG = "ImageResize";
    ImageButton imgbtn_imagecrop_right;
    TextView txt_imagecrop_title;
    TextView btn_imagecrop_done;
    CropImageView cropImageView;
    Button btn_crop;
    ToggleButton fixedAspectRatioToggle;
    ImageView img_view_croped_image;
    LinearLayout linear_image_display;
    RelativeLayout rel_crop;
    // ===========================================================
    // Methods
    // ===========================================================
    Bitmap croppedImage;
    ExifInterface exif = null;
    // Instance variables
    private int mAspectRatioX = DEFAULT_ASPECT_RATIO_VALUES;

    // ===========================================================
    // Constructors
    // ===========================================================

    // Saves the state upon rotating the screen/restarting the activity
    private int mAspectRatioY = DEFAULT_ASPECT_RATIO_VALUES;

    public static String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    //==================
    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix mtx = new Matrix();
        mtx.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(ASPECT_RATIO_X, mAspectRatioX);
        bundle.putInt(ASPECT_RATIO_Y, mAspectRatioY);
    }

    // Restores the state upon rotating the screen/restarting the activity
    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        mAspectRatioX = bundle.getInt(ASPECT_RATIO_X);
        mAspectRatioY = bundle.getInt(ASPECT_RATIO_Y);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_crop);

        InitViews();
        cropImageView = (CropImageView) findViewById(R.id.CropImageView);
        cropImageView.setGuidelines(0);
        cropImageView.setFixedAspectRatio(true);
        // Sets initial aspect ratio to 10/10, for demonstration purposes
        cropImageView.setAspectRatio(DEFAULT_ASPECT_RATIO_VALUES, DEFAULT_ASPECT_RATIO_VALUES);

        btn_crop = (Button) findViewById(R.id.btn_crop);
        btn_crop.setOnClickListener(this);
       // GlobalVariable.str_original_path = SdcardUtils.ORIGINAL_IMAGE_PATH;
        if (SdcardUtils.ORIGINAL_IMAGE_PATH != null) {
            Bitmap sb = getBitmap(SdcardUtils.ORIGINAL_IMAGE_PATH);
            if (sb != null) {
                cropImageView.setImageBitmap(sb);
            }
        }

        //cropImageView.setFixedAspectRatio(true);

        fixedAspectRatioToggle = (ToggleButton) findViewById(R.id.fixedAspectRatioToggle);
        fixedAspectRatioToggle.setVisibility(View.GONE);
        fixedAspectRatioToggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cropImageView.setFixedAspectRatio(isChecked);
            }
        });

        imgbtn_imagecrop_right.setOnClickListener(this);
        btn_imagecrop_done.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        SdcardUtils.isImageUploading = false;
        SdcardUtils.CROPED_IMAGE_PATH = null;
        SdcardUtils.CROPED_IMAGE_THUMB = null;
        SdcardUtils.ORIGINAL_IMAGE_PATH = "";
        //GlobalVariable.str_original_path = "";
        SdcardUtils.MEDIA_FILE_ORIGINAL = null;
        //StaticData.mediapath = "";
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private Bitmap getBitmap(String path) {

        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 2800000; // 2.8MP
            in = new FileInputStream(path);
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inPreferredConfig = Bitmap.Config.RGB_565;
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();

            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
                scale++;
            }
            Bitmap b = null;
            in = new FileInputStream(path);
            if (scale > 1) {

                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new BitmapFactory.Options();
                o.inPreferredConfig = Bitmap.Config.RGB_565;
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);

                // resize to desired dimensions
                int height = b.getHeight();
                int width = b.getWidth();

                try {
                    exif = new ExifInterface(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("======EXIFvalue========" + exif.getAttribute(ExifInterface.TAG_ORIENTATION));
                if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6")) {
                    b = rotate(b, 90);
                } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("8")) {
                    b = rotate(b, 270);
                } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("3")) {
                    b = rotate(b, 180);
                }

                double y = Math.sqrt(IMAGE_MAX_SIZE / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = ScalingUtilities.createScaledBitmap(b, (int) x, (int) y, ScalingUtilities.ScalingLogic.FIT);
                b.recycle();
                b = scaledBitmap;

                System.gc();
            } else {
                b = BitmapFactory.decodeStream(in);
            }
            in.close();

            final int memUsageKb = (b.getRowBytes() * b.getHeight()) / 1024;
            //Log.e(TAG, "" + memUsageKb + "kb");
            long usebyte = (b.getRowBytes() * b.getHeight());
           // Log.e(TAG, "SIZE: " + readableFileSize(usebyte));

            return b;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_crop:
                croppedImage = cropImageView.getCroppedImage();

             //   Log.v("croppedImage getWidth", "" + croppedImage.getWidth());
              //  Log.v("croppedImage getHeight", "" + croppedImage.getHeight());

                if (croppedImage.getWidth() > RESIZE_BITMAP_SIZE || croppedImage.getHeight() > RESIZE_BITMAP_SIZE) {
                    if (croppedImage.getWidth() > croppedImage.getHeight()) {
                        int new_height = (croppedImage.getHeight() * RESIZE_BITMAP_SIZE) / croppedImage.getWidth();
                        Bitmap scaledBitmap = ScalingUtilities.createScaledBitmap(croppedImage, (int) RESIZE_BITMAP_SIZE, (int) new_height, ScalingUtilities.ScalingLogic.FIT);
                        croppedImage.recycle();
                        croppedImage = scaledBitmap;
                    } else {
                        int new_width = (croppedImage.getWidth() * RESIZE_BITMAP_SIZE) / croppedImage.getHeight();
                        Bitmap scaledBitmap = ScalingUtilities.createScaledBitmap(croppedImage, (int) new_width, (int) RESIZE_BITMAP_SIZE, ScalingUtilities.ScalingLogic.FIT);
                        croppedImage.recycle();
                        croppedImage = scaledBitmap;
                    }
                }

              //  Log.e(TAG, "after croppedImage getWidth: " + croppedImage.getWidth());
              //  Log.e(TAG, "after croppedImage getHeight: " + croppedImage.getHeight());

                // ==============================================================
                // Image
                // ==============================================================
                File imageFile = SdcardUtils.returnImageFileName();
                try {
                    FileOutputStream out = new FileOutputStream(imageFile);
                    croppedImage.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                SdcardUtils.CROPED_IMAGE_PATH = imageFile;
                // ==============================================================
                // Image Thumb
                // ==============================================================
                Bitmap bmThumbnail;
            /*if(croppedImage.getWidth() >  croppedImage.getHeight())
            {
	           	int new_width = (croppedImage.getWidth()*150)/croppedImage.getHeight();
	           	bmThumbnail = ThumbnailUtils.extractThumbnail(croppedImage, 270,270);
	        }
	        else
	        {
	           	int new_height = (croppedImage.getHeight()*150)/croppedImage.getWidth();

	        } */

                bmThumbnail = ThumbnailUtils.extractThumbnail(croppedImage, 270, 270);
                croppedImage.recycle();
                croppedImage = bmThumbnail;
                File thumbImageFile = SdcardUtils.returnThumbImageFileName();
                try {
                    FileOutputStream out = new FileOutputStream(thumbImageFile);
                    croppedImage.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                SdcardUtils.CROPED_IMAGE_THUMB = thumbImageFile;
                //btn_imagecrop_done.setVisibility(View.VISIBLE);
                if (SdcardUtils.MEDIA_FILE_ORIGINAL != null) {
                    if (SdcardUtils.MEDIA_FILE_ORIGINAL.exists()) {
                        SdcardUtils.MEDIA_FILE_ORIGINAL.delete();
                    }
                }
                //cropImageView.setVisibility(View.GONE);
                //rel_crop.setVisibility(View.GONE);
                // img_view_croped_image.setVisibility(View.VISIBLE);
                //linear_image_display.setVisibility(View.VISIBLE);
                Bitmap displaybit = getBitmap(SdcardUtils.CROPED_IMAGE_PATH.toString());
                if (displaybit != null) {
                    SdcardUtils.ORIGINAL_IMAGE_PATH = "";

                    SdcardUtils.MEDIA_FILE_ORIGINAL = null;
                    SdcardUtils.isImageUploading = true;
                    ImageCropActivity.this.finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    //img_view_croped_image.setImageBitmap(displaybit);
                }
            /*SdcardUtils.ORIGINAL_IMAGE_PATH = "";
            SdcardUtils.MEDIA_FILE_ORIGINAL=null;
			SdcardUtils.isImageUploading=true;
			ImageCropActivity.this.finish();*/
                break;

            case R.id.imgbtn_imagecrop_right:
                SdcardUtils.isImageUploading = false;
                SdcardUtils.CROPED_IMAGE_PATH = null;
                SdcardUtils.CROPED_IMAGE_THUMB = null;
                SdcardUtils.ORIGINAL_IMAGE_PATH = "";
                SdcardUtils.MEDIA_FILE_ORIGINAL = null;
                //StaticData.mediapath = "";
                ImageCropActivity.this.finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                break;
            case R.id.btn_imagecrop_done:
                SdcardUtils.ORIGINAL_IMAGE_PATH = "";
                SdcardUtils.MEDIA_FILE_ORIGINAL = null;
                SdcardUtils.isImageUploading = true;
                ImageCropActivity.this.finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


                break;
        }
    }

    public void InitViews() {
        imgbtn_imagecrop_right = (ImageButton) findViewById(R.id.imgbtn_imagecrop_right);
        txt_imagecrop_title = (TextView) findViewById(R.id.txt_imagecrop_title);
        btn_imagecrop_done = (TextView) findViewById(R.id.btn_imagecrop_done);
        btn_imagecrop_done.setVisibility(View.GONE);
        linear_image_display = (LinearLayout) findViewById(R.id.linear_image_display);
        img_view_croped_image = (ImageView) findViewById(R.id.img_view_croped_image);
        rel_crop = (RelativeLayout) findViewById(R.id.rel_crop);

    }



}
