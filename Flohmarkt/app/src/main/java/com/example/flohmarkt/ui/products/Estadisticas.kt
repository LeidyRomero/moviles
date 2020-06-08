package com.example.flohmarkt.ui.products

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.flohmarkt.MainActivity.Companion.navController
import com.example.flohmarkt.R
import com.github.mikephil.charting.charts.PieChart
import com.example.flohmarkt.MainActivity.Companion.db
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.estadisticas.view.*
import com.example.flohmarkt.MainActivity.Companion.mainViewModel

class Estadisticas : Fragment(), View.OnClickListener {
    private lateinit var piechart: PieChart
    private lateinit var root: View
    private var numeroTienda=0
    private var valorespie = ArrayList<PieEntry>()
    private val desc = Description()
    lateinit var dataset:PieDataSet
    lateinit var data:PieData
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.estadisticas, container, false)
        piechart = root.pie

        root.fab.setOnClickListener(this)
        desc.text = "Productos gustados"
        desc.textSize = 12f
        desc.textColor = Color.BLACK
        piechart.description=desc
        piechart.holeRadius = 10f
        piechart.transparentCircleRadius = 10f
        piechart.isRotationEnabled=false

        mainViewModel.userEmail.observe(viewLifecycleOwner, Observer<String> { email ->
            db.collection("Stores")
                .whereEqualTo("Email",email.toString())
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        numeroTienda=document.data.get("Store_number").toString().toInt()
                        db.collection("Products")
                            .whereEqualTo("store_number",numeroTienda)
                            .get()
                            .addOnSuccessListener { result ->
                                for (document in result) {
                                    valorespie.add(PieEntry(document.data["consultas"].toString().toFloat(), document.data["descripción"].toString()))
                                }
                                dataset = PieDataSet(valorespie, "Productos")
                                data = PieData(dataset)
                                dataset.colors = ColorTemplate.createColors(ColorTemplate.JOYFUL_COLORS);
                                piechart.data = data
                            }
                    }

                }
        })

        return root
    }
    override fun onClick(v: View?) {
        navController.navigate(R.id.productForm)
    }
}
