package fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.driving.senor.test.R;

import keshav.com.utilitylib.LogService;

/**
 * Created by Keshav on 1/23/2016.
 */
public class LogFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    private TextView logWindow;
    private View fragment;
    private Context context;

    @Override
    public void onCreate(  Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {

        // inflate view
        fragment = inflater.inflate( R.layout.fragment_log, container, false );
        context = fragment.getContext();

        logWindow = (TextView) fragment.findViewById( R.id.tv_logs );
        LogService.setLogWindow( logWindow );
        return fragment;
    }

    @Override
    public void onClick( View v ) {

    }
}
