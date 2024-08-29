package com.example.imagedownloader;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;

public class ImageUtils {

    public static void saveImageToGallery(Context context, ImageView imageView, String title, String description) {
        Bitmap bitmap = null;
        // Get the Bitmap from the ImageView
        try {
            imageView.setDrawingCacheEnabled(true);
            bitmap = Bitmap.createBitmap(imageView.getDrawingCache());
            imageView.setDrawingCacheEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (bitmap != null) {
            // Save the Bitmap to external storage directory
            String savedImagePath = saveBitmap(context, bitmap);

            if (savedImagePath != null) {
                // Insert image into the device's gallery
                addImageToGallery(context, savedImagePath, title, description);
            } else {
                Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static String saveBitmap(Context context, Bitmap bitmap) {
        String savedImagePath = null;

        // Create a directory for images if it doesn't exist
        File imagesDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "YourDirectoryName");

        if (!imagesDir.exists()) {
            imagesDir.mkdirs();
        }

        // Generate a unique filename
        String timeStamp = String.valueOf(new Date().getTime());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File imageFile = new File(imagesDir, imageFileName);
        savedImagePath = imageFile.getAbsolutePath();

        try {
            OutputStream outputStream = new FileOutputStream(imageFile);
            bitmap.compress(CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            savedImagePath = null;
        }

        return savedImagePath;
    }

    private static void addImageToGallery(Context context, String imagePath, String title, String description) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.ImageColumns.BUCKET_ID, imagePath.hashCode());
        values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, new File(imagePath).getParentFile().getName());
        values.put("_data", imagePath);

        ContentResolver cr = context.getContentResolver();
        cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }
}

