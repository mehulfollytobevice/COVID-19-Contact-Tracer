package com.tasks.tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tasks.tracker.network.WifiBroadcastReciever;

import java.util.ArrayList;
import java.util.List;

public class TrackerActivity extends AppCompatActivity  {
//    Todo: if the user has not entered details do not allow to track
//    todo: create a textview to display user details also

    private ListView discovery_details;
    private TextView connection_status;
    private Button wifion;
    private Button discover;


    private WifiManager manager;
    public WifiP2pManager wifiP2pManager;
    public WifiP2pManager.Channel channel;
    public BroadcastReceiver broadcastReceiver;
    public IntentFilter intentFilter;


    private List<WifiP2pDevice> peers=new ArrayList<WifiP2pDevice>();
    private String[] devicenameArray;
    private WifiP2pDevice[] deviceArray;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);
        initialWork();
        exqlistener();



//
//        manager=(WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE);
//        channel=manager.initialize(this,getMainLooper(),null);

    }

    private void exqlistener() {
        wifion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (manager.isWifiEnabled()){
                    manager.setWifiEnabled(false);
                    wifion.setText("On");
                }
                else {
                    manager.setWifiEnabled(true);
                    wifion.setText("OFF");
                }
            }
        });

        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        connection_status.setText("Discovery Started");
                    }

                    @Override
                    public void onFailure(int reason) {
                        connection_status.setText("Discovery Failed");
                        Log.d("REASON", "onFailure: "+reason);
                    }
                });
            }
        });
    }

    private void initialWork() {
        connection_status=findViewById(R.id.listen_track);
        discovery_details=findViewById(R.id.details_devices);
        wifion=findViewById(R.id.wifi_on_button);
        discover=findViewById(R.id.discover_devices_button);

        manager=(WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiP2pManager=(WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE);
        assert wifiP2pManager != null;
        channel=wifiP2pManager.initialize(this,getMainLooper(),null);

        broadcastReceiver=new WifiBroadcastReciever(wifiP2pManager,channel,this);
        intentFilter=new IntentFilter();

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
//        info: Indicates whether Wi-Fi P2P is enabled

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
//        info: Indicates that the available peer list has changed.

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
//        info:Indicates the state of Wi-Fi P2P connectivity has changed. Starting with Android 10, this is not sticky.
//         If your app has relied on receiving these broadcasts at registration because they had been sticky,
//         use the appropriate get method at initialization to obtain the information instead.

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
//        info:Indicates this device's configuration details have changed.
//         Starting with Android 10, this is not sticky.
//         If your app has relied on receiving these broadcasts at registration because they had been sticky,
//         use the appropriate get method at initialization to obtain the information instead.

    }

    public WifiP2pManager.PeerListListener peerListListener=new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            if (!peerList.getDeviceList().equals(peers)){
                peers.clear();
                peers.addAll(peerList.getDeviceList());

                devicenameArray=new String[peerList.getDeviceList().size()];
                deviceArray=new WifiP2pDevice[peerList.getDeviceList().size()];
                int index=0;

                for (WifiP2pDevice device:peerList.getDeviceList()){
                    devicenameArray[index]=device.deviceName;
                    deviceArray[index]=device;
                    index++;

                }

                ArrayAdapter<String> adapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,devicenameArray);
                discovery_details.setAdapter(adapter);
            }
            if (peers.size()==0){
                Toast.makeText(TrackerActivity.this, "No Devices discovered", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    };



    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver,intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
}
