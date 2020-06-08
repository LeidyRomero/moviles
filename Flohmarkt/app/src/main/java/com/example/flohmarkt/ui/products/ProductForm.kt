package com.example.flohmarkt.ui.products

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import com.example.flohmarkt.MainActivity.Companion.db
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.flohmarkt.R
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.product_form.view.*
import com.example.flohmarkt.MainActivity.Companion.mainViewModel

class ProductForm : Fragment(), View.OnClickListener {
    //References to firestore storage and data base
    private var mStorageRef: StorageReference? = null
    private lateinit var root: View
    private var numeroTienda:Int? = 0
    private lateinit var ruta:Uri
    private lateinit var r:StorageReference
    private lateinit var producto:Producto
    private lateinit var dialog: AlertDialog.Builder
    data class Producto(
            val Nombre: String? = null,
            val Descripción: String? = null,
            val Precio: Int? = null,
            val Store_number: Int? = null,
            val Image: String? = null,
            val Consultas: Int? = null
    )
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.product_form, container, false)
        //Storage Firebase
        mStorageRef = FirebaseStorage.getInstance().reference

        root.buscarImagen.setOnClickListener {
            buscarImagen()
        }

        mainViewModel.userEmail.observe(viewLifecycleOwner, Observer<String> { email ->
            db.collection("Stores")
                .whereEqualTo("Email",email)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        numeroTienda= document.data["Store_number"].toString().toInt()
                    }

                }
        })
        root.enviar.setOnClickListener(this)
        return root
    }
    private fun ok(){
        root.progressBar.visibility=View.INVISIBLE
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        dialog = AlertDialog.Builder(activity)
        dialog.setTitle("Registro exitoso")
        dialog.setMessage("Producto registrado")
        dialog.show()
        clear()
    }
    private fun informacion(){
        dialog = AlertDialog.Builder(activity)
        dialog.setTitle("Datos incompletos")
        dialog.setMessage("Por favor ingrese todos los campos")
        dialog.show()
    }
    private fun fail(){
        root.progressBar.visibility=View.INVISIBLE
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        dialog = AlertDialog.Builder(activity)
        dialog.setTitle("Registro no exitoso")
        dialog.setMessage("Producto no registrado\npor favor, intente de nuevo")
        dialog.show()

        clear()
    }
    private fun clear()
    {
        root.path.setText("", TextView.BufferType.EDITABLE)
        root.inNombre.setText("", TextView.BufferType.EDITABLE)
        root.inDescripcion.setText("", TextView.BufferType.EDITABLE)
        root.inPrecio.setText("", TextView.BufferType.EDITABLE)
    }
    private fun buscarImagen()
    {
        val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        i.type = "image/"
        startActivityForResult(Intent.createChooser(i, "Seleccione la aplicación"), 10)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            ruta = data!!.data!!
            root.path.text=ruta.lastPathSegment.toString()
        }
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("WrongConstant")
    override fun onClick(v: View?) {
        if(ruta.path.toString()=="")
        {
            dialog = AlertDialog.Builder(activity)
            dialog.setTitle("Imagen no encontrada")
            dialog.setMessage("Por favor seleccione una imagen valida")
            dialog.show()
        }
        else if (root.inNombre.text.toString() == "" || root.inDescripcion.text.toString() == "" || root.inPrecio.text.toString() == "") {
            informacion()
            root.inNombre.backgroundTintList = ColorStateList.valueOf(Color.RED)
            root.inDescripcion.backgroundTintList = ColorStateList.valueOf(Color.RED)
            root.inPrecio.backgroundTintList = ColorStateList.valueOf(Color.RED)
        }
        else{
        root.progressBar.visibility=View.VISIBLE
        activity?.window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        r=mStorageRef?.child("Products_images/$numeroTienda")!!

            r.putFile(ruta)
                .continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    r.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                            producto = Producto(root.inNombre.text.toString(),
                            root.inDescripcion.text.toString(),
                            root.inPrecio.text.toString().toInt(),
                            numeroTienda,
                            task.result.toString(), 0)
                        db.collection("Products")
                            .add(producto)
                            .addOnSuccessListener {
                                ok()
                            }
                            .addOnFailureListener {
                                fail()
                            }
                    }
                }
        }
    }
}