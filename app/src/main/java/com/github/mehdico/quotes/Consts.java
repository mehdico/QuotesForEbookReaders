package com.github.mehdico.quotes;

import android.content.Context;
import android.graphics.Typeface;

public class Consts {

//    public static final String FONT_PATH_1 = "fonts/sm.ttf";
    public static final String FONT_PATH_1 = "fonts/vazir_r.ttf";
    public static final String FONT_PATH_1_BOLD = "fonts/vazir_bl.ttf";

    private static Typeface tfFont1;
    private static Typeface tfFont1Bold;

    public static Typeface getFont1(Context context) {
        if (tfFont1 == null) {
            tfFont1 = Typeface.createFromAsset(context.getAssets(), FONT_PATH_1);
        }
        return tfFont1;
    }

    public static Typeface getFont1Bold(Context context) {
        if (tfFont1Bold == null) {
            tfFont1Bold = Typeface.createFromAsset(context.getAssets(), FONT_PATH_1_BOLD);
        }
        return tfFont1Bold;
    }
}
