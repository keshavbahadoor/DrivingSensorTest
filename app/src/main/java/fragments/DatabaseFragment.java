package fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.driving.senor.test.R;

import datalayer.LocalStorage;

/**
 * Created by Keshav on 5/19/2016.
 */
public class DatabaseFragment extends android.support.v4.app.Fragment {

    private View fragment;
    private Context context;
    private TextView text;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {

        // inflate view
        fragment = inflater.inflate( R.layout.fragment_gps, container, false );
        context = fragment.getContext();
        text = (TextView)fragment.findViewById( R.id.tv_database );
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        text.setText( "" );
        text.append( "Sensor Data: " + LocalStorage.getInstance( context ).getSensorDataCount() );
        text.append( "GPS Data: " + LocalStorage.getInstance( context ).getGPSDataCount() );
    }





}
