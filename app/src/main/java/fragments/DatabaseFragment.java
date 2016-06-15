package fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.driving.senor.test.R;

import org.json.JSONArray;

import datalayer.LocalStorage;
import datasync.SyncUtil;
import keshav.com.utilitylib.LogService;

/**
 * Created by Keshav on 5/19/2016.
 */
public class DatabaseFragment extends android.support.v4.app.Fragment implements View.OnClickListener{

    private View fragment;
    private Context context;
    private TextView text, dbData;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {

        // inflate view
        fragment = inflater.inflate( R.layout.fragment_database, container, false );
        context = fragment.getContext();
        text = (TextView)fragment.findViewById( R.id.tv_database );
        dbData = (TextView)fragment.findViewById( R.id.textview_db_data );

        fragment.findViewById( R.id.button_delete_data ).setOnClickListener( this );
        fragment.findViewById( R.id.button_refresh ).setOnClickListener( this );
        fragment.findViewById( R.id.button_loadgpsdata ).setOnClickListener( this );
        fragment.findViewById( R.id.button_loadsensordata ).setOnClickListener( this );
        fragment.findViewById( R.id.button_request_sync ).setOnClickListener( this );

        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        text.setText( "" );
        text.append( "Sensor Data: " + LocalStorage.getInstance( context ).getSensorDataCount() );
        text.append( "GPS Data: " + LocalStorage.getInstance( context ).getGPSDataCount() );
    }

    public void clearData(){
        LocalStorage.getInstance( context ).deleteAll();
        //LocalStorage.getInstance( context ).dropAndRecreateTables();
    }

    public void refreshData(){
        text.setText( "" );
        text.append( "Sensor Data: " + LocalStorage.getInstance( context ).getSensorDataCount() );
        text.append( " GPS Data: " + LocalStorage.getInstance( context ).getGPSDataCount() );
    }

    /**
     * loads last 10 records from db
     */
    public void loadDataIntoTextView(String tablename) {
        try {

            JSONArray array = LocalStorage.getInstance( context ).getDataAsJsonArray( tablename, 10 );
            dbData.setText( array.toString( 2 ) );

            // Delete rows after selected
//            LocalStorage.getInstance( context ).deleteFromBetween( tablename,
//                    array.getJSONObject( array.length() -1 ).getString( "ID" ),
//                    array.getJSONObject( 0 ).getString( "ID" ) );

            LogService.log( "========= DATA ==========" );
            LogService.log( array.toString() );

            refreshData();
        } catch ( Exception ex ) {
            LogService.log( "Error setting json text: " + ex.getMessage() );
        }
    }



    @Override
    public void onClick( View v ) {
        if (v.getId() == R.id.button_delete_data)
            clearData();
        if (v.getId() == R.id.button_refresh)
            refreshData();
        if (v.getId() == R.id.button_loadgpsdata){
            loadDataIntoTextView( LocalStorage.TABLE_GPSData );
        }
        if (v.getId() == R.id.button_loadsensordata) {
            loadDataIntoTextView( LocalStorage.TABLE_SensorData );
        }
        if (v.getId() == R.id.button_request_sync) {
            ContentResolver.requestSync( SyncUtil.CURRENT_ACCOUNT, SyncUtil.AUTHORITY, Bundle.EMPTY );
            Toast.makeText( context, "Data Sync requested", Toast.LENGTH_SHORT ).show();
        }
    }
}
