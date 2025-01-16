package ro.eim.practicaltest02mainactivityv9

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : FragmentActivity(), OnMapReadyCallback {
    private var mMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Coordinates for Ghelmeșoaia (center of the map)
        val ghelmesoaia = LatLng(44.7253, 22.9382)

        // Coordinates for Bucharest (marker location)
        val bucharest = LatLng(44.4268, 26.1025)

        // Add a marker in Bucharest
        mMap!!.addMarker(MarkerOptions().position(bucharest).title("Marker in Bucharest"))

        // Center the map on Ghelmeșoaia
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(ghelmesoaia, 10f))
    }
}