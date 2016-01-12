package ua.com.abakumov.bikecomp.util.theme;


/**
 * <Class Name and Purpose>
 * <p>
 * Created by Oleksandr Abakumov on 7/22/15.
 */
public class FullscreenThemeDecider implements ThemeDecider{
    @Override
    public int dailyTheme() {
        return android.R.style.Theme_Material_Light;
    }

    @Override
    public int nightlyTheme() {
        return android.R.style.Theme_Material;
    }
}
