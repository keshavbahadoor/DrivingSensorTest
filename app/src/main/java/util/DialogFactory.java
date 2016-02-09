package util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.driving.senor.test.MainActivity;
import com.driving.senor.test.R;

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

    public static Dialog getSignInDialog( Activity activity, String title )
    {
        Dialog dialog = new Dialog( activity );
        dialog.setContentView( R.layout.sign_in_dialog );
        dialog.setTitle( title );
        return dialog;
    }
}
