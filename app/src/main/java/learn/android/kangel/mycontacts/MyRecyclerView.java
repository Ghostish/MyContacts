package learn.android.kangel.mycontacts;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

/**
 * Created by Kangel on 2016/3/18.
 */
public class MyRecyclerView extends FastScrollRecyclerView {
    private View loadingView;
    private boolean isLoading = false;
    private View mEmptyView;

    public interface EndlessScrollingAdapter {
        void onLoadMore();
    }

    public void setLoadingView(View loadingView) {
        this.loadingView = loadingView;
        loadingView.setVisibility(GONE);
    }


    public MyRecyclerView(Context context) {
        super(context);

    }

    public MyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        if (!ViewCompat.canScrollVertically(this, 1)) {
            final Adapter adapter = getAdapter();
            if (adapter instanceof EndlessScrollingAdapter && !isLoading) {
                loadingView.setVisibility(VISIBLE);
                isLoading = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((EndlessScrollingAdapter) adapter).onLoadMore();
                        loadingView.setVisibility(GONE);
                        isLoading = false;
                    }
                }, 3000);
            }
        }
    }

    /**
     * must be called after calling setAdapter
     **/
    public void setEmptyView(View mEmptyView) {
        this.mEmptyView = mEmptyView;
        checkIfEmpty();
    }

    @Override
    public void setAdapter(Adapter adapter) {
        Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }
    }

    final AdapterDataObserver observer = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            checkIfEmpty();
        }
    };

    private void checkIfEmpty() {
        if (mEmptyView != null && getAdapter() != null) {
            mEmptyView.setVisibility(getAdapter().getItemCount() > 0 ? GONE : VISIBLE);
        }
    }
}
