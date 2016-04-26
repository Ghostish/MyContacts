package learn.android.kangel.mycontacts.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.InputStream;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import learn.android.kangel.mycontacts.R;

/**
 * Created by Kangel on 2016/3/28.
 */
public class HeadShowLoader {
    final static private Handler mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == LOAD_OK) {
                LoadResult result = (LoadResult) msg.obj;
                ImageView imageView = result.resultImage;
                Bitmap bitmap = result.resultBitmap;
                imageView.setImageBitmap(bitmap);
                return true;
            }
            return false;
        }
    });
    private final static int LOAD_OK = 11;
    private final static LruCache<Uri, Bitmap> bitmapLruCache = new LruCache<>(200);
    private final static ThreadPoolExecutor mExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());

    public static void removeCacheItem(Uri key) {
        bitmapLruCache.remove(key);
    }
    /*public void bindImageView(final ImageView imageView, final Context context, final long contactId) {
        if (bitmapLruCache.get(contactId) != null) {
            imageView.setImageBitmap(bitmapLruCache.get(contactId));
        } else {
            imageView.setImageResource(R.drawable.default_head_show_list);
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
                    Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
                    Cursor cursor = context.getContentResolver().query(photoUri,
                            new String[]{ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
                    if (cursor == null) {
                        return;
                    }
                    try {
                        if (cursor.moveToFirst()) {
                            byte[] data = cursor.getBlob(0);
                            if (data != null) {
                                Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(data));
                                bitmapLruCache.put(contactId, bitmap);
                                Message msg = mHandler.obtainMessage(LOAD_OK, new LoadResult(imageView, bitmap));
                                mHandler.sendMessage(msg);
                            }
                        }
                    } finally {
                        cursor.close();
                    }
                }
            });
        }
    }*/

    class LoadResult {
        ImageView resultImage;
        Bitmap resultBitmap;

        public LoadResult(ImageView resultImage, Bitmap resultBitmap) {
            this.resultImage = resultImage;
            this.resultBitmap = resultBitmap;
        }
    }

    public void bindImageView(final ImageView imageView, final Context context, final String number) {
        String[] projection = new String[]{ContactsContract.Data.CONTACT_ID, ContactsContract.Data.LOOKUP_KEY, ContactsContract.CommonDataKinds.Phone.NUMBER};
        Uri mUri = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI, Uri.encode(number));
        Cursor c = context.getContentResolver().query(mUri, projection,
                null, null, null);
        try {
            if (c != null && c.moveToFirst()) {
                long contactId = c.getLong(0);
                String lookUpKey = c.getString(1);
                String retrievedNumber = c.getString(2);
                /**
                 *compare the two numbers to see if they are really matched
                 */
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < retrievedNumber.length(); i++) {
                    char ch = retrievedNumber.charAt(i);
                    if (Character.isDigit(ch)) {
                        sb.append(ch);
                    }
                }
                if (sb.toString().equals(number)) {
                    bindImageView(imageView, context, contactId, lookUpKey);
                    return;
                }
            }
        } finally {
            c.close();
        }
        imageView.setImageResource(R.drawable.default_head_show_list);
    }

    public void bindImageView(final ImageView imageView, final Context context, final long contactId, final String lookUpKey) {
        final Uri contactUri = ContactsContract.Contacts.getLookupUri(contactId, lookUpKey);
        if (bitmapLruCache.get(contactUri) != null) {
            imageView.setImageBitmap(bitmapLruCache.get(contactUri));
        } else {
            imageView.setImageResource(R.drawable.default_head_show_list);
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    InputStream in = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), contactUri);
                    if (in != null) {
                        Bitmap bitmap = BitmapFactory.decodeStream(in);
                        bitmapLruCache.put(contactUri, bitmap);
                        Message msg = mHandler.obtainMessage(LOAD_OK, new LoadResult(imageView, bitmap));
                        mHandler.sendMessage(msg);
                    }
                }
            });
        }
    }
}
