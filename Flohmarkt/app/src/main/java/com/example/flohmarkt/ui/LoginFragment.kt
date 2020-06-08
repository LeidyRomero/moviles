package com.example.flohmarkt.ui

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.flohmarkt.MainActivity.Companion.mainViewModel
import com.example.flohmarkt.MainActivity.Companion.navController
import com.example.flohmarkt.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.login.view.*

class LoginFragment : Fragment(), View.OnClickListener {
    private lateinit var root: View
    private lateinit var dialog: AlertDialog.Builder
    private var mAuth: FirebaseAuth? = null
    private val patronCorreo:Regex = ("(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*" +
            "|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]" +
            "|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9]" +
            "(?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?" +
            "|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]" +
            "|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*" +
            "[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]" +
            "|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])").toRegex()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        mAuth = FirebaseAuth.getInstance()
        root = inflater.inflate(R.layout.login, container, false)
        root.ingresar.setOnClickListener(this)
        return root
    }

    private fun fail(){
        dialog = AlertDialog.Builder(activity)
        dialog.setTitle("Datos incorrectos")
        dialog.setMessage("Por favor intente de nuevo")
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun correoInvalido(){
        dialog = AlertDialog.Builder(activity)
        dialog.setTitle("Correo inválido")
        dialog.setMessage("Por favor ingrese un correo válido")
        dialog.show()

        root.user.backgroundTintList= ColorStateList.valueOf( Color.RED )
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun llenar(){
        dialog = AlertDialog.Builder(activity)
        dialog.setTitle("Datos incompletos")
        dialog.setMessage("Por favor diligencie todos los datos")
        dialog.show()
        root.user.backgroundTintList= ColorStateList.valueOf( Color.RED )
        root.password.backgroundTintList= ColorStateList.valueOf( Color.RED )
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onClick(v: View?) {
        activity?.let {
            if (root.user.text.toString() == "" || root.password.text.toString() == "") {
                llenar()
            }
            else if(!root.user.text.toString().matches(patronCorreo))
            {
                correoInvalido()
            }
            else {
                mAuth?.signInWithEmailAndPassword(root.user.text.toString(), root.password.text.toString())
                        ?.addOnCompleteListener(it) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                mainViewModel.updateUserEmail(mAuth!!.currentUser?.email.toString())
                                if (navController != null) {
                                    navController.navigate(R.id.navigation_home)
                                }
                            } else {
                                fail()
                            }
                        }
            }
        }
    }
}

