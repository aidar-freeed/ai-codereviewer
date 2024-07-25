package com.adins.mss.base.dynamictheme;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.adins.mss.foundation.image.Utils;

/**
 * Created by intishar.fa on 06/09/2018.
 */

public class ThemeUtility {

    public static String getColorItemValue(DynamicTheme colorSet, String itemName){
        String value = "";
        if(null != colorSet) {
            if (colorSet.getThemeItemList() != null){
                for (ThemeItem item : colorSet.getThemeItemList()) {
                    if (item.getItemName().equals(itemName)) {
                        value = item.getValue();
                        break;
                    }
                }
            }
        }
        return value;
    }

    public static void setToolbarColor(Toolbar toolbar,int color){
        toolbar.setBackgroundColor(color);
    }

    public static void setStatusBarColor(Activity activity, int color){
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(color);
        }
    }

    public static void setViewBackground(View view, int color){
        Drawable drawable = null;
        if(view instanceof ImageView)
            drawable = ((ImageView) view).getDrawable();
        else{
            if(view instanceof ImageButton)
                drawable = DrawableCompat.wrap(view.getBackground());
            else
                drawable = view.getBackground();
        }
        DrawableCompat.setTint(drawable,color);
        if(view instanceof ImageButton){
            view.setBackground(DrawableCompat.unwrap(drawable));
        }
    }

    public static void setViewBackground(View view, ColorStateList colorstate){
        Drawable drawable = null;
        if(view instanceof ImageView)
            drawable = ((ImageView) view).getDrawable();
        else{
            if(view instanceof ImageButton)
                drawable = DrawableCompat.wrap(view.getBackground());
            else
                drawable = view.getBackground();
        }
        DrawableCompat.setTintList(drawable,colorstate);
    }

    public static void setTextViewDrawableLeftColor(TextView view,int color){
        Drawable[] drawables = view.getCompoundDrawables();
        if(drawables.length != 0){
            DrawableCompat.setTint(drawables[0],color);//index 0 is left
            view.setCompoundDrawables(drawables[0],null,null,null);
        }
    }

    public static void setEditTextBorder(TextInputLayout view, int color){
        Drawable editTxtDrw = view.getEditText().getBackground();
        DrawableCompat.setTint(editTxtDrw,color);
        view.getEditText().setBackground(editTxtDrw);
    }

    public static void setTextColor(View view,int color){

    }

    public static void setImage(final ImageView view, final String base64Img){
        byte[] imgByteArr = Utils.base64ToByte(base64Img);
        Bitmap tempBitmap = Utils.byteToBitmap(imgByteArr);

        view.setImageBitmap(tempBitmap);
    }

}
