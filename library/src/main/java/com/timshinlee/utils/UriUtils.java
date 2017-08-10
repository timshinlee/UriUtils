package com.timshinlee.utils;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

public class UriUtils {
    private static final String TAG = "UriUtils";

    public static String getUriPath(Context context, Uri uri) {
        if (null == uri) {
            return null;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return getFilePath(context, uri, null, null);
        }
        // deal with KitKat and above
        if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(uri.getScheme())) {
            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }
            if (DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    return getExternalStorageDocument(uri);
                }
                if (isMediaDocument(uri)) {
                    return getMediaDocumentPath(context, uri);
                }
                if (isDownloadsDocument(uri)) {
                    return getDownloadsDocumentPath(context, uri);
                }
            }
            return getFilePath(context, uri, null, null);
        } else if (ContentResolver.SCHEME_FILE.equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String getMediaDocumentPath(Context context, Uri uri) {
        final String documentId = DocumentsContract.getDocumentId(uri);
        final String[] split = documentId.split(":");
        final String type = split[0];
        Uri contentUri = null;
        if ("image".equals(type)) {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if ("video".equals(type)) {
            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else if ("audio".equals(type)) {
            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }
        final String selection = MediaStore.Images.Media._ID + "=?";
        final String[] selectionArgs = new String[]{split[1]};
        return getFilePath(context, contentUri, selection, selectionArgs);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String getExternalStorageDocument(Uri uri) {
        final String documentId = DocumentsContract.getDocumentId(uri);
        final String[] split = documentId.split(":");
        final String type = split[0];
        if ("primary".equalsIgnoreCase(type)) {
            return Environment.getExternalStorageDirectory() + "/" + split[1];
        } else {
            // TODO handle non-primary type
            return null;
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String getDownloadsDocumentPath(Context context, Uri uri) {
        final String documentId = DocumentsContract.getDocumentId(uri);
        final Uri downloadUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"), Long.valueOf(documentId));
        return getFilePath(context, downloadUri, null, null);
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getFilePath(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = MediaStore.MediaColumns.DATA;
        final String[] projection = {column};
        // 注意此处projection必须设置为_data，不然可能会出现查询不到的情况
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }


    /**
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @return Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * 此为耗时操作，尽量不要在主线程调用
     */
    public static Bitmap getBitmapFromUri(Context context, Uri uri) {
        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r"); // r表示read
            if (parcelFileDescriptor != null) {
                final FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                return BitmapFactory.decodeFileDescriptor(fileDescriptor);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (parcelFileDescriptor != null) {
                try {
                    parcelFileDescriptor.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
