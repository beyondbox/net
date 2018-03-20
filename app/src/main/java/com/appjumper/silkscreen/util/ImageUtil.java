package com.appjumper.silkscreen.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.appjumper.silkscreen.base.MyApplication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 图像处理工具类
 * Created by Botx on 2016/11/26.
 */

public class ImageUtil {

    /**
     * 解析图片方向
     * @return 方向角度
     */
    public static int parseDirection(String imgPath) {
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imgPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (exif != null) {
            int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            switch (ori) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:
                    degree = 0;
                    break;
            }
        }

        return degree;
    }

    /**
     * 压缩图片，按质量压缩
     * @param imgPath　源图片路径
     * @param size　指定大小，单位KB
     * @return
     */
    /*public static Bitmap compressImage(String imgPath, int size) {
        //先按像素压缩了一下，防止像素过大
        Bitmap bitmap = compressImage(imgPath, 1920, 1080);
        if (bitmap == null) {
            return null;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        if (baos.toByteArray().length > size * 1024) {
            int quality = 90;
            while (baos.toByteArray().length > size * 1024) { //循环判断压缩后图片是否大于指定大小,大于继续压缩
                baos.reset(); //重置baos即清空baos
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
                quality -= 10;
            }

            byte[] data = baos.toByteArray();
            LogHelper.e("ImageLength", data.length + "");
            return BitmapFactory.decodeByteArray(data, 0, data.length);
        }

        return bitmap;
    }*/

    /**
     * 压缩图片，按质量压缩
     * @param imgPath 图片路径
     * @param size 目标大小，单位KB
     * @return map中存放原bitmap和需要压缩到的质量
     */
    public static Map<String, Object> compressImage(String imgPath, int size) {
        //压缩质量为100时，有时初始压缩后会比原图大，因为有时ＪＰＧ图片默认是以８０％的质量保存的
        int quality = 80;
        HashMap<String, Object> map = new HashMap<String, Object>();

        //先按像素压缩了一下，防止像素过大
        Bitmap bitmap = compressImage(imgPath, 1920, 1080);
        if (bitmap == null) {
            return null;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);

        if (baos.toByteArray().length > size * 1024) {
            while (baos.toByteArray().length > size * 1024) { //循环判断压缩后图片是否大于指定大小,大于继续压缩
                baos.reset(); //重置baos即清空baos
                quality -= 10;
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            }
        }

        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        map.put(Const.KEY_BITMAP, bitmap);
        map.put(Const.KEY_QUALITY, quality);
        return map;
    }


    /**
     * 压缩图片，按像素压缩
     * @param imgPath
     * @param width
     * @param height
     * @return
     */
    public static Bitmap compressImage(String imgPath, int width, int height) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, opts);
        int origWidth = opts.outWidth;
        int origHeight = opts.outHeight;
        int be = 1;
        if (origWidth > width || origHeight > height) {
            int target = width > height ? width : height;
            if (origWidth > origHeight) {
                be = Math.round((float)origWidth / target);
            } else {
                be = Math.round((float)origHeight / target);
            }
        }

        opts.inJustDecodeBounds = false;
        opts.inSampleSize = be;
        return BitmapFactory.decodeFile(imgPath, opts);
    }

    /**
     * 保存bitmap到本地
     * @param bitmap
     * @return boolean
     */
    public static boolean saveBitmap(Bitmap bitmap, int quality, File targetFile) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            OutputStream os = null;
            try {
                if (!targetFile.getParentFile().exists()) {
                    targetFile.getParentFile().mkdirs();
                }
                os = new FileOutputStream(targetFile);
                boolean result = bitmap.compress(Bitmap.CompressFormat.JPEG, quality, os);

                if (result) {
                    //通知图片更新
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri uri;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        uri = FileProvider.getUriForFile(MyApplication.appContext, Const.FILE_PROVIDER, targetFile);
                    } else {
                        uri = Uri.fromFile(targetFile);
                    }
                    intent.setData(uri);
                    MyApplication.appContext.sendBroadcast(intent);
                    return true;
                } else {
                    Toast.makeText(MyApplication.appContext, "保存图片失败", Toast.LENGTH_SHORT).show();
                    return false;
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(MyApplication.appContext, "保存图片出错", Toast.LENGTH_SHORT).show();
                return false;
            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        } else {
            Toast.makeText(MyApplication.appContext, "手机存储不可用，请检查存储状态", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * 根据Uri获取图片绝对路径，解决Android4.4以上版本Uri转换
     *
     * @param context
     * @param imageUri
     * @author yaoxing
     * @date 2014-10-12
     */
    @TargetApi(19)
    public static String getImageAbsolutePath(Activity context, Uri imageUri) {
        if (context == null || imageUri == null)
            return null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT
                && DocumentsContract.isDocumentUri(context, imageUri)) {
            if (isExternalStorageDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            } else if (isDownloadsDocument(imageUri)) {
                String id = DocumentsContract.getDocumentId(imageUri);
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[] { split[1] };
                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(imageUri))
                return imageUri.getLastPathSegment();
            return getDataColumn(context, imageUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            return imageUri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = { column };
        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }

}
