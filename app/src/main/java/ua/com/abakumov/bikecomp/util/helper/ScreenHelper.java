package ua.com.abakumov.bikecomp.util.helper;

import android.content.ContentResolver;
import android.view.Window;
import android.view.WindowManager;

import static android.provider.Settings.System.*;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

/**
 * Created by Oleksandr_Abakumov on 1/13/2016.
 */
public class ScreenHelper {
    public enum BrightnessLevel {
        MAX(255), MIDDLE(127), AUTO(1);

        private final int level;

        BrightnessLevel(int level) {

            this.level = level;
        }

        public int getLevel() {
            return level;
        }
    }

    public enum ScreenLock {ALWAYS_ON, SYS_DEFAULT}

    public static void setScreenLock(ScreenLock lock, Window window) {
        if (lock == ScreenLock.ALWAYS_ON) {
            window.addFlags(FLAG_KEEP_SCREEN_ON);
        } else if (lock == ScreenLock.SYS_DEFAULT) {
            window.clearFlags(FLAG_KEEP_SCREEN_ON);
        }
    }

    public static void setBrightness(BrightnessLevel brightness, Window window) {
        ContentResolver contentResolver = window.getContext().getContentResolver();

        if (brightness == BrightnessLevel.AUTO) {
            putFloat(contentResolver, "screen_auto_brightness_adj", brightness.getLevel());
        } else {
            putInt(contentResolver, SCREEN_BRIGHTNESS, brightness.getLevel());
        }

        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.screenBrightness = brightness.getLevel();
        window.setAttributes(attributes);
    }
}
