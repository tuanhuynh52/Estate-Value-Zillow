package com.tuan.Estate_Value_Zillow;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Tuan on 10/28/2016.
 *
 */
class XMLParser {

    private static final String TAG = "XMLParser";
    private static final String COMPARE_HTTP = "http://www.zillow.com/webservice/GetDeepComps.htm?zws-id=";
    private static final String ZWS_ID = "X1-ZWz19l4idhcgsr_8gte4";
    private static String myZpid;

    void setZpid(String zpid) {
        XMLParser.myZpid = zpid;
    }

    List<PropertyDetail> getPropertyList() {
//        Log.i(TAG, "getPropertyList running");

        List<PropertyDetail> propertyList = new ArrayList<>();

        URL url;
        try {
            url = new URL(COMPARE_HTTP + ZWS_ID + "&zpid="+myZpid+ "&count=25");

            //Log.i(TAG, url.toString());

            String response = get(url);
            Document doc = XMLFromString(response);
            NodeList zpid_node = doc.getElementsByTagName("zpid");
            NodeList street_node = doc.getElementsByTagName("street");
            NodeList zip_node = doc.getElementsByTagName("zipcode");
            NodeList city_node = doc.getElementsByTagName("city");
            NodeList state_node = doc.getElementsByTagName("state");
            NodeList lat_node = doc.getElementsByTagName("latitude");
            NodeList lng_node = doc.getElementsByTagName("longitude");
//            NodeList yearBuilt_node = doc.getElementsByTagName("yearBuilt");
//            NodeList size_node = doc.getElementsByTagName("finishedSqFt");
//            NodeList bath_node = doc.getElementsByTagName("bathrooms");
//            NodeList bed_node = doc.getElementsByTagName("bedrooms");
            NodeList price_node = doc.getElementsByTagName("amount");

            int property_count = lat_node.getLength();

            for (int i = 0; i < property_count; i++){
                String zpid = zpid_node.item(i+1).getTextContent();

                Double lat = Double.valueOf(lat_node.item(i).getTextContent());
                Double lng = Double.valueOf(lng_node.item(i).getTextContent());
                //Log.i(TAG, "Latlng is: " +lat+","+lng);
                String street = street_node.item(i).getTextContent();
                String zip = zip_node.item(i).getTextContent();
                String city = city_node.item(i).getTextContent();
                String state = state_node.item(i).getTextContent();
//                String yearBuild = yearBuilt_node.item(i).getTextContent();
//                String finishedSqft = size_node.item(i).getTextContent();
//                String bathroom = bath_node.item(i).getTextContent();
//                String bedroom = bed_node.item(i).getTextContent();
                String zEstimate = price_node.item(i).getTextContent();
                if (zEstimate.equals("")){
                    zEstimate = "Unknown";
                }

                PropertyDetail propertyDetail = new PropertyDetail(zpid, street, city, state, zip,
                        lng, lat, zEstimate);
                propertyList.add(propertyDetail);
            }

            return propertyList;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
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
