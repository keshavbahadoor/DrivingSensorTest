package keshav.com.utilitylib;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.View;

/**
 * Created by Keshav on 1/17/2016.
 */
public class DialogFactory
{
    public static AlertDialog getBasicDialog( Context context, String title, String message )
    {
        return new AlertDialog.Builder(context)
                .setTitle( title )
                .setMessage( message )
                .setIcon( android.R.drawable.ic_dialog_info )
                .show();
    }

    public static Dialog getSignInDialog( Activity activity, String title, View view )
    {
        Dialog dialog = new Dialog( activity );
        dialog.setContentView( view );
        dialog.setTitle( title );
        return dialog;
    }
}
