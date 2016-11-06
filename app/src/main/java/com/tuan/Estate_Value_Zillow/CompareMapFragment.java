package com.tuan.Estate_Value_Zillow;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CompareMapFragment extends Fragment implements OnMapReadyCallback{

    private static final String TAG = "CompareMapFragment";
    private MapView compareMapView;
    private GoogleMap map;
    private LatLng latLng;
    private ProgressBar mProgressbar;

    public CompareMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_compare_map, container, false);

        showActionBar();
        mProgressbar = (ProgressBar)view.findViewById(R.id.mapProgressBar);
        mProgressbar.setVisibility(View.VISIBLE);

        Bundle b = this.getArguments();
        latLng = b.getParcelable("Lat_Lng");

        //show mapview
        compareMapView = (MapView) view.findViewById(R.id.compare_mapview);
        compareMapView.onCreate(savedInstanceState);
        compareMapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "Mapview is running");
        //show myhome marker on map
//        MarkerOptions markerOptions = new MarkerOptions()
//                .position(latLng).title("300K").visible(true);
//        Marker marker = googleMap.addMarker(markerOptions);
//        marker.showInfoWindow();
        map = googleMap;
        new SearchTask().execute();
    }


    @Override
    public void onResume() {
        super.onResume();
        compareMapView.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        compareMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compareMapView.onDestroy();
    }

    @SuppressWarnings("deprecation")
    private void showActionBar() {
        ActionBar mActionBar =  ((AppCompatActivity)getActivity()).getSupportActionBar();
        assert mActionBar != null;
        mActionBar.setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);

        mActionBar.setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.myMapActionBarColor)));

        //set status bar color
        Window window = getActivity().getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(getResources().getColor(R.color.mayMapStatusBarColor));
        mActionBar.setTitle("Neighborhood Values");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_map, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
//            Log.i(TAG, "click back button");
            FragmentManager fm = getFragmentManager();
            if (fm.getBackStackEntryCount() > 0) {
                fm.popBackStack();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class SearchTask extends AsyncTask<String, String, List<PropertyDetail>>{
//        private List<PropertyDetail> parkingList;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<PropertyDetail> doInBackground(String... params) {

            Log.i(TAG, "Running DoinBackground");
            return new XMLParser().getPropertyList();
        }

        @Override
        protected void onPostExecute(List<PropertyDetail> propertyDetails) {
            super.onPostExecute(propertyDetails);
            Log.i(TAG, "Running onPostExecute");

            IconGenerator iconFactory = new IconGenerator(getActivity());
            iconFactory.setColor(Color.WHITE);
            if (propertyDetails != null) {

               for (int i = 0; i < propertyDetails.size(); i++){
                   Double lat = propertyDetails.get(i).getLat();
                   Double lng = propertyDetails.get(i).getLng();
                   LatLng position = new LatLng(lat, lng);

                   String street = propertyDetails.get(i).getStreet();
//                   String city = propertyDetails.get(i).getCity();
//                   String state = propertyDetails.get(i).getState();
//                   String zip = propertyDetails.get(i).getZipcode();

//                   Log.i(TAG, "mylatlng: " + position);
                   String zEstimate = propertyDetails.get(i).getzEstimate();
                   addCustomMarker(iconFactory, zEstimate, street, position);
               }
                map.getUiSettings().setZoomControlsEnabled(true);
                map.getUiSettings().setCompassEnabled(true);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            } else {
                Toast.makeText(getActivity(), "No Neighborhood data found!",
                        Toast.LENGTH_LONG).show();
            }


            mProgressbar.setVisibility(View.GONE);
        }

        //make custom marker with price label for map
        private void addCustomMarker(IconGenerator iconGenerator, CharSequence price,
                                     String address, LatLng position){
            MarkerOptions markerOptions = new MarkerOptions().
                    icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon("$"+price))).
                    position(position).
                    title(address).
                    anchor(iconGenerator.getAnchorU(), iconGenerator.getAnchorV());
            map.addMarker(markerOptions);
        }
    }
}
