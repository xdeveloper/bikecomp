package ua.com.abakumov.bikecomp.util;

import android.content.res.Resources;

import static android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen;
import static android.R.style.Theme_Holo_NoActionBar_Fullscreen;
import static android.R.style.Theme_Material;
import static android.R.style.Theme_Material_Light;
import static android.R.style.Theme_Material_Light_DarkActionBar;

import static android.support.v7.appcompat.R.style.Base_V7_Theme_AppCompat;
import static android.support.v7.appcompat.R.style.Base_V7_Theme_AppCompat_Light;


/**
 * <Class Name and Purpose>
 * <p>
 * Created by Oleksandr Abakumov on 7/22/15.
 */
public class FullscreenThemeDecider implements ThemeDecider{
    @Override
    public int dailyTheme() {
        return android.R.style.Theme_Material;
    }

    @Override
    public int nightlyTheme() {
        return android.R.style.Theme_Material;
    }
}
