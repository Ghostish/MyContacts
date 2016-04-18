package learn.android.kangel.mycontacts.utils;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Kangel on 2016/4/8.
 */
public class AnimationUtil {
    public static void slide(final View targetView, int fromHeight, int toHeight) {
        ValueAnimator animator = ValueAnimator.ofInt(fromHeight, toHeight);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int currHeight = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams lp = targetView.getLayoutParams();
                lp.height = currHeight;
                targetView.setLayoutParams(lp);
            }
        });
        animator.setDuration(300).start();
    }
}
