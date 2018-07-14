package cn.wrh.smart.dove.view.snippet;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import cn.wrh.smart.dove.R;

/**
 * Created by wurenhai on 2016/7/27.
 */
public class DividerDecoration extends RecyclerView.ItemDecoration {

    private Drawable drawable;
    private Rect padding = new Rect();

    public DividerDecoration(Resources resources) {
        drawable = resources.getDrawable(R.drawable.divider_decoration, null);
        drawable.getPadding(padding);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        final int left = parent.getPaddingLeft() + padding.left;
        final int right = parent.getWidth() - parent.getPaddingRight() - padding.right;

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)child.getLayoutParams();

            //以下计算主要用来确定绘制的位置
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + drawable.getIntrinsicHeight();
            drawable.setBounds(left, top, right, bottom);
            drawable.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0, 0, drawable.getIntrinsicWidth());
    }

}
