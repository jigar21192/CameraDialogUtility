package com.jp.cameradialogutility.utils;

import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.util.Calendar;

public class SdcardUtils {

    public static final int ACTION_CROP_IMAGE = 1003;
    public static final int ACTION_TAKE_PHOTO = 1001;
    public static final int ACTION_PICK_FROM_GALLERY = 1002;
    public static String BROADCAST_IMAGE_ACTION = "";
    public static boolean isImageUploading = false;
    public static Uri CAMERA_IMAGE_URI;
    public static File MEDIA_FILE_ORIGINAL = null;
    public static String ORIGINAL_IMAGE_PATH = "";
    public static File CROPED_IMAGE_PATH = null;
    public static File CROPED_IMAGE_THUMB = null;

    private static String mainFolderName = "app";
    public static String PUTILS_IMAGE_THUMB = mainFolderName + "/Thumb";  // Sub Folder
    public static String PUTILS_IMAGE_IMAGES = mainFolderName + "/Images";// Sub Folder

    public static String CreateFolder(String folderPath) {
        String folderName = "";

        folderName = Environment.getExternalStorageDirectory() + "/" + folderPath;
        File fileName = new File(folderName);
        if (!fileName.exists()) {
            fileName.mkdirs();
        }
        return folderName;
    }


    public static File returnImageFileName() {
        File imageFileName;
        String ImageName = "Image_" + Calendar.getInstance().getTimeInMillis() + ".jpeg";
        String Imagefolder = CreateFolder(PUTILS_IMAGE_IMAGES);
        if (!Imagefolder.equals("")) {
            imageFileName = new File(Imagefolder, ImageName);
        } else {
            imageFileName = null;
        }
        return imageFileName;
    }

    public static File returnThumbImageFileName() {
        File thumbfilename;
        String ImagethumbName = "Image_thumb_" + Calendar.getInstance().getTimeInMillis() + ".jpeg";
        String ImageThumbfolder = CreateFolder(PUTILS_IMAGE_THUMB);
        if (!ImageThumbfolder.equals("")) {
            thumbfilename = new File(ImageThumbfolder, ImagethumbName);
        } else {
            thumbfilename = null;
        }

        return thumbfilename;
    }

    /**
     * Delete Images
     * When profile pic upload at that time temporary image will store in particular path
     * so it is unused image after upload
     * so delete it
     */
    public static void deleteDataFromFolder() {
        // Delete Images inside "Images" folder
        File dir = new File(Environment.getExternalStorageDirectory() + "/" + PUTILS_IMAGE_IMAGES);
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
        }

        // Delete Images inside "Thumb" folder
        File dir1 = new File(Environment.getExternalStorageDirectory() + "/" + PUTILS_IMAGE_THUMB);
        if (dir1.isDirectory()) {
            String[] children = dir1.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir1, children[i]).delete();
            }
        }

        // Delete Folder of "Images"
        File dir2 = new File(Environment.getExternalStorageDirectory() + "/" + mainFolderName);
        if (dir2.isDirectory()) {
            String[] children = dir2.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir2, children[i]).delete();
            }
        }
    }
}
