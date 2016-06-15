package fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.driving.senor.test.R;

import keshav.com.drivingeventlib.GLOBALS;
import keshav.com.utilitylib.LogService;

/**
 * Created by Keshav on 6/12/2016.
 */
public class DebugSettingsFragment extends android.support.v4.app.Fragment implements CompoundButton.OnCheckedChangeListener{

    private View fragment;
    private Context context;
    ToggleButton debug, inVehicle, dataSync;


    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {

        // inflate view
        fragment = inflater.inflate( R.layout.fragment_debug_settings, container, false );
        context = fragment.getContext();
        debug = (ToggleButton) fragment.findViewById( R.id.toggle_debug );
        inVehicle = (ToggleButton) fragment.findViewById( R.id.toggle_alwaysinvehicle );
        dataSync = (ToggleButton) fragment.findViewById( R.id.toggle_datasync );

        debug.setOnCheckedChangeListener( this );
        inVehicle.setOnCheckedChangeListener( this );
        dataSync.setOnCheckedChangeListener( this );

        debug.setChecked( GLOBALS.DEBUG_MODE );
        inVehicle.setChecked( GLOBALS.DEBUG_ALWAYS_IN_VEHICLE );
        dataSync.setChecked( GLOBALS.DEBUG_DATA_SYNC );

        return fragment;
    }

    @Override
    public void onCheckedChanged( CompoundButton buttonView, boolean isChecked ) {

        if (buttonView == debug){
//            GLOBALS.DEBUG_MODE ^= true;
//            debug.setChecked( GLOBALS.DEBUG_MODE );
//            LogService.log( "DEBUG_MODE " + GLOBALS.DEBUG_MODE);
        }
        if (buttonView == inVehicle){
//            GLOBALS.DEBUG_ALWAYS_IN_VEHICLE ^= true;
//            inVehicle.setChecked( GLOBALS.DEBUG_ALWAYS_IN_VEHICLE );
//            LogService.log( "DEBUG_ALWAYS_IN_VEHICLE " + GLOBALS.DEBUG_ALWAYS_IN_VEHICLE);
        }
        if (buttonView == dataSync){
//            GLOBALS.DEBUG_DATA_SYNC ^= true;
//            dataSync.setChecked( GLOBALS.DEBUG_DATA_SYNC );
//            LogService.log( "DEBUG_DATA_SYNC " + GLOBALS.DEBUG_DATA_SYNC);
        }
    }
}
