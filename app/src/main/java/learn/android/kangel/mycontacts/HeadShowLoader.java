package learn.android.kangel.mycontacts;

import android.content.ContentUris;
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

import java.io.ByteArrayInputStream;

/**
 * Created by Kangel on 2016/3/28.
 */
public class HeadShowLoader {
    final  private Handler mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
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
    private LruCache<Long,Bitmap> bitmapLruCache = new LruCache<>(200);
    public void bindImageView(final ImageView imageView,final Context context, final long contactId) {
        if (bitmapLruCache.get(contactId) != null) {
            imageView.setImageBitmap(bitmapLruCache.get(contactId));
        }else {
            new Thread(new Runnable() {
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
            }).start();
        }
    }

    class LoadResult {
        ImageView resultImage;
        Bitmap resultBitmap;

        public LoadResult(ImageView resultImage, Bitmap resultBitmap) {
            this.resultImage = resultImage;
            this.resultBitmap = resultBitmap;
        }
    }
}
