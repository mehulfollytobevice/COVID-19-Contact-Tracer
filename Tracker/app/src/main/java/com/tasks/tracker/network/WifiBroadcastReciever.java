package com.tasks.tracker.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import com.tasks.tracker.TrackerActivity;

public class WifiBroadcastReciever extends BroadcastReceiver {
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private TrackerActivity activity;


    public WifiBroadcastReciever(WifiP2pManager manager, WifiP2pManager.Channel channel, TrackerActivity activity) {
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        Log.d("WifiDirectAction", "onReceive: "+"Action");

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Determine if Wifi P2P mode is enabled or not, alert
            // the Activity.

            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);

            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Log.d("WIFIDIRECT", "onReceive: "+"Wifi is ON");
                Toast.makeText(context, "Wifi is ON", Toast.LENGTH_SHORT).show();
            }
            else {
                Log.d("WIFIDIRECT", "onReceive: "+"Wifi is OFF");
                Toast.makeText(context, "Wifi is OFF", Toast.LENGTH_SHORT).show();
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            // The peer list has changed! We should probably do something about
            // that.

            if (manager!=null){
                manager.requestPeers(channel,activity.peerListListener);
            }

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            // Connection state changed! We should probably do something about
            // that.

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {


        }
    }
}
