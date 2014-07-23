package edu.psu.team3.app.awayteam;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MapFragment extends Fragment implements OnInfoWindowClickListener {

	private enum CategoryID {TEAM,FOOD,HOTEL,TRANS,SHOP,SHOWS}
	
	private LatLng currentLocation;
	private MapView mapView;
	private GoogleMap map;
	private FourSquareTask fqT;

	// UI Buttons
	ToggleButton teamButton;
	ToggleButton foodButton;
	ToggleButton hotelButton;
	ToggleButton transButton;
	ToggleButton shopButton;
	ToggleButton showsButton;
	
	//collections of markers formatted [categoryID,Marker,URL]
	List<Object[]>  markerList = new ArrayList<Object[]>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_map, container,
				false);
		mapView = (MapView) rootView.findViewById(R.id.mapview);
		mapView.onCreate(savedInstanceState);

		// Gets to GoogleMap from the MapView and does initialization stuff
		map = mapView.getMap();
		map.getUiSettings().setMyLocationButtonEnabled(true);
		map.setMyLocationEnabled(true);

		MapsInitializer.initialize(this.getActivity());

		// Updates the location and zoom of the MapView
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
				new LatLng(0, 0), 10);
		map.animateCamera(cameraUpdate);

		return rootView;
	}

	public void onStart() {
		super.onStart();
		// Get a handle to the Map Fragment
		map = mapView.getMap();

		LocationManager mgr = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);
		Location lastKnownLocation = mgr
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		currentLocation = new LatLng(lastKnownLocation.getLatitude(),
				lastKnownLocation.getLongitude());

		map.setMyLocationEnabled(true);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 10));
		map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
		map.setOnInfoWindowClickListener(this);

		// setup map layer buttons
		teamButton = (ToggleButton) getView().findViewById(R.id.mapTeamLayer);
		teamButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					// show map items
					plotTeamLocations();
				} else {
					// remove map items
					// TODO: do this
				}

			}
		});
		foodButton = (ToggleButton) getView().findViewById(R.id.mapFoodLayer);
		foodButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					// show map items
					if (fqT == null) {
						fqT = new FourSquareTask();
						fqT.execute(CategoryID.FOOD,"food");
					}
				} else {
					// remove map items
					// TODO: do this
				}

			}
		});
		hotelButton = (ToggleButton) getView().findViewById(R.id.mapHotelLayer);
		hotelButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					// show map items
					if (fqT == null) {
						fqT = new FourSquareTask();
						fqT.execute(CategoryID.HOTEL,"","hotels");
					}
				} else {
					// remove map items
					// TODO: do this
				}

			}
		});
		transButton = (ToggleButton) getView().findViewById(R.id.mapTransLayer);
		transButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					// show map items
					if (fqT == null) {
						fqT = new FourSquareTask();
						fqT.execute(CategoryID.TRANS,"travTrans");
					}
				} else {
					// remove map items
					// TODO: do this
				}

			}
		});
		shopButton = (ToggleButton) getView().findViewById(R.id.mapShopLayer);
		shopButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					// show map items
					if (fqT == null) {
						fqT = new FourSquareTask();
						fqT.execute(CategoryID.SHOP,"shopServ");
					}
				} else {
					// remove map items
					// TODO: do this
				}

			}
		});
		showsButton = (ToggleButton) getView().findViewById(R.id.mapShowLayer);
		showsButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					// show map items
					if (fqT == null) {
						fqT = new FourSquareTask();
						fqT.execute(CategoryID.SHOWS,"artsEnt");
					}
				} else {
					// remove map items
					// TODO: do this
				}

			}
		});
		
		// TODO: redisplay all markers in the list (if it still exists)

	}

	//removes all markers of this category from the list and the map display
	private void removeMarkerCategory(int category){
		
	}
	
	//show users with location info on map
	private void plotTeamLocations(){
		
	}
	
	// Lifecycle calls for the mapview
	@Override
	public void onResume() {
		mapView.onResume();
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mapView.onLowMemory();
	}

	
	//Makes a query using the foursquare api
	//input: category ID used to manage groups by other tasks, category text (optional),query text (optional)
	//action: populates marker list with new markers, displays markers on map
	public class FourSquareTask extends AsyncTask<Object, Void, JSONObject> {
		CategoryID categoryID;
		@Override
		protected JSONObject doInBackground(Object... params) {
			categoryID = (CategoryID) params[0];
			String category = "";
			String query = "";
			if (params.length > 1) {
				category = (String) params[1];
			}
			if (params.length > 2) {
				query = (String) params[2];
			}

			JSONObject result;
			String ll = currentLocation.latitude + ", "
					+ currentLocation.longitude;
			UserSession s = UserSession.getInstance(getActivity());
			String user = s.getUsername();

			result = CommUtil.GetSpots(getActivity(), user, "ll", ll, category,
					10, 20, query);
			return result;
		}

		@Override
		protected void onPostExecute(final JSONObject result) {
			JSONArray resultArray = null;
			String menu = "";
			try {
				resultArray = result.getJSONArray("response");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			for (int i = 0; i < resultArray.length(); i++) {

				String name = null;
				try {
					name = resultArray.getJSONObject(i).getString("name");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					String placeUrl = resultArray.getJSONObject(i).getString("url");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				String phone = null;
				try {
					phone = resultArray.getJSONObject(i).getString("phone");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					String placeCat = resultArray.getJSONObject(i)
							.getString("category");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					menu = resultArray.getJSONObject(i).getString("menu");
				} catch (JSONException e) {
					e.printStackTrace();
				}

				JSONObject loc = null;
				try {
					loc = resultArray.getJSONObject(i).getJSONObject("location");

					Double lat = null;
					try {
						lat = Double.parseDouble(loc.getString("lat"));
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
					Double lng = null;
					try {
						lng = Double.parseDouble(loc.getString("lng"));
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
					LatLng place = new LatLng(lat, lng);

					MarkerOptions markOp = new MarkerOptions();
					map.addMarker(new MarkerOptions()
							.title(name)
							.snippet(menu)
							.position(place)
							.icon(BitmapDescriptorFactory
									.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

		}

		@Override
		protected void onCancelled() {
			fqT = null;
			// showProgress(false);
		}
	}

	// Handle clicks on Markers!
	@Override
	public void onInfoWindowClick(Marker marker) {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(marker
				.getSnippet()));
		startActivity(browserIntent);
	}

}
