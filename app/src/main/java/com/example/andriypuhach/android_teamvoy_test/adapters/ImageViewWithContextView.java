package com.example.andriypuhach.android_teamvoy_test.adapters;

/**
 * Created by andriypuhach on 05.02.15.
 */
import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ImageView;

public class ImageViewWithContextView extends ImageView  {
    public ImageViewWithContextView(Context context) {
        super(context);
    }

    @Override
    protected ContextMenuInfo getContextMenuInfo() {
        return new ImageViewContextMenuInfo(this);
    }

    public ImageViewWithContextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ImageViewWithContextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static class ImageViewContextMenuInfo implements ContextMenu.ContextMenuInfo {
        public ImageViewContextMenuInfo(View targetView) {
            this.targetView = (ImageView) targetView;
        }

        public ImageView targetView;
    }
}
