package com.example.flohmarkt.ui.mapa

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import com.example.flohmarkt.MainActivity.Companion.db
import com.example.flohmarkt.MainActivity.Companion.mainViewModel
import android.os.Bundle
import android.os.RemoteException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.flohmarkt.MainActivity.Companion.navController
import com.example.flohmarkt.R
import kotlinx.android.synthetic.main.fragment_map.view.*
import org.altbeacon.beacon.*

class DashboardFragment : Fragment(), View.OnClickListener, BeaconConsumer {

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var root: View
    private lateinit var beaconManager: BeaconManager
    private var resID: Int=0
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_map, container, false)
        // Beacon
        beaconManager = BeaconManager.getInstanceForApplication(root.context)
        beaconManager!!.getBeaconParsers().add(
            BeaconParser()
                .setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        beaconManager!!.bind(this)

        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel::class.java)

        mainViewModel.fragmentQuery.observe(viewLifecycleOwner, Observer { fragment ->
            fragment?.let {
                if(fragment=="Mapa") {
                    mainViewModel.query.observe(viewLifecycleOwner, Observer { query ->
                        query?.let {
                            //int = mainViewModel.query.value?.toIntOrNull()!!
                            db.collection("Categoria")
                                .whereEqualTo("Nombre", mainViewModel.query.value)
                                .get()
                                .addOnSuccessListener { result ->
                                    if(result.isEmpty){
                                       basicAlert()
                                    }
                                    else {
                                        for (document in result) {
                                            dashboardViewModel.setResultStores(document.data.getValue("Stores").toString().split(","))
                                        }
                                    }
                                }
                        }
                     })
                }
            }
        })

        root.button44.setOnClickListener(this)
        root.button45.setOnClickListener(this)
        root.button46.setOnClickListener(this)
        root.button46A.setOnClickListener(this)
        root.button47.setOnClickListener(this)
        root.button48.setOnClickListener(this)
        root.button49.setOnClickListener(this)
        root.button50.setOnClickListener(this)
        root.button51.setOnClickListener(this)
        root.button52.setOnClickListener(this)
        root.button160.setOnClickListener(this)
        root.button161.setOnClickListener(this)
        root.button162.setOnClickListener(this)
        root.button163.setOnClickListener(this)
        root.button164.setOnClickListener(this)
        root.button165.setOnClickListener(this)
        root.button192.setOnClickListener(this)
        root.button193.setOnClickListener(this)
        root.button194.setOnClickListener(this)
        root.button195.setOnClickListener(this)
        root.button196.setOnClickListener(this)
        root.button197.setOnClickListener(this)

        dashboardViewModel.nearStore.observe(viewLifecycleOwner, Observer { near ->
            near?.let {
                root.findViewById<Button>(resources.getIdentifier("button$it", "id", requireActivity().packageName.toString())).setBackgroundColor(Color.rgb(33,74,4))
                root.findViewById<Button>(resources.getIdentifier("button"+dashboardViewModel.bNearStore.value, "id", requireActivity().packageName.toString()))?.setBackgroundColor(Color.rgb(63,165,53))
            }
        })
        dashboardViewModel.biciZone.observe(viewLifecycleOwner, Observer { bici ->
            bici?.let { root.bici_zone.text = it.toString() }
        })
        dashboardViewModel.restZone.observe(viewLifecycleOwner, Observer { rest ->
            rest?.let { root.rest_zone.text = it.toString() }
        })

        dashboardViewModel.stores.observe(viewLifecycleOwner, Observer { stores ->
            stores?.let {
                for (i in dashboardViewModel.bStores.value!!){
                    resID = resources.getIdentifier("button$i", "id", requireActivity().packageName)
                    root.findViewById<Button>(resID).setBackgroundColor(Color.parseColor("#3FA535"))
                }
                for (i in stores){
                    resID = resources.getIdentifier("button$i", "id", requireActivity().packageName)
                    root.findViewById<Button>(resID).setBackgroundColor(Color.parseColor("#214A04"))
                }
            }
        })

        return root
    }
    override fun onClick(v: View?) {
        val b: Button = v as Button
        mainViewModel.numberStore(b.text.toString())
        navController.navigate(R.id.storeDetail)
    }
    private fun basicAlert(){
        val dialog = AlertDialog.Builder(activity)
        dialog.setTitle("Valor ingresado no válido")
        dialog.setMessage("Intentar de nuevo, opciones válidas:\n" +
                "ID tienda: 52\n" +
                "Categoria: Escultura.")

        dialog.show()
    }

    override fun getApplicationContext(): Context {
        return activity?.applicationContext!!
    }

    override fun unbindService(p0: ServiceConnection?) {
        if (p0 != null) {
            activity?.unbindService(p0)
        }
    }

    override fun bindService(p0: Intent?, p1: ServiceConnection?, p2: Int): Boolean {
        return if (p1 != null) {
            activity?.bindService(p0, p1, p2)!!
        } else {
            false
        }
    }

    override fun onBeaconServiceConnect() {
        beaconManager!!.setRangeNotifier(object : RangeNotifier {
            override fun didRangeBeaconsInRegion(beacons: Collection<Beacon>, region: Region) {
                if (beacons.size > 0) {
                    var min: Double = Double.MAX_VALUE
                    var idMin: String = ""
                    for (beacon in beacons)
                    {
                        var id = beacon.id2.toHexString().substring(11).toInt()
                        if( id == 0)
                            dashboardViewModel.setRest(beacon.distance)
                        else if (id == 400)
                            dashboardViewModel.setBici(beacon.distance)
                        else
                        {
                            if (beacon.distance < min) {
                                min = beacon.distance
                                idMin = beacon.id2.toHexString()
                            }
                        }

                    }
                    dashboardViewModel.setNeartest(idMin)
                }
            }
        })
        try {
            beaconManager!!.startRangingBeaconsInRegion(Region("myRangingUniqueId", null, null, null))
        } catch (e: RemoteException) {
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        beaconManager.unbind(this)
    }

    override fun onResume() {
        super.onResume()
        beaconManager.backgroundMode = false
    }

    override fun onPause() {
        super.onPause()
        beaconManager.backgroundMode = true
    }
 }
