package edu.psu.team3.app.awayteam;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MapFragment extends Fragment implements OnInfoWindowClickListener {

	private LatLng pos;
	private GoogleMap map;
	private FourSquareTask fqT;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		

		

		View rootView = inflater.inflate(R.layout.fragment_map,
				container, false);
		
		rootView.findViewById(R.id.findSpots).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						fqT = new FourSquareTask();
				        fqT.execute();
					}
				});

		
		//com.google.android.gms.maps.MapFragment MapFragment;
		
        // Get a handle to the Map Fragment
         map = ((com.google.android.gms.maps.MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapview_fragment)).getMap();

        String locationProvider = LocationManager.NETWORK_PROVIDER;
     // Or use LocationManager.GPS_PROVIDER

        LocationManager mgr = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        Location lastKnownLocation = mgr.getLastKnownLocation(locationProvider);

         pos = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        
        
       // LatLng sydney = new LatLng(-33.867, 151.206);

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 10));
        map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);


//        map.addMarker(new MarkerOptions()
//                .title("Sydney")
//                .snippet("The most populous city in Australia.")
//                .position(sydney));
        map.setOnInfoWindowClickListener(this);

		return rootView;
	}
	


	public class FourSquareTask extends AsyncTask<Object, Void, JSONObject> {

		@Override
		protected JSONObject  doInBackground(Object... params) {
			//UserSession s = UserSession.getInstance(getActivity());
			// dispatch the login method

			JSONObject result;
			String ll = pos.latitude + ", " +  pos.longitude;
			UserSession s = UserSession.getInstance(getActivity());
			String user = s.getUsername();

			result = CommUtil.GetSpots(getActivity(), user, "ll", ll, "food", 100, 20, "");
			Log.v("Background", "returned from commutil.  result = " + result.getClass());
//	GetSpots(Context context, String userName, String searchMethod, String searchValue, String category, Integer limit, Integer radius, String query) {
			return result;
		}

		@Override
		protected void onPostExecute(final JSONObject result) {
			//Toast.makeText(getActivity(),"i got this far",Toast.LENGTH_SHORT).show();
			JSONArray arr = null;
			String menu = "";
			try {
				 arr = result.getJSONArray("response");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			for (int i = 0; i < arr.length(); i++) {
//				int id = response.getJSONObject(i).getInt("teamId");
//				String name = response.getJSONObject(i).getString(
//						"teamName");
//				teamList.add(new Object[] { id, name });

				List<Object[]> aPlace = new ArrayList<Object[]>();
				List<Object[]> aLocation = new ArrayList<Object[]>();
				
				String name = null;
				try {
					name = arr.getJSONObject(i).getString("name");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					String placeUrl = arr.getJSONObject(i).getString("url");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String phone = null;
				try {
					phone = arr.getJSONObject(i).getString("phone");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					String placeCat = arr.getJSONObject(i).getString("category");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					 menu = arr.getJSONObject(i).getString("menu");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					Integer crowd = arr.getJSONObject(i).getInt("crowd");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				JSONObject loc = null;
				try {
					loc = arr.getJSONObject(i).getJSONObject("location");
					
					Double lat = null;
					try {
						lat = Double.parseDouble(loc.getString("lat"));
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Double lng = null;
					try {
						lng = Double.parseDouble(loc.getString("lng"));
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					LatLng place = new LatLng(lat,lng);
					
			        map.addMarker(new MarkerOptions()
	                .title(name)
	                .snippet(menu)
	                .position(place)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
//				aPlace.add(new Object[] {"name", name});
//				aPlace.add(new Object[] {"url", placeUrl});
//				aPlace.add(new Object[] {"phone", phone});
//				aPlace.add(new Object[] {"category", placeCat});
//				aPlace.add(new Object[] {"menu", menu});
//				aPlace.add(new Object[] {"crowd", crowd});

				for (int j = 0; j < loc.length(); j++)
				{
//					aLocation.add(new Object[] {"address", loc.getJSONObject(j).getString("address")});
//					aLocation.add(new Object[] {"crossStreet", loc.getJSONObject(j).getString("crossStreet")});
//					aLocation.add(new Object[] {"lat", loc.getJSONObject(j).getString("lat")});
//					aLocation.add(new Object[] {"lng", loc.getJSONObject(j).getString("lng")});
//					aLocation.add(new Object[] {"distance", loc.getJSONObject(j).getString("distance")});
//					aLocation.add(new Object[] {"postalCode", loc.getJSONObject(j).getString("postalCode")});
//					aLocation.add(new Object[] {"cc", loc.getJSONObject(j).getString("cc")});
//					aLocation.add(new Object[] {"city", loc.getJSONObject(j).getString("city")});
//					aLocation.add(new Object[] {"state", loc.getJSONObject(j).getString("state")});
//					aLocation.add(new Object[] {"country", loc.getJSONObject(j).getString("country")});
					

				}
				
			}
				//aPlace.add(new Object[] {"location", aLocation});
				//thePlaces.add(new Object[] {i, aPlace});

		}

		@Override
		protected void onCancelled() {
			fqT = null;
			//showProgress(false);
		}
	}





	@Override
	public void onInfoWindowClick(Marker arg0) {
		// TODO Auto-generated method stub
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(arg0.getSnippet()));
		startActivity(browserIntent);
	}
	
}
