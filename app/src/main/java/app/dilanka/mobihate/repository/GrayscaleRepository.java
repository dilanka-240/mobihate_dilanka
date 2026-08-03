package app.dilanka.mobihate.repository;

import android.content.Context;
import android.provider.Settings;

public class GrayscaleRepository {
    
    private static final String DALTONIZER_ENABLED = "accessibility_display_daltonizer_enabled";
    private static final String DALTONIZER_MODE = "accessibility_display_daltonizer";
    private static final int GREYSCALE_MODE = 0;

    public boolean applyGrayscale(boolean enabled, Context context) {
        try {
            Settings.Secure.putInt(context.getContentResolver(),
                DALTONIZER_MODE, GREYSCALE_MODE);
            
            return Settings.Secure.putInt(context.getContentResolver(),
                DALTONIZER_ENABLED, enabled ? 1 : 0);
                
        } catch (SecurityException e) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}