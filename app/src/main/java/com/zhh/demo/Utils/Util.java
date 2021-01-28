package com.zhh.demo.Utils;

import android.graphics.RectF;
import android.view.View;

/**
 * Tip:
 *
 * @author zhh
 * @date 2019-12-01.
 */
public class Util {
    /**
     * 计算指定的 View 在屏幕中的坐标。
     */
    public static RectF calcViewScreenLocation(View view) {
        int[] location = new int[2];
        // 获取控件在屏幕中的位置，返回的数组分别为控件左顶点的 x、y 的值
        view.getLocationOnScreen(location);
        return new RectF(location[0], location[1], location[0] + view.getWidth(),
                location[1] + view.getHeight());
    }
}
