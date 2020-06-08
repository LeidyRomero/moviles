package com.example.flohmarkt

import android.Manifest
import android.app.AlertDialog
import android.app.SearchManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuItemCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class MainActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    companion object {
        @JvmStatic
        val db = FirebaseFirestore.getInstance()
        @JvmStatic
        lateinit var navController: NavController
        @JvmStatic
        lateinit var mainViewModel: MainViewModel
    }
    private var topMenu: Menu? = null
    private lateinit var dialog: AlertDialog.Builder
    var REQUEST_ENABLE_BT=1;
    lateinit var mAuth: FirebaseAuth
    var PERMISSION_REQUEST_COARSE_LOCATION = 1;
    val cr: ConnectivityReceiver = ConnectivityReceiver()
    //Adaptador y manager de bluetooth
    //val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        //val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        //bluetoothManager.adapter
    //}
    //val BluetoothAdapter.isDisabled: Boolean
        //get() = !isEnabled
    //busqueda
    lateinit var searchView: SearchView
    //navegacion

    lateinit var navView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //login
        mAuth = FirebaseAuth.getInstance()
        //navegacion
        navView = findViewById(R.id.nav_view)
        navController = findNavController(this,R.id.nav_host_fragment)
        //comunicacion
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        //Connectivy
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION).apply {
            addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        }
        registerReceiver(cr, filter)

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        //bluetoothAdapter?.takeIf { it.isDisabled }?.apply {
            //val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            //startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        //}

        // Ask for permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("This app needs location access")
                builder.setMessage("Please grant location access so this app can detect beacons in the background.")
                builder.setPositiveButton(android.R.string.ok, null)
                builder.setOnDismissListener {
                    requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                        PERMISSION_REQUEST_COARSE_LOCATION)
                }
                builder.show()
            }
        }
        //login sucessful
        mainViewModel.userEmail.observe(this, Observer<String> {
            agregar()
            topMenu?.findItem(R.id.app_bar_logout)?.isVisible=true
            topMenu?.findItem(R.id.app_bar_signin)?.isVisible=false
        })

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        if(item.itemId==16908332)
            navController.navigateUp()
        return true
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.tool_bar_menu,menu)
        topMenu=menu
        val searchItem: MenuItem = menu.findItem(R.id.app_bar_search)
        val sigin: MenuItem = menu.findItem(R.id.app_bar_signin)
        val logout: MenuItem = menu.findItem(R.id.app_bar_logout)
        if(mAuth.currentUser!=null)
        {
            mainViewModel.updateUserEmail(mAuth.currentUser?.email.toString())
            if(!logout.isVisible) {
                agregar()
            }
            logout.isVisible = true
            sigin.isVisible = false
        }
        else
        {
            quitar()
            logout.isVisible=false
            sigin.isVisible=true
        }
        sigin.setOnMenuItemClickListener {
            navController.navigate(R.id.loginFragment)
            true
        }
        logout.setOnMenuItemClickListener {
            quitar()
            mAuth.signOut()

            topMenu?.findItem(R.id.app_bar_logout)?.isVisible=false
            topMenu?.findItem(R.id.app_bar_signin)?.isVisible=true

            navController.navigate(R.id.navigation_home)
            true
        }

        searchView = MenuItemCompat.getActionView(searchItem) as SearchView
        searchView.setOnCloseListener { true }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val currentFragment= navController.currentDestination?.label

                if  (currentFragment == "Mapa") {
                    mainViewModel.fragmentQuery("Mapa")
                    mainViewModel.query(query.toString())
                }
                else if  (currentFragment == "Tiendas"||currentFragment == "Home"||currentFragment == "Detalle tienda"||currentFragment == "Detalle producto") {
                    //SEARCH
                    val int = query?.toIntOrNull()
                    if(int!=null){
                        mainViewModel.numberStore(int.toString())
                        navController.navigate(R.id.storeDetail)
                    }
                    else
                    {
                        db.collection("Categoria")
                                .whereEqualTo("Nombre", query)
                                .get()
                                .addOnSuccessListener { result ->
                                    if(result.isEmpty){
                                        db.collection("Products")
                                                .whereEqualTo("nombre", query)
                                                .get()
                                                .addOnSuccessListener { result ->
                                                    if(result.isEmpty){
                                                        basicAlert()
                                                    }
                                                    else {
                                                        mainViewModel.nameProduct(query.toString())
                                                        navController.navigate(R.id.productDetail)
                                                    }
                                                }
                                    }
                                    else {
                                        mainViewModel.nameCategory(query.toString())
                                        navController.navigate(R.id.navigation_stores)
                                    }
                                }
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(cr)
    }
    fun basicAlert(){
        dialog = AlertDialog.Builder(this)
        dialog.setTitle("Valor ingresado no válido")
        dialog.setMessage("Intentar de nuevo, opciones válidas:\n" +
                "ID tienda: 52\n" +
                "Categoria: Escultura\n" +
                "Producto:.")

        dialog.show()
    }

     private fun agregar() {
        val i: MenuItem? =navView.menu.add(Menu.NONE,R.id.estadisticas,3,"Más")
        if (i != null) {
            i.icon=resources.getDrawable(R.drawable.ic_statistics)
        }
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.navigation_home, R.id.navigation_map, R.id.estadisticas))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
    private fun quitar() {
        navView.menu.removeItem(R.id.estadisticas)

        val appBarConfiguration = AppBarConfiguration(setOf(R.id.navigation_home, R.id.navigation_map, R.id.navigation_stores))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onResume() {
        super.onResume()
        Flohmarkt.getInstance().setConnectivityLister(this)
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showSnack(isConnected)
        mainViewModel.setIsConnected(isConnected)
    }
    // Showing the status in Snackbar
    private fun showSnack(isConnected: Boolean) {
        val message: String
        val color: Int
        if (isConnected) {
            message = "Good! Connected to Internet"
            color = Color.WHITE
        } else {
            message = "Sorry! Not connected to internet"
            color = Color.RED
        }
        val snackbar: Snackbar = Snackbar
            .make(findViewById(R.id.bottom_menu), message, Snackbar.LENGTH_LONG)
        val sbView: View = snackbar.getView()
        val textView: TextView =
            sbView.findViewById(R.id.snackbar_text) as TextView
        textView.setTextColor(color)
        snackbar.show()
    }

}




