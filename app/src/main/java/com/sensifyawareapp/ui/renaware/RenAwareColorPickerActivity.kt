package com.sensifyawareapp.ui.renaware

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import com.sensifyawareapp.databinding.ActivityRenAwareColorPickBinding
import com.sensifyawareapp.ui.BaseActivity
import kotlin.math.pow
import kotlin.math.sqrt

class RenAwareColorPickerActivity : BaseActivity() {
    lateinit var binding: ActivityRenAwareColorPickBinding
    var data = ArrayList<Int>()
    var rgb = ArrayList<String>()
    var colorCode = ArrayList<String>()
    var colorValue = ArrayList<String>()
    private var wordsList = ArrayList<String>()
    private var sensorValue = 0.0
    var name = StringBuilder()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRenAwareColorPickBinding.inflate(layoutInflater)
        setContentView(binding.root)
        data = intent.getIntegerArrayListExtra("pixel") as ArrayList<Int>
        sensorValue = intent.getDoubleExtra("sensorValue", 0.0)
        binding.text13.text = "Luminosity    = $sensorValue"
        Log.e("TAG", "onCreate: $data")

        val r = Color.red(data[0])
        val g = Color.green(data[0])
        val b = Color.blue(data[0])
        // color name in Hexadecimal(#RRGGBB)
        Log.e(
            "TAG",
            "onImageSaved: 12 ${Integer.toHexString(data[0])}  //  ${
                Color.rgb(
                    r,
                    g,
                    b
                )
            }  // $r $g  $b ",
        )

        for (i in data) {
            val r = Color.red(i)
            val g = Color.green(i)
            val b = Color.blue(i)
//            Color.rgb(Color.red(i), Color.green(i), Color.blue(i))
            rgb.add("$r, $g, $b")
            Log.e("TAG", "onCreate: ${Color.rgb(Color.red(i), Color.green(i), Color.blue(i))}")

        }

        for (i in data) {
            colorCode.add(Integer.toHexString(i))
        }

        binding.text1.text = "Box 1 = UIExtendedSRGBColorSpace ${rgb[0]}"
        binding.text2.text = "Box 2 = UIExtendedSRGBColorSpace ${rgb[1]}"
        binding.text3.text = "Box 3 = UIExtendedSRGBColorSpace ${rgb[2]}"
        binding.text4.text = "Box 4 = UIExtendedSRGBColorSpace ${rgb[3]}"
        binding.text5.text = "Box 5 = UIExtendedSRGBColorSpace ${rgb[4]}"
        binding.text6.text = "Box 6 = UIExtendedSRGBColorSpace ${rgb[5]}"
        binding.text7.text = "Box 7 = UIExtendedSRGBColorSpace ${rgb[6]}"
        binding.text8.text = "Box 8 = UIExtendedSRGBColorSpace ${rgb[7]}"
        binding.text9.text = "Box 9 = UIExtendedSRGBColorSpace ${rgb[8]}"
        binding.text10.text = "Box 10 = UIExtendedSRGBColorSpace ${rgb[9]}"
        binding.text11.text = "Box 11 = UIExtendedSRGBColorSpace ${rgb[10]}"
        binding.text12.text = "Box 12 = UIExtendedSRGBColorSpace ${rgb[11]}"

        for (i in colorCode) {
            colorValue.add(i.drop(2))
        }

        binding.toolbar.ivBack.setOnClickListener {
            showCloseDialog()
        }
        binding.toolbar.title = "ShowColor"

        main()
    }

    var Leucocyte = listOf("#E0E2CC", "#E1E1D9", "#CDC2C8", "#9A76A2", "#745C98")
    var Urobilinogen = listOf("#EEE0DD", "#F0D4D1", "#EFC4B3", "#E7B0A9", "#DB9397", "#D47F7C")
    var Protein = listOf("#D7DA57", "#C6DC53", "#ACCA80", "#91C392", "#71B09F", "#559187")
    var Bilirubin = listOf("#E2DCAC", "#E8DCC6", "#D4BAA3", "#D1A29C")
    var Glucose = listOf("#DCE451", "#C7E35A", "#94C552", "#45A871", "#077A6D", "#004845")
    var Ascorbate = listOf("#06436F", "#4F696A", "#D7C94C", "#D1C138")
    var Specific_gravity =
        listOf("#001638", "#074129", "#4D671D", "#667402", "#7F8200", "#887F00", "#A18E01")
    var Ketone = listOf("#DFE1C9", "#CCC0CE", "#C29ECC", "#9E63B7", "#5E0073", "#450075")
    var Nitrite = listOf("#D8D5C2", "#D9CBBE", "#9C0E86")
    var Creatinine = listOf("#999500", "#9AAC32", "#87A541", "#498428", "#004900")
    var pH = listOf("#C46801", "#BB9A00", "#9C8900", "#879F01", "#557B01", "#497A1E", "#022F42")
    var Blood = listOf("#BF9600", "#ACA000", "#7C8D01", "#355200", "#000000", "#B39A00", "#A69D00")


    private fun getDiffColor(cola: String, colb: String): Double {
        val a = hexToRgb(cola)
        val b = hexToRgb(colb)
//        Log.e("TAG", "getDiffColor: $a // $b}")
        return sqrt(
            (a!!.r - b!!.r).toDouble().pow(2) + (a.g - b.g).toDouble()
                .pow(2) + (a.b - b.b).toDouble()
                .pow(2)
        )
    }

    private fun hexToRgb(hex: String): RGB? {
        val result = Regex(
            "^#?([a-f\\d]{2})([a-f\\d]{2})([a-f\\d]{2})$",
            RegexOption.IGNORE_CASE
        ).matchEntire(hex)
        return result?.destructured?.let { (r, g, b) ->
            RGB(r.toInt(16), g.toInt(16), b.toInt(16))
        }
    }

    data class RGB(val r: Int, val g: Int, val b: Int)

    private fun main() {
        var i = 0
        while (i < colorValue.size) {
            when (i) {
                0 -> {
                    name.append("Variant : Leucocyte")
                    colorList(Leucocyte, i)
                    i++
                }

                1 -> {
                    name.append("Variant : Urobilinogen")
                    colorList(Urobilinogen, i)
                    i++
                }

                2 -> {
                    name.append("Variant : Protein")
                    colorList(Protein, i)
                    i++
                }

                3 -> {
                    name.append("Variant : Bilirubin")
                    colorList(Bilirubin, i)
                    i++
                }

                4 -> {
                    name.append("Variant : Glucose")
                    colorList(Glucose, i)
                    i++
                }

                5 -> {
                    name.append("Variant : Ascorbate")
                    colorList(Ascorbate, i)
                    i++
                }

                6 -> {
                    name.append("Variant : Specific_gravity")
                    colorList(Specific_gravity, i)
                    i++
                }

                7 -> {
                    name.append("Variant : Ketone")
                    colorList(Ketone, i)
                    i++
                }

                8 -> {
                    name.append("Variant : Creatinine")
                    colorList(Creatinine, i)
                    i++
                }

                9 -> {
                    name.append("Variant : Nitrite")
                    colorList(Nitrite, i)
                    i++
                }

                10 -> {
                    name.append("Variant : pH")
                    colorList(pH, i)
                    i++
                }

                11 -> {
                    name.append("Variant : Blood")
                    colorList(Blood, i)
                    i++
                }
            }
        }
    }

    private fun colorList(colorCode: List<String>, i: Int) {
        val res = mutableListOf<Double>()
        colorCode.forEach { value ->
            res.add(getDiffColor(value, colorValue[i]))
        }
        val minIndex = res.indexOf(res.minOrNull())
        val color = colorCode.filter { it == colorCode[minIndex] }.toString().replace("[", "")
            .replace("]", "")
        Log.e("TAG", "colorList 123: ${colorValue[i]}")
        name.append(" || Matching Index : ${colorCode.indexOf(color) + 1}")

        wordsList.add(name.toString())
        Log.e("TAG", "colorList: $name")
        name.clear()

        binding.button.setOnClickListener {
            val intent =
                Intent(this@RenAwareColorPickerActivity, RenAwareResultActivity::class.java)
//            intent.putExtra("color", wordsList)
            intent.putExtra("pixel", data)
                .putExtra("cartridgeId", intent.getStringExtra("cartridgeId"))
            startActivity(intent)
        }
        println(name)

    }

    override fun onBackPressed() {
        showCloseDialog()
    }

}