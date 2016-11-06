package com.tuan.Estate_Value_Zillow;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


/**
 * A simple {@link Fragment} subclass.
 */
public class PropertyFragment extends Fragment implements OnMapReadyCallback{

    private final static String TAG = "PropertyFragment";

    private String streetAddress, city_state_zip, mHomeDetailURL;
    private LatLng latLng;
    private MapView mapView;
    private TextView price_tv;
    private TextView type_tv;
    private TextView lastSoldDate_tv;
    private TextView bed_tv;
    private TextView bath_tv;
    private TextView yearBuilt_tv;
    private TextView finishedSqft_tv;
    private TextView lastSoldPrice_tv;
    private TextView lotSize_tv;
    private TextView zindexvalue_tv;
    private TextView remodel_year_tv, basement_tv, roof_tv, view_tv, parking_type_tv,
            heating_source_tv, heating_system_tv, room_tv, home_desc_tv;

    private ProgressBar progressBar;

    private String zpid;

    private View rootView;
    private XMLParser xmlParser;

    public PropertyFragment() {
        // Required empty public constructor
        setArguments(new Bundle());
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_property, container, false);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        progressBar = (ProgressBar)rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        Bundle mBundle = this.getArguments();
        streetAddress = mBundle.getString("street_address");
        city_state_zip = mBundle.getString("city_state_zip");
        mHomeDetailURL = mBundle.getString("home_detail");
        zpid = mBundle.getString("zpid");
        latLng = mBundle.getParcelable("Lat_Lng");

        showActionBar();
        //show mapview
        mapView = (MapView) rootView.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        displayInfo(rootView);

        URL myURL = null;
        try {
            String encoded_address, encoded_citystatezip;
            encoded_address = URLEncoder.encode(streetAddress, "UTF-8");
            encoded_citystatezip = URLEncoder.encode(city_state_zip, "UTF-8");
            String PRO_HTTP = "http://www.zillow.com/webservice/GetDeepSearchResults.htm?";
            myURL = new URL(PRO_HTTP + "zws-id=X1-ZWz19l4idhcgsr_8gte4" +
                    "&address="+encoded_address + "&citystatezip="+encoded_citystatezip);
//            Log.i(TAG, myURL.toString());

        } catch (MalformedURLException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        PropertyDetailRequest request = new PropertyDetailRequest();
        request.execute(myURL);

        xmlParser = new XMLParser();
        Button compare_button = (Button) rootView.findViewById(R.id.compareButton);
        compare_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set zpid to XMLParser for collecting data
//                Log.i(TAG, "setting zpid to XMLParser: "+zpid);
                xmlParser.setZpid(zpid);

                CompareMapFragment nextFragment = new CompareMapFragment();
                Bundle arg = new Bundle();
                arg.putString("zpid", zpid);
                arg.putParcelable("Lat_Lng", latLng);
                nextFragment.setArguments(arg);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment, nextFragment).addToBackStack(null).commit();
            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        if (rootView.getParent() != null){
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
        super.onDestroyView();
    }

    private void displayInfo(View view) {

        TextView address_tv = (TextView) view.findViewById(R.id.address_tv);
        address_tv.setText(streetAddress);
        TextView cityState_tv = (TextView) view.findViewById(R.id.cityState_tv);
        cityState_tv.setText(city_state_zip);

        bed_tv = (TextView)view.findViewById(R.id.bedroom_tv);
        bath_tv = (TextView)view.findViewById(R.id.bathroom_tv);
        price_tv = (TextView)view.findViewById(R.id.price_tv);
        yearBuilt_tv = (TextView)view.findViewById(R.id.yearBuilt_tv);
        finishedSqft_tv = (TextView)view.findViewById(R.id.finishedSq_tv);
        type_tv = (TextView)view.findViewById(R.id.type_tv);
        lastSoldDate_tv = (TextView)view.findViewById(R.id.lastSoldDate_tv);
        lastSoldPrice_tv = (TextView)view.findViewById(R.id.lastSoldPrice_tv);
        lotSize_tv = (TextView)view.findViewById(R.id.lotSize_tv);
        zindexvalue_tv = (TextView)view.findViewById(R.id.zindexvalue_tv);
        //remodel_year_tv, basement_tv, roof_tv, view_tv, parking_type_tv,
        //heating_source_tv, heating_system_tv, room_tv, home_desc_tv;
        remodel_year_tv = (TextView)view.findViewById(R.id.remodel_tv);
        basement_tv = (TextView)view.findViewById(R.id.basement_tv);
        roof_tv = (TextView)view.findViewById(R.id.roof_tv);
        view_tv = (TextView)view.findViewById(R.id.view_tv);
        parking_type_tv = (TextView)view.findViewById(R.id.parking_type_tv);
        heating_source_tv = (TextView)view.findViewById(R.id.heating_source_tv);
        heating_system_tv = (TextView)view.findViewById(R.id.heating_system_tv);
        room_tv = (TextView)view.findViewById(R.id.room_tv);
        home_desc_tv = (TextView)view.findViewById(R.id.home_desc_tv);

    }

    @SuppressWarnings({"ConstantConditions", "deprecation"})
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showActionBar(){
        ActionBar mActionBar =  ((AppCompatActivity)getActivity()).getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
        mActionBar.setBackgroundDrawable(new ColorDrawable(getResources()
                    .getColor(R.color.myActionBarColor)));


        //set status bar color
        Window window = getActivity().getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(getResources().getColor(R.color.myStatusBarColor));
        mActionBar.setTitle(streetAddress);
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
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
        if (id == R.id.action_share){
//            Log.i(TAG, "click share button");
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(android.content.Intent.EXTRA_SUBJECT, "Sharing URL");
            share.putExtra(Intent.EXTRA_TEXT, mHomeDetailURL);

            getActivity().startActivity(Intent.createChooser(share, "Share Link!"));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

//        Log.i(TAG, "Mapview is running");
        googleMap.addMarker(new MarkerOptions().position(latLng));
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    private class PropertyDetailRequest extends AsyncTask<URL, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            String response = "";
            for (URL url: params){
                response = PropertyFragment.get(url);
            }

            return response;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Document doc = XMLFromString(result);

            //retrieve data from xml document
            NodeList bath_node = doc.getElementsByTagName("bathrooms");
            if (bath_node.getLength() > 0){
                String bathroom = bath_node.item(0).getTextContent();
                bath_tv.setText(bathroom);
            } else{
                bath_tv.setText("Unknown");
            }
            String z_estimate = doc.getElementsByTagName("amount").item(0).getTextContent();
            if (z_estimate.equals("")){
                price_tv.setText("$Unknown");
            } else {
                price_tv.setText("$"+z_estimate);
            }

            NodeList bed_node = doc.getElementsByTagName("bedrooms");
            if (bed_node.getLength() > 0){
                String bedroom = doc.getElementsByTagName("bedrooms").item(0).getTextContent();
                bed_tv.setText(bedroom);
            } else {
                bed_tv.setText("Unknown");
            }

            NodeList finished_sqft_node = doc.getElementsByTagName("finishedSqFt");
            if (finished_sqft_node.getLength() > 0){
                String finishedSqft = finished_sqft_node.item(0).getTextContent();
                finishedSqft_tv.setText(finishedSqft);
            } else {
                finishedSqft_tv.setText("Unknown");
            }

            NodeList property_type_node = doc.getElementsByTagName("useCode");
            if (property_type_node.getLength() > 0){
                String property_type = property_type_node.item(0).getTextContent();
                type_tv.setText("Property Type:   "+ property_type);
            }else {
                type_tv.setVisibility(View.GONE);
            }

            NodeList lot_node = doc.getElementsByTagName("lotSizeSqFt");
            if (lot_node.getLength() > 0){
                String lotSizeSqft = lot_node.item(0).getTextContent();
                lotSize_tv.setText("Lot Size in SqFt:   "+ lotSizeSqft);
            } else {
                lotSize_tv.setVisibility(View.GONE);
            }

            NodeList year_node = doc.getElementsByTagName("yearBuilt");
            if (year_node.getLength() > 0) {
                String yearBuilt = year_node.item(0).getTextContent();
                yearBuilt_tv.setText(yearBuilt);
            } else {
                yearBuilt_tv.setText("Unknown");
            }

            NodeList lastSoldDate_node = doc.getElementsByTagName("lastSoldDate");
            if (lastSoldDate_node.getLength() > 0) {
                String lastSoldDate = lastSoldDate_node.item(0).getTextContent();
                lastSoldDate_tv.setText("Last Sold Date:   " + lastSoldDate);
            } else {
                lastSoldDate_tv.setVisibility(View.GONE);
            }

            NodeList lastSoldPrice_node = doc.getElementsByTagName("lastSoldPrice");
            if (lastSoldPrice_node.getLength() > 0){
                String lastSoldPrice = lastSoldPrice_node.item(0).getTextContent();
                lastSoldPrice_tv.setText("Last Sold Price:   $" + lastSoldPrice);
            } else {
                lastSoldPrice_tv.setVisibility(View.GONE);
            }
            
            String zIndexValue = doc.getElementsByTagName("zindexValue").item(0).getTextContent();
            zindexvalue_tv.setText("  Zillow Index Value:   $"+zIndexValue);

            //parse another xml to retrieve images of given property
            URL imageUrl = null;
            try {
                String IMG_HTTP = "http://www.zillow.com/webservice/GetUpdatedPropertyDetails.htm?";
                imageUrl = new URL(IMG_HTTP + "zws-id=X1-ZWz19l4idhcgsr_8gte4" + "&zpid="+zpid);
//                Log.i(TAG, imageUrl.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            new PropertyImage().execute(imageUrl);
        }
    }

    private class PropertyImage extends AsyncTask<URL, Void, String>{

        @Override
        protected String doInBackground(URL... urls) {
            String response = "";
            for (URL url : urls){
                response = PropertyFragment.get(url);
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Document doc = XMLFromString(s);

            getMoreView(doc);
            getMoreView2(doc);

            NodeList img_node = doc.getElementsByTagName("url");
            int img_count = img_node.getLength();
//            Log.i(TAG, "num of img: " +img_count);
            if (img_count > 0) {
                for (int i=0; i < img_count; i++){
                    String img_url = img_node.item(i).getTextContent();
//                    Log.i(TAG, img_url);

                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    bmOptions.inSampleSize = 1;
                    final Bitmap bm = loadBitmap(img_url, bmOptions);

                    //add images to image view inside horizontal linear layout
                    //noinspection ConstantConditions
                    LinearLayout layout = (LinearLayout)getView().findViewById(R.id.img_container_layout);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams
                            (LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT);
                    ImageView imgView = new ImageView(getContext());
//                    imgView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    //imgView.setAdjustViewBounds(true);
                    imgView.setImageBitmap(bm);
                    layout.addView(imgView);
                    lp.setMargins(2, 0, 2, 0);
                    imgView.setId(i);
                    imgView.setMaxWidth(lp.width);
                    imgView.setMaxHeight(lp.height);
                    imgView.setLayoutParams(lp);
                }

            } else {
                //noinspection ConstantConditions
                HorizontalScrollView hsv = (HorizontalScrollView)getView()
                        .findViewById(R.id.imageListScrollView);
                hsv.setVisibility(View.GONE);
            }

            progressBar.setVisibility(View.GONE);
        }

        @SuppressLint("SetTextI18n")
        private void getMoreView(Document doc) {
            //heating_source_tv, heating_system_tv, room_tv, home_desc_tv;
            NodeList remodel_year_node = doc.getElementsByTagName("yearUpdated");
            if (remodel_year_node.getLength() > 0){
                String remodel_year = remodel_year_node.item(0).getTextContent();
                remodel_year_tv.setText("Remodel Year:   "+remodel_year);
            } else{
                remodel_year_tv.setVisibility(View.GONE);
            }

            NodeList basement_node = doc.getElementsByTagName("basement");
            if (basement_node.getLength() > 0){
                String basement = basement_node.item(0).getTextContent();
                basement_tv.setText("Basement:   "+basement);
            } else {
                basement_tv.setVisibility(View.GONE);
            }

            NodeList roof_node = doc.getElementsByTagName("roof");
            if (roof_node.getLength() > 0){
                String roof = roof_node.item(0).getTextContent();
                roof_tv.setText("Roof:   "+roof);
            } else {
                roof_tv.setVisibility(View.GONE);
            }

            NodeList view_node = doc.getElementsByTagName("view");
            if (view_node.getLength() > 0){
                String view = view_node.item(0).getTextContent();
                view_tv.setText("Roof:   "+view);
            } else {
                view_tv.setVisibility(View.GONE);
            }

            NodeList parking_node = doc.getElementsByTagName("parkingType");
            if (parking_node.getLength() > 0){
                String parking_type = parking_node.item(0).getTextContent();
                parking_type_tv.setText("Parking Type:   "+parking_type);
            } else {
                parking_type_tv.setVisibility(View.GONE);
            }
        }

        @SuppressLint("SetTextI18n")
        private void getMoreView2(Document doc) {

            //heating_source_tv, heating_system_tv, room_tv, home_desc_tv;
            NodeList heating_node = doc.getElementsByTagName("heatingSources");
            if (heating_node.getLength() > 0){
                String heating_source = heating_node.item(0).getTextContent();
                heating_source_tv.setText("Heating Source:   "+heating_source);
            } else{
                heating_source_tv.setVisibility(View.GONE);
            }

            NodeList heating_system_node = doc.getElementsByTagName("heatingSystem");
            if (heating_system_node.getLength() > 0){
                String heating_system = heating_system_node.item(0).getTextContent();
                heating_system_tv.setText("Heating System:   "+heating_system);
            } else {
                heating_system_tv.setVisibility(View.GONE);
            }

            NodeList room_node = doc.getElementsByTagName("rooms");
            if (room_node.getLength() > 0){
                String room = room_node.item(0).getTextContent();
                room_tv.setText("Rooms:   "+room);
            } else {
                room_tv.setVisibility(View.GONE);
            }

            NodeList home_desc_node = doc.getElementsByTagName("homeDescription");
            if (home_desc_node.getLength() > 0){
                String home_desc = home_desc_node.item(0).getTextContent();
                home_desc_tv.setText(home_desc);
            } else {
                home_desc_tv.setVisibility(View.GONE);
            }
        }

        private Bitmap loadBitmap(String img_url, BitmapFactory.Options bmOptions) {
            Bitmap bitmap = null;
            InputStream in;
            try {
                in = OpenHttpConnection(img_url);
                bitmap = BitmapFactory.decodeStream(in, null, bmOptions);
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        private InputStream OpenHttpConnection(String img_url) throws IOException {
            InputStream is = null;
            URL url = new URL(img_url);
            URLConnection conn = url.openConnection();

            try {
                HttpURLConnection httpConn = (HttpURLConnection) conn;
                httpConn.setRequestMethod("GET");
                httpConn.connect();

                if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK){
                    is = httpConn.getInputStream();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            return is;
        }
    }

    private Document XMLFromString(String result) {
        Document doc = null;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(result));
            doc = db.parse(is);

        } catch (ParserConfigurationException | IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            Log.e(TAG, "Wrong XML file structure: " + e.getMessage());
        }

        return doc;
    }

    private static String get(URL url) {

        String response = "";
        try {
            URLConnection conn = url.openConnection();
            BufferedInputStream bin = new BufferedInputStream(conn.getInputStream());

            byte[] contentBytes = new byte[1024];
            int bytesRead;
            StringBuilder sb = new StringBuilder();

//            Log.i(TAG, "starting to read data");
            while((bytesRead = bin.read(contentBytes)) != -1){
                response = new String(contentBytes, 0, bytesRead);
                sb.append(response);
            }
            return sb.toString();

        } catch (IOException e) {
            Log.e("URL RESPONSE ERROR", "Internet#get");
        }
        return response;
    }
}
