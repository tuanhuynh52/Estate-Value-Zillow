package com.tuan.Estate_Value_Zillow;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener, ResultCallback<LocationSettingsResult> {

    private final static String TAG = "MainActivityFragment";
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private String streetAddress, cityStateZip;
    private LatLng latLng;
    private Location mLocation;

    ActionBar mActionBar;
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest locationRequest;
    int REQUEST_CHECK_SETTINGS = 100;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_main, container, false);
        mActionBar =  ((AppCompatActivity)getActivity()).getSupportActionBar();
        assert mActionBar != null;
        mActionBar.setTitle("Home");
        setHasOptionsMenu(true);
        return v;
    }

    @SuppressWarnings("deprecation")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        ActionBar mActionBar =  ((AppCompatActivity)getActivity()).getSupportActionBar();
        assert mActionBar != null;
        mActionBar.setDisplayHomeAsUpEnabled(false);

        mActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        Window window = getActivity().getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_item, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                //search using placeautocomplete intent
                //check if device is connected to the internet
                ConnectivityManager cm = (ConnectivityManager)getActivity()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                boolean connected;
                connected = networkInfo != null && networkInfo.isConnected();
                if (connected){
                    try {
                        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                                .build();
                        Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                .setFilter(typeFilter)
                                .build(getActivity());
                        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                        Log.d("MainActivity", e.toString());
                    }
                } else {
                    Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_LONG).show();
                }
                return true;

            case R.id.action_current_location:
                //get current location for latlng after check connection succeeded
                if (isConnected()) {
                    getCurrentLocation();
                }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getCurrentLocation() {
        if (checkPermission()){
            final LocationManager locationManager = (LocationManager)
                    getActivity().getSystemService(Context.LOCATION_SERVICE);
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    mLocation = location;
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    Log.i(TAG, "location status changed");
                }

                @Override
                public void onProviderEnabled(String provider) {
                    Log.i(TAG, "location provider enabled");
                }

                @Override
                public void onProviderDisabled(String provider) {
                    Log.i(TAG, "location provider disabled");
                }
            };
            // Check whether if the GPS provider exists and if it is enabled in the device
            // If the GPS is enabled, request location updates.
            if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)
                    && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                // request location update with the location listener to use onLocationChanged(),
                // given the name of the provider, minimum time interval as 5 seconds,
                // and minimum distance as 10 meters between location updates
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        5000, 10, locationListener);
            }
            mLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//            Toast.makeText(getActivity(), "location: "+mLocation, Toast.LENGTH_LONG).show();
            //convert latlng to complete address
            if (mLocation != null) {
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(getActivity(), Locale.getDefault());
                try {
                    //set latlng
                    latLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());

                    addresses = geocoder.getFromLocation(mLocation.getLatitude(),
                            mLocation.getLongitude(), 1);
                    streetAddress = addresses.get(0).getAddressLine(0);
                    String encoded_street = URLEncoder.encode(streetAddress, "UTF-8");
//                    Log.i(TAG, encoded_street);

                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String zip = addresses.get(0).getPostalCode();
                    cityStateZip = city +" "+ state + " "+zip;
//                    Log.i(TAG, cityStateZip);
                    String encoded_cityStateZip = URLEncoder.encode(cityStateZip, "UTF-8");

                    //parsing xml
                    XmlParserForZpid(encoded_street, encoded_cityStateZip);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getActivity(), "GPS is not enabled", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean checkPermission() {
        int gpsPermission = ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (gpsPermission == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "permissions granted");
            return true;
        } else {
            Log.i(TAG, "Device location access permission was denied");
            String[] permission = {android.Manifest.permission.ACCESS_FINE_LOCATION};
            // request for device location permission with a result id code as 0
            ActivityCompat.requestPermissions(getActivity(), permission, 0);
            return false;
        }
    }

    private boolean isConnected() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);

        //To get started the application, it requires that you have an internet connection
        final ConnectivityManager cm = (ConnectivityManager)getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork;
        activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        builder.build()
                );

        result.setResultCallback(this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                // NO need to show the dialog;
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                //  Location settings are not satisfied. Show the user a dialog
                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().

                    status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);

                } catch (IntentSender.SendIntentException e) {
                    //failed to show
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                // Location settings are unavailable so not possible to show any dialog now
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
//                Log.i(TAG, "Place: " + place.getAddress());
                latLng = place.getLatLng();

                String address = place.getAddress().toString();
                //split address into streetAdress and cityStateZip
                String[] splitAddress = address.split(",");
                streetAddress = splitAddress[0];
                String city = splitAddress[1].trim();
                String stateZip = splitAddress[2];
                cityStateZip = city + stateZip;
//                Log.i(TAG, "street address: " + streetAddress);
//                Log.i(TAG, "city state zip: " + cityStateZip);

                /**
                 * parse xml to retrieve property ID from zillow
                 */

                //encode values
                String encoded_address, encoded_citystatezip;
                try {
                    encoded_address = URLEncoder.encode(streetAddress, "UTF-8");
//                    Log.i(TAG, encoded_address);
                    encoded_citystatezip = URLEncoder.encode(cityStateZip, "UTF-8");
//                    Log.i(TAG, encoded_citystatezip);

                    XmlParserForZpid(encoded_address, encoded_citystatezip);

                } catch (UnsupportedEncodingException e) {
                    Log.e("BAD URL", "ENCODING PROBLEM");
                }

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                Log.i(TAG, "search has been cancelled");
            }
        }
    }

    private void XmlParserForZpid(String address, String cityStateZip) {
        //execute XML parser
        try {
            String DEEP_HTTP = "http://www.zillow.com/webservice/GetSearchResults.htm";
            String ZWS_ID = "X1-ZWz19l4idhcgsr_8gte4";
            URL myURL = new URL(DEEP_HTTP + "?zws-id="+ ZWS_ID + "&address="+address
                    + "&citystatezip="+cityStateZip);
//                        Log.i(TAG, myURL.toString());
            ZillowRequest zr = new ZillowRequest();
            zr.execute(myURL);
        } catch (MalformedURLException e){
            e.printStackTrace();
        }
    }

    private static String get(URL url) {
        String response = "";
//        Log.i("TRACE", "trying to get thing");
        try {
            URLConnection conn = url.openConnection();
//            Log.i("TRACE", "opened connection");
            BufferedInputStream bin = new BufferedInputStream(conn.getInputStream());
//            Log.i("TRACE", "got buffered stream");

            byte[] contentBytes = new byte[1024];
            int bytesRead;
            StringBuilder stringBuffer = new StringBuilder();

//            Log.i("TRACE", "starting to read data");
            while((bytesRead = bin.read(contentBytes)) != -1) {
                response = new String(contentBytes, 0, bytesRead);
                stringBuffer.append(response);
            }
//            Log.i("TRACE", "done with data");
            return stringBuffer.toString();
        } catch (Exception e) {
            Log.e("URL RESPONSE ERROR", "Internet#get");
        }

        return response;
    }

    private class ZillowRequest extends AsyncTask<URL, Void, String>{
        @Override
        protected String doInBackground(URL... params) {

            String reponse = "";
            for (URL url: params) {
                //make the request
                reponse = MainActivityFragment.get(url);
            }
            return reponse;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
//            Log.i("URL RESPONSE", result);
            //require device connected to the internet

            Document doc = XMLfromString(result);
            NodeList parse_successful_code = doc.getElementsByTagName("code");
            if (parse_successful_code.item(0).getTextContent().equals("0")){
                String zpid = doc.getElementsByTagName("zpid").item(0).getTextContent();
//                Log.i(TAG, "ZPID: "+zpid);
                String homeDetail = doc.getElementsByTagName("homedetails").item(0).getTextContent();

                //passing data to property fragment
                PropertyFragment myFragment = new PropertyFragment();
                Bundle b = new Bundle();
                b.putString("street_address", streetAddress);
                b.putString("city_state_zip", cityStateZip);
                b.putString("zpid", zpid);
                b.putString("home_detail", homeDetail);
                //send latlng to show info on mapview
                b.putParcelable("Lat_Lng", latLng);
                myFragment.setArguments(b);

                //start new fragment with data
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment, myFragment).addToBackStack(null).commit();

            } else {
                Toast.makeText(getActivity(), "No match found for this address",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public Document XMLfromString(String v){

        Document doc = null;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {

            DocumentBuilder db = dbf.newDocumentBuilder();

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(v));
            doc = db.parse(is);

        } catch (ParserConfigurationException | IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            System.out.println("Wrong XML file structure: " + e.getMessage());
            return null;
        }

        return doc;
    }
}
