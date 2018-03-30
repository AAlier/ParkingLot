package neobis.alier.parking.utils;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import neobis.alier.parking.R;

public class Permissions {

    public static boolean iPermissionRecordAudio(AppCompatActivity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            Permissions.requestPermission(activity, Request.RECORD_AUDIO,
                    new String[]{Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, true);
            return false;
        }
        return true;
    }

    public static boolean iPermissionCamera(AppCompatActivity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            Permissions.requestPermission(activity, Request.CAMERA,
                    Manifest.permission.CAMERA, true);
            return false;
        }
        return true;
    }

    public static boolean iPermissionContact(AppCompatActivity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            Permissions.requestPermission(activity, Request.READ_CONTACTS,
                    Manifest.permission.READ_CONTACTS, true);
            return false;
        }
        return true;
    }

    public static boolean iPermissionLocation(AppCompatActivity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Permissions.requestPermission(activity, Request.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
            return false;
        } else if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Permissions.requestPermission(activity, Request.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION, true);
            return false;
        }
        return true;
    }

    public static boolean iPermissionWriteStorage(AppCompatActivity appCompatActivity) {
        if (ContextCompat.checkSelfPermission(appCompatActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Permissions.requestPermission(appCompatActivity, Request.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, true);
            return false;
        }
        return true;
    }

    public static boolean iPermissionReadStorage(AppCompatActivity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Permissions.requestPermission(activity, Request.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE, true);
            return false;
        }
        return true;
    }

    /**
     * Requests the fine location permission. If a rationale with an additional explanation should
     * be shown to the user, displays a dialog that triggers the request.
     */
    public static void requestPermission(AppCompatActivity activity, int requestId,
                                         String permission, boolean finishActivity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            RationaleDialog.newInstance(requestId, finishActivity)
                    .show(activity.getSupportFragmentManager(), "dialog");
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestId);
        }
    }

    public static void requestPermission(AppCompatActivity activity, int requestId,
                                         String[] permission, boolean finishActivity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission[0])) {
            RationaleDialog.newInstance(requestId, finishActivity)
                    .show(activity.getSupportFragmentManager(), "dialog");
        } else {
            ActivityCompat.requestPermissions(activity, permission, requestId);
        }
    }

    /**
     * Checks if the result contains a {@link PackageManager#PERMISSION_GRANTED} result for a
     * permission from a runtime permissions request.
     *
     * @see ActivityCompat.OnRequestPermissionsResultCallback
     */
    public static boolean isPermissionGranted(String[] grantPermissions, int[] grantResults,
                                              String permission) {
        for (int i = 0; i < grantPermissions.length; i++) {
            if (permission.equals(grantPermissions[i])) {
                return grantResults[i] == PackageManager.PERMISSION_GRANTED;
            }
        }
        return false;
    }

    public static class Request {
        public static final int SETTING = 0;
        public static final int CAMERA = 1;
        public static final int WRITE_EXTERNAL_STORAGE = 2;
        public static final int READ_EXTERNAL_STORAGE = 3;
        public static final int READ_CONTACTS = 4;
        public static final int ACCESS_FINE_LOCATION = 5;
        public static final int ACCESS_COARSE_LOCATION = 6;
        public static final int RECORD_AUDIO = 7;
    }

    /**
     * A dialog that explains the use of the location permission and requests the necessary
     * permission.
     * <p>
     * The activity should implement
     * {@link ActivityCompat.OnRequestPermissionsResultCallback}
     * to handle permit or denial of this permission request.
     */
    public static class RationaleDialog extends DialogFragment {

        private static final String ARGUMENT_PERMISSION_REQUEST_CODE = "requestCode";

        private static final String ARGUMENT_FINISH_ACTIVITY = "finish";

        /**
         * Creates a new instance of a dialog displaying the rationale for the use of the location
         * permission.
         * <p>
         * The permission is requested after clicking 'ok'.
         *
         * @param requestCode    Id of the request that is used to request the permission. It is
         *                       returned to the
         *                       {@link ActivityCompat.OnRequestPermissionsResultCallback}.
         * @param finishActivity Whether the calling Activity should be finished if the dialog is
         *                       cancelled.
         */
        public static RationaleDialog newInstance(int requestCode, boolean finishActivity) {
            Bundle arguments = new Bundle();
            arguments.putInt(ARGUMENT_PERMISSION_REQUEST_CODE, requestCode);
            arguments.putBoolean(ARGUMENT_FINISH_ACTIVITY, finishActivity);
            RationaleDialog dialog = new RationaleDialog();
            dialog.setArguments(arguments);
            return dialog;
        }

        public void openApplicationSettings() {
            Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getActivity().getPackageName()));
            startActivityForResult(appSettingsIntent, Request.SETTING);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.request_setting_permissions)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openApplicationSettings();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .create();
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
        }
    }
}
