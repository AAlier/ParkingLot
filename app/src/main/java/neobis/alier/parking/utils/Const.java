package neobis.alier.parking.utils;

import android.support.annotation.IdRes;

public class Const {
    /*
     * Constants for Google Maps
     */
    @IdRes
    public static final int ZOOM_CONTROL_ID = 0x1;
    @IdRes
    public static final int MY_LOCATION_CONTROL_ID = 0x2;

    public static final String ERROR_LOAD = "Ошибка загрузки";
    public static final float PROGRESSBAR_SIZE = .4f;
    public static final String BROADCAST = "PACKAGE_NAME.android.action.broadcast";

    enum STEPS {
        NONE, TRIAL_PERIOD, WAITING, LEFT
    }
}
