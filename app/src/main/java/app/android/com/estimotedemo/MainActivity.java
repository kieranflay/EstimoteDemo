package app.android.com.estimotedemo;

import android.app.Activity;
import android.graphics.Region;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        beacons();
    }

    private void beacons() {

        private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
        private static final Region ALL_ESTIMOTE_BEACONS = new Region("regionId", ESTIMOTE_PROXIMITY_UUID, null, null);

        private final BeaconManager beaconManager = new BeaconManager(context);

        // Should be invoked in #onCreate.
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
                Log.d(TAG, "Ranged beacons: " + beacons);
            }
        });

        // Should be invoked in #onStart.
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override public void onServiceReady() {
                try {
                    beaconManager.startRanging(ALL_ESTIMOTE_BEACONS);
                } catch (RemoteException e) {
                    Log.e(TAG, "Cannot start ranging", e);
                }
            }
        });

        // Should be invoked in #onStop.
        try {
            beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS);
        } catch (RemoteException e) {
            Log.e(TAG, "Cannot stop but it does not matter now", e);
        }

        // When no longer needed. Should be invoked in #onDestroy.
        beaconManager.disconnect();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
