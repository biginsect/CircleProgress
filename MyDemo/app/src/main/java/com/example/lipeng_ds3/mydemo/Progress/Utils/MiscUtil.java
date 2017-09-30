package com.example.lipeng_ds3.mydemo.Progress.Utils;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by lipeng-ds3 on 2017/9/29.
 */

public class MiscUtil {
    /**
     * measure view
     *@param measureSpec
     *@param defaultSize view default size
     */
    public static int measure(int measureSpec, int defaultSize){
        int result = defaultSize;
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);

        if (specMode == View.MeasureSpec.EXACTLY){
            result = specSize;
        }else if (specMode == View.MeasureSpec.AT_MOST){
            result = Math.min(result,specSize);
        }

        return result;
    }

    /**
     * change dip to px
     * @param dip
     * @return
     * */
    public static int dipToPx(Context context, float dip){
        float density = context.getResources().getDisplayMetrics().density;

        return (int)(dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }

    /**
     * @param precision
     * @return
     * */
    public static String getPrecisionFormat(int precision){
        return "%." + precision + "f";
    }

    /**
     * reverse array
     * @param <T>
     * @return
     * */
    public static <T> T[] reverse(T[] arrays){
        if (null == arrays)
            return null;
        int length = arrays.length;
        for (int i = 0; i < length / 2; i++){
            //change value of two element
            T t = arrays[i];
            arrays[i] = arrays[length-i-1];
            arrays[length-i-1] = t;
        }

        return arrays;
    }

    /**
     * measure text Height
     * @param paint
     * @return
     * */
    public static float measureTextHeight(Paint paint){
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();

        return (Math.abs(fontMetrics.ascent) - fontMetrics.descent);
    }

}
