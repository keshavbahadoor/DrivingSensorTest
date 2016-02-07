package util;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by Keshav on 1/17/2016.
 */
public class DialogFactory
{
    public static AlertDialog BasicDialog(Context context, String title, String message)
    {
        return new AlertDialog.Builder(context)
                .setTitle( title )
                .setMessage( message )
                .setIcon( android.R.drawable.ic_dialog_info )
                .show();
    }
}
