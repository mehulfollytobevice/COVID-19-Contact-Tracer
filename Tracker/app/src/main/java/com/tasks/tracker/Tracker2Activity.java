package com.tasks.tracker;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.api.Endpoint;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tasks.tracker.Service.LocationTrack;
import com.tasks.tracker.model.Log_details;
import com.tasks.tracker.util.TrackerApi;

import java.security.Permission;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class Tracker2Activity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks,LocationListener {

    private static final String TAG = "TrackerTAG";
    private static final String SERVICE_ID = "Tracker";
    private static final long FASTEST_INTERVAL = 5000;// info: this is in milliseconds
    private static final long UPDATE_INTERVAL = 5000;

    private TrackerApi trackerApi = TrackerApi.getInstance();
    private String tracker_username;


    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;


    private Animation anim;
    private TextView connection_status;
    private Button discover_devices;
    private Button stop_discover;

    private static final int ALL_PERMISSION_RESULT = 1111;
    private ArrayList<String> permissionToRequest;
    private ArrayList<String> permissions = new ArrayList<>();
    private ArrayList<String> permissionRejected = new ArrayList<>();


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private CollectionReference log_details_collection = db.collection(TrackerApi.getInstance().getUsername());
    private CollectionReference details_collection = db.collection("UserDetails");



    private LocationTrack locationTrack;
    private double longitude;
    private double latitude;


    private EndpointDiscoveryCallback endpointDiscoveryCallback = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(@NonNull String s, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
//            Nearby.getConnectionsClient(Tracker2Activity.this)
//                    .requestConnection(tracker_username, SERVICE_ID, connectionLifecycleCallback)
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//
//                        }
//                    });
            Toast.makeText(Tracker2Activity.this, " Discovered:" + discoveredEndpointInfo.getEndpointName(), Toast.LENGTH_SHORT).show();
//            Log.d(TAG, "onEndpointFound: "+devices_name_list);
            details_collection.whereEqualTo("username", discoveredEndpointInfo.getEndpointName())
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Toast.makeText(Tracker2Activity.this, "Some error has come", Toast.LENGTH_SHORT).show();
                                return;
//                                some error
                            }

                            assert queryDocumentSnapshots != null;
                            if (!queryDocumentSnapshots.isEmpty()) {
                                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {

                                    Log_details log_details = new Log_details();
                                    log_details.setDate_crossed(new Timestamp(new Date()));
                                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling
                                        //    Activity#requestPermissions
                                        // here to request the missing permissions, and then overriding
                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                        //                                          int[] grantResults)
                                        // to handle the case where the user grants the permission. See the documentation
                                        // for Activity#requestPermissions for more details.
                                        return;
                                    }

                                    GeoPoint geoPoint = new GeoPoint(latitude, longitude);
                                    log_details.setLocation(geoPoint);
                                    log_details.setPhone_number(snapshot.getString("phone_number"));
                                    log_details.setUsername(snapshot.getString("username"));
                                    log_details.setName(snapshot.getString("name_user"));

                                    log_details_collection.add(log_details)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Toast.makeText(Tracker2Activity.this, "Logged", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(Tracker2Activity.this, "Error", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                }
                            }
                        }
                    });


        }

        @Override
        public void onEndpointLost(@NonNull String s) {
            // A previously discovered endpoint has gone away.

        }
    };

    private ConnectionLifecycleCallback connectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(@NonNull String s, @NonNull ConnectionInfo connectionInfo) {
//            Nearby.getConnectionsClient(Tracker2Activity.this).acceptConnection(SERVICE_ID, payloadCallback);

        }

        @Override
        public void onConnectionResult(@NonNull String s, @NonNull ConnectionResolution connectionResolution) {

            switch (connectionResolution.getStatus().getStatusCode()) {

                case ConnectionsStatusCodes.STATUS_OK:
                    // We're connected! Can now start sending and receiving data.
                    break;
                case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                    // The connection was rejected by one or both sides.
                    break;
                case ConnectionsStatusCodes.STATUS_ERROR:
                    // The connection broke before it was able to be accepted.
                    break;
                default:
                    // Unknown status code
            }
        }

        @Override
        public void onDisconnected(@NonNull String s) {
            // We've been disconnected from this endpoint. No more data can be
            // sent or received.
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker2);



//        locationListener=new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//                log_location=new Location(location);
//                Log.d(TAG, "onLocationChanged: "+location.toString());
//            }
//
//            @Override
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//
//            }
//
//            @Override
//            public void onProviderEnabled(String provider) {
//
//            }
//
//            @Override
//            public void onProviderDisabled(String provider) {
//
//            }
//        };

        connection_status=findViewById(R.id.connection_status);
        discover_devices=findViewById(R.id.discover_devices);
        stop_discover=findViewById(R.id.stop_discover_button);
        stop_discover.setEnabled(false);

        googleApiClient=new GoogleApiClient.Builder(this).addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(Tracker2Activity.this);


        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();


//        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    Activity#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for Activity#requestPermissions for more details.
//            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION },1);
//        }else{
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
////            Although Runtime exception is being thrown , it still gives location every second
////            Runtime exception does not matter
//        }

        tracker_username=trackerApi.getUsername();

        permissions.add(Manifest.permission.BLUETOOTH);
        permissions.add(Manifest.permission.BLUETOOTH_ADMIN);
        permissions.add(Manifest.permission.ACCESS_WIFI_STATE);
        permissions.add(Manifest.permission.CHANGE_WIFI_STATE);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionToRequest=permissionToRequest(permissions);



        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(permissionToRequest.size()>0){
                requestPermissions(permissionToRequest.toArray(new String[permissionToRequest.size()]),ALL_PERMISSION_RESULT);
            }
        }


        locationTrack = new LocationTrack(Tracker2Activity.this);
        if (locationTrack.canGetLocation()) {

//                    Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
        } else {

            locationTrack.showSettingsAlert();
        }



//        discover_devices.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (advertise_discover==0){
//                    startAdvertising();
//                    advertise_discover=1;
//                }
//                else if (advertise_discover==1){
//                    startDiscovering();
//                    advertise_discover=0;
//                }
//            }
//        });

//        discover_devices.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startAdvertising();
//                startDiscovering();
//                discover_devices.setEnabled(false);
//            }
//
//        });


        discover_devices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {





//                runAnimation();
//                blink();
                startBlinkText();
                startAdvertising();
                startDiscovering();
                connection_status.setText("I am Visible");
                discover_devices.setEnabled(false);
                stop_discover.setEnabled(true);
            }
        });

        stop_discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAdvertising();
                stopBlinkText();
                stopDiscovering();
                discover_devices.setEnabled(true);
                stop_discover.setEnabled(false);
                connection_status.setText("I am Invisible");
            }
        });



//        discover_devices.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(Tracker2Activity.this,LoginActivity.class));
//                finish();
//            }
//        });



    }


    public void startBlinkText() {
        TextView myText = (TextView) findViewById(R.id.connection_status);
        anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500); //You can manage the time of the blink with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        myText.startAnimation(anim);
    }

    public void stopBlinkText() {
        try {
            // TextView myText = (TextView) findViewById(R.id.state);
            anim.cancel();
            anim.reset();
            // myText.startAnimation(anim);
        } catch (Exception e) {
            Log.e(TAG,
                    "stopBlinkText method cannot be processed", e);
            e.printStackTrace();
        }
    }

    private void blink(){
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToBlink = 500;    //in milissegunds
                try{Thread.sleep(timeToBlink);}catch (Exception e) {}
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        TextView txt = (TextView) findViewById(R.id.connection_status);
                        if(txt.getVisibility() == View.VISIBLE){
                            txt.setVisibility(View.INVISIBLE);
                        }else{
                            txt.setVisibility(View.VISIBLE);
                        }
                        blink();
                    }
                });
            }
        }).start();
    }
    private void runAnimation() {
        Animation a = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        a.reset();
        TextView tv = (TextView) findViewById(R.id.connection_status);
        tv.clearAnimation();
        tv.startAnimation(a);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_signout:
//                Todo:Signout of the journal
                if (user!=null && firebaseAuth!=null){
                    firebaseAuth.signOut();//signing out of the firebase
                    startActivity(new Intent(Tracker2Activity.this,MainActivity.class));
                    finish();
                }
                break;

        }
        return super.onOptionsItemSelected(item);

    }

    private void stopDiscovering() {
        Nearby.getConnectionsClient(Tracker2Activity.this)
                .stopDiscovery();
        Toast.makeText(Tracker2Activity.this, "Stop Discovering", Toast.LENGTH_SHORT).show();
    }

    private void stopAdvertising() {
        Nearby.getConnectionsClient(Tracker2Activity.this)
                .stopAdvertising();
        Toast.makeText(Tracker2Activity.this, "Stop Advertising", Toast.LENGTH_SHORT).show();
    }

    private void startDiscovering() {
        DiscoveryOptions discoveryOptions =
                new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build();
        Nearby.getConnectionsClient(Tracker2Activity.this)
                .startDiscovery(SERVICE_ID,endpointDiscoveryCallback,discoveryOptions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Tracker2Activity.this, "Discovering", Toast.LENGTH_SHORT).show();
// WE are discovering
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Tracker2Activity.this, "Not discovering", Toast.LENGTH_SHORT).show();
// unable to discover
                    }
                });

    }

    private void startAdvertising() {

        AdvertisingOptions advertisingOptions =
                new AdvertisingOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build();
        Nearby.getConnectionsClient(Tracker2Activity.this)
                .startAdvertising(tracker_username
                        , SERVICE_ID, connectionLifecycleCallback, advertisingOptions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                         We are advertising

                        Toast.makeText(Tracker2Activity.this, "Advertising", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        Unable to advertise
                        Toast.makeText(Tracker2Activity.this, "Not advertising", Toast.LENGTH_SHORT).show();
                    }
                });


    }




    private ArrayList<String> permissionToRequest(ArrayList<String> wanted_permissions) {
        ArrayList<String> topermit=new ArrayList<>();
        for(String perm: wanted_permissions){
            if(!hasperm(perm)){
                topermit.add(perm);
            }
        }
        return topermit;
    }

    private boolean hasperm(String perm) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
            return checkSelfPermission(perm)== PackageManager.PERMISSION_GRANTED;
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

//        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
////                this will listen to the updates in the location
////                first time the requests are granted
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
//            }
//        }

        switch (requestCode){
            case ALL_PERMISSION_RESULT:
                for(String perm:permissionToRequest){
                    if(!hasperm(perm)){
                        permissionRejected.add(perm);
                    }
                }
                if(permissionRejected.size()>0){
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                        if(shouldShowRequestPermissionRationale(permissionRejected.get(0))){
//                            we dont need all permissions
                            new AlertDialog.Builder(Tracker2Activity.this)
                                    .setMessage("These permissions are mandatory")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                                                requestPermissions(permissionRejected.toArray(new String[permissionRejected.size()]),ALL_PERMISSION_RESULT);
                                            }
                                        }
                                    }).setNegativeButton("Cancel",null).create().show();
                        }
                    }

                }else{
                    if (googleApiClient!=null){
                        googleApiClient.connect();
                    }
                }
                break;

        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        stopAdvertising();
        stopDiscovering();
        googleApiClient.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        locationTrack.stopListener();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location!=null){
            latitude=location.getLatitude();
            longitude=location.getLongitude();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

//        permission check
        if (ActivityCompat.checkSelfPermission(Tracker2Activity.this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(Tracker2Activity.this,Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            return;
        }

//        getting last location of the client
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location!=null){
                            latitude=location.getLatitude();
                            longitude=location.getLongitude();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Tracker2Activity.this, "Some problem has occured ", Toast.LENGTH_SHORT).show();
                    }
                });
        startLocationUpdates();

    }

    private void startLocationUpdates() {

//        Here we get the location updates

//        info: making location request and passing the various attributes of the request
        locationRequest=new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setInterval(UPDATE_INTERVAL);

        if (ActivityCompat.checkSelfPermission(Tracker2Activity.this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(Tracker2Activity.this,Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){

            Toast.makeText(this, "Please Enable Location", Toast.LENGTH_SHORT).show();
        }
//        info: passing in the
        LocationServices.getFusedLocationProviderClient(Tracker2Activity.this).requestLocationUpdates(locationRequest,new LocationCallback(){
            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                if (locationResult!=null){
                    Location location= locationResult.getLastLocation();
                    latitude=location.getLatitude();
                    longitude=location.getLongitude();
                }
            }
        },null);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient!=null){
            googleApiClient.connect();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

//        whether the google service are available or not

        int error= GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(Tracker2Activity.this);

        if (error!=ConnectionResult.SUCCESS){
//            info: if the connection is not successful then the following error dialog is presented
            Dialog dialog=GoogleApiAvailability.getInstance().getErrorDialog(this, error, error, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    Toast.makeText(Tracker2Activity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                }
            });
            dialog.show();
        }
        else {
            Toast.makeText(Tracker2Activity.this, "All is Good ", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (googleApiClient!=null && googleApiClient.isConnected()){
            LocationServices.getFusedLocationProviderClient(Tracker2Activity.this).removeLocationUpdates(new LocationCallback());
            googleApiClient.disconnect();
        }
    }
}