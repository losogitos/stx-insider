package com.stxnext.stxinsider

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.stxnext.stxinsider.receiver.WifiConnStateChangedListener

import org.joda.time.DateTime
import org.joda.time.Duration

import butterknife.Bind
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.bindView

class MapActivity : AppCompatActivity() {

    val OFFICE_LOCATION = LatLng(52.3944957, 16.8936571)
    val WiFiSSID = "StxXXXXXXX"
    val WiFiPass = "xxxxxxxxx"

    internal val taxiPhoneNoTextView: TextView  by bindView(R.id.activity_main_taxi_phone_no_tv)
    internal val wifiSSIDTextView: TextView by bindView(R.id.activity_main_wifi_ssid_tv)
    internal val wifiPassTextView: TextView by bindView(R.id.activity_main_wifi_pass_tv)
    internal val wifiProgressBar: ProgressBar  by bindView(R.id.activity_main_wifi_connection_progressbar)

    private var map: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        supportActionBar!!.title = "Location"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        ButterKnife.bind(this)

        wifiSSIDTextView.text = wifiSSIDTextView.text.toString() + WiFiSSID
        wifiPassTextView.text = wifiPassTextView.text.toString() + WiFiPass

        prepareMap()
    }

    internal var wifiInitStarted: DateTime? = null
    private fun wifiConnectionStateChanged(ssid: String, enabled: Boolean) {

        if (wifiInitStarted != null && enabled == true) {
            val diffInMillis = DateTime.now().millis - wifiInitStarted!!.millis
            val seconds = Duration.millis(diffInMillis).standardSeconds
            if (seconds > 0) {
                wifiInitStarted = null
                //Toast.makeText(this, "MainActivity: Wifi connected: " + ssid, Toast.LENGTH_SHORT).show();
                wifiProgressBar.visibility = View.GONE
            }
        }
    }

    @OnClick(R.id.activity_main_taxi_phone_no_tv)
    fun callTaxiClick(v: View) {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:" + taxiPhoneNoTextView.text)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        startActivity(intent)
    }

    @OnClick(R.id.activity_main_wifi_outer_layout)
    fun connectToWifi(v: View) {
        wifiInitStarted = DateTime.now()
        wifiProgressBar.visibility = View.VISIBLE

        val conf = WifiConfiguration()
        conf.SSID = "\"" + WiFiSSID + "\""
        conf.preSharedKey = "\"" + WiFiPass + "\""

        val wifiManager = this.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (!wifiManager.isWifiEnabled)
            wifiManager.isWifiEnabled = true

        var netId = getNetworkId(wifiManager, WiFiSSID)
        if (netId != -1)
            wifiManager.removeNetwork(netId)
        wifiManager.addNetwork(conf)

        netId = getNetworkId(wifiManager, WiFiSSID)
        wifiManager.disconnect()
        wifiManager.enableNetwork(netId, true)
        wifiManager.reconnect()
    }

    private fun getNetworkId(wifiManager: WifiManager, SSID: String): Int {
        val list = wifiManager.configuredNetworks
        if (list == null || list.size == 0)
            return -1

        for (i in list) {
            if (i.SSID != null && i.SSID == "\"" + SSID + "\"")
                return i.networkId
        }

        return -1
    }


    override fun onResume() {
        super.onResume()
        wifiStateListener = WifiConnStateChangedListener { ssid, enabled -> wifiConnectionStateChanged(ssid, enabled) }
    }

    override fun onPause() {
        super.onPause()
        wifiStateListener = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home)
            finish()

        return super.onOptionsItemSelected(item)
    }

    private fun prepareMap() {
        val fm = supportFragmentManager
        val fragment = fm.findFragmentById(R.id.map_fragment) as SupportMapFragment
        if (fragment != null) {
            map = fragment.map
            if (map != null) {

                val options = MarkerOptions()
                options.position(OFFICE_LOCATION)
                map!!.addMarker(options)

                map!!.mapType = GoogleMap.MAP_TYPE_TERRAIN
                map!!.uiSettings.setAllGesturesEnabled(false)

                animateMap()
            }
        }
    }

    private fun animateMap() {
        if (map != null) {
            val cameraPosition = LatLng(OFFICE_LOCATION.latitude + 0.02, OFFICE_LOCATION.longitude)
            map!!.animateCamera(CameraUpdateFactory.newLatLngZoom(cameraPosition, 12f), 2000, null)
        }
    }

    companion object WifiStateListener {
        var wifiStateListener: WifiConnStateChangedListener? = null
            private set
    }
}
