package learn.android.kangel.mycontacts.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.CallLog;
import android.support.v7.widget.RecyclerView;
import android.util.LruCache;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kangel on 2016/4/27.
 */
public class LocationLoader {
    private static final int LOAD_OK = 200;
    final static private Handler mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == LOAD_OK) {
                MObj result = (MObj) msg.obj;
                RecyclerView.Adapter adapter = result.adapter;
                int position = result.position;
                if (adapter != null) {
                    adapter.notifyItemChanged(position);
                }
                return true;
            }
            return false;
        }
    });
    private final static LruCache<String, String> LruCache = new LruCache<>(200);
    private final static ThreadPoolExecutor mExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
    private SQLiteDatabase locationDB;

    public LocationLoader(SQLiteDatabase db) {
        this.locationDB = db;
    }

    private static class MObj {
        RecyclerView.Adapter adapter;
        int position;

        public MObj(RecyclerView.Adapter adapter, int position) {
            this.adapter = adapter;
            this.position = position;
        }
    }

    public void bindLocation(final CallogBean bean, final int itemPosition, final RecyclerView.Adapter adapter) {
        if (LruCache.get(bean.getNumber()) != null) {
            bean.setLocation(LruCache.get(bean.getNumber()));
        } else {
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    AttributionUtil.LocationBean locationBean = AttributionUtil.getAttribution(bean.getNumber(), locationDB);
                    if (locationBean != null) {
                        bean.setLocation(locationBean.getCity());
                    }
                    LruCache.put(bean.getNumber(), locationBean == null ? "" : locationBean.getCity());
                    Message msg = mHandler.obtainMessage(LOAD_OK, new MObj(adapter, itemPosition));
                    mHandler.sendMessage(msg);
                }
            });
        }
    }
}
