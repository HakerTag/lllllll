package org.apache.cordova;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.widget.EditText;

public class CordovaDialogsHelper {
    private final Context context;
    private AlertDialog lastHandledDialog;

    public interface Result {
        void gotResult(boolean z, String str);
    }

    public CordovaDialogsHelper(Context context2) {
        this.context = context2;
    }

    public void showAlert(String message, final Result result) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(this.context);
        dlg.setMessage(message);
        dlg.setTitle("Alert");
        dlg.setCancelable(true);
        dlg.setPositiveButton(17039370, new DialogInterface.OnClickListener() {
            /* class org.apache.cordova.CordovaDialogsHelper.AnonymousClass1 */

            public void onClick(DialogInterface dialog, int which) {
                result.gotResult(true, null);
            }
        });
        dlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
            /* class org.apache.cordova.CordovaDialogsHelper.AnonymousClass2 */

            public void onCancel(DialogInterface dialog) {
                result.gotResult(false, null);
            }
        });
        dlg.setOnKeyListener(new DialogInterface.OnKeyListener() {
            /* class org.apache.cordova.CordovaDialogsHelper.AnonymousClass3 */

            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode != 4) {
                    return true;
                }
                result.gotResult(true, null);
                return false;
            }
        });
        this.lastHandledDialog = dlg.show();
    }

    public void showConfirm(String message, final Result result) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(this.context);
        dlg.setMessage(message);
        dlg.setTitle("Confirm");
        dlg.setCancelable(true);
        dlg.setPositiveButton(17039370, new DialogInterface.OnClickListener() {
            /* class org.apache.cordova.CordovaDialogsHelper.AnonymousClass4 */

            public void onClick(DialogInterface dialog, int which) {
                result.gotResult(true, null);
            }
        });
        dlg.setNegativeButton(17039360, new DialogInterface.OnClickListener() {
            /* class org.apache.cordova.CordovaDialogsHelper.AnonymousClass5 */

            public void onClick(DialogInterface dialog, int which) {
                result.gotResult(false, null);
            }
        });
        dlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
            /* class org.apache.cordova.CordovaDialogsHelper.AnonymousClass6 */

            public void onCancel(DialogInterface dialog) {
                result.gotResult(false, null);
            }
        });
        dlg.setOnKeyListener(new DialogInterface.OnKeyListener() {
            /* class org.apache.cordova.CordovaDialogsHelper.AnonymousClass7 */

            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode != 4) {
                    return true;
                }
                result.gotResult(false, null);
                return false;
            }
        });
        this.lastHandledDialog = dlg.show();
    }

    public void showPrompt(String message, String defaultValue, final Result result) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(this.context);
        dlg.setMessage(message);
        final EditText input = new EditText(this.context);
        if (defaultValue != null) {
            input.setText(defaultValue);
        }
        dlg.setView(input);
        dlg.setCancelable(false);
        dlg.setPositiveButton(17039370, new DialogInterface.OnClickListener() {
            /* class org.apache.cordova.CordovaDialogsHelper.AnonymousClass8 */

            public void onClick(DialogInterface dialog, int which) {
                result.gotResult(true, input.getText().toString());
            }
        });
        dlg.setNegativeButton(17039360, new DialogInterface.OnClickListener() {
            /* class org.apache.cordova.CordovaDialogsHelper.AnonymousClass9 */

            public void onClick(DialogInterface dialog, int which) {
                result.gotResult(false, null);
            }
        });
        this.lastHandledDialog = dlg.show();
    }

    public void destroyLastDialog() {
        AlertDialog alertDialog = this.lastHandledDialog;
        if (alertDialog != null) {
            alertDialog.cancel();
        }
    }
}
