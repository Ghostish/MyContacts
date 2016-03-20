package learn.android.kangel.mycontacts;

/**
 * Created by Kangel on 2016/3/17.
 */

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;


public class ScrollFabBehavior extends FloatingActionButton.Behavior {

    public ScrollFabBehavior(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if (dyConsumed > 0 && child.isShown()) {
            child.setAnimation(AnimationUtils.loadAnimation(child.getContext(), R.anim.fab_slide_out));
            child.setVisibility(View.INVISIBLE);

        } else if ((dyConsumed < 0 || !ViewCompat.canScrollVertically(target, -1) && target instanceof RecyclerView) && !child.isShown()) {
            child.setAnimation(AnimationUtils.loadAnimation(child.getContext(), R.anim.fab_slide_in));
            child.setVisibility(View.VISIBLE);
        }
    }
}
