package com.example.contactlist.ui.gps

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.contactlist.R
import com.example.contactlist.databinding.FragmentGpsBinding

private const val GPS_PROVIDER_REQUEST_CODE = 41

class GpsFragment : Fragment() {

    private var _binding: FragmentGpsBinding? = null
    private val binding get() = _binding!!

    private val locationManager by lazy {
        context?.let { it.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentGpsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        context?.let {
            when (ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION)) {
                PackageManager.PERMISSION_GRANTED -> {
                    getLocation()
                }
                PackageManager.PERMISSION_DENIED -> {
                    requestPermissions()
                }
            }
            super.onViewCreated(view, savedInstanceState)
        }
    }

    // Обратный вызов после получения разрешений от пользователя
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        context?.let {
            when (requestCode) {
                GPS_PROVIDER_REQUEST_CODE -> {
                    // Проверяем, дано ли пользователем разрешение по нашему запросу
                    val pos = permissions.indexOf(Manifest.permission.ACCESS_FINE_LOCATION)
                    if ((grantResults[pos] == PackageManager.PERMISSION_GRANTED)) {
                        getLocation()
                    } else {
                        showDialog(
                            getString(R.string.dialog_title_no_gps),
                            getString(R.string.dialog_message_no_gps)
                        )
                    }
                }
            }
            return
        }
    }

    private fun showDialog(title: String, message: String) {
        activity?.let {
            AlertDialog.Builder(it)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(getString(R.string.dialog_button_close)) { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }

    private fun getLocation() {
        context?.let {
            when (ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION)) {
                PackageManager.PERMISSION_GRANTED -> {
                    locationManager?.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        3000,
                        10f
                    ) { loc ->
                        binding.latTextView.text = loc.latitude.toString()
                        binding.lonTextView.text = loc.longitude.toString()
                        if (Geocoder.isPresent()) {
                       Thread{
                           Geocoder(it).getFromLocation(loc.latitude, loc.longitude, 1)
                               .firstOrNull()?.let { addr ->
                                   activity?.runOnUiThread{binding.addrTextView.text = addr.getAddressLine(0)}
                               }
                       }.start()

                        }
                    }
                }
                else -> {
                    showRationaleDialog()
                }

            }

        }
    }

    private fun showRationaleDialog() {
        activity?.let {
            AlertDialog.Builder(it)
                .setTitle(getString(R.string.dialog_rationale_title))
                .setMessage(getString(R.string.dialog_rationale_meaasge))
                .setPositiveButton(getString(R.string.dialog_rationale_give_access)) { _, _ ->
                    requestPermissions()
                }
                .setNegativeButton(getString(R.string.dialog_rationale_decline)) { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }

    private fun requestPermissions() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            GPS_PROVIDER_REQUEST_CODE
        )
    }

    companion object {
        @JvmStatic
        fun newInstance() = GpsFragment()
    }
}