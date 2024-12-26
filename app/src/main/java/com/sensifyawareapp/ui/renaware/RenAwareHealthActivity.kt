package com.sensifyawareapp.ui.renaware

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.ActivityRenAwareHealthBinding
import com.sensifyawareapp.ui.BaseActivity

class RenAwareHealthActivity : BaseActivity() {
    lateinit var binding: ActivityRenAwareHealthBinding
    lateinit var number: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRenAwareHealthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        number = intent.getStringExtra("Name").toString()

//        showColorCard()
        /* val courseModelArrayList: ArrayList<String> = ArrayList()

         courseModelArrayList.add("DSA")
         courseModelArrayList.add("JAVA")
         courseModelArrayList.add("C++")
         courseModelArrayList.add("Python")
         courseModelArrayList.add("Python")
         courseModelArrayList.add("Python")

         val adapter = CourseGVAdapter(this, courseModelArrayList)
         binding.llProtein.gridCompanies.adapter = adapter
         binding.llKetone.gridCompanies.adapter = adapter
         binding.llBlood.gridCompanies.adapter = adapter
         binding.llCreatinine.gridCompanies.adapter = adapter*/
    }

//    fun showColorCard() {
//        if (number == "Renal") {
//            binding.llProtein.card7.visibility = View.GONE
//            binding.llProtein.layout1.backgroundTintList =
//                ContextCompat.getColorStateList(this, R.color.protein_1)
//            binding.llProtein.layout2.backgroundTintList =
//                ContextCompat.getColorStateList(this, R.color.protein_2)
//            binding.llProtein.layout3.backgroundTintList =
//                ContextCompat.getColorStateList(this, R.color.protein_3)
//            binding.llProtein.layout4.backgroundTintList =
//                ContextCompat.getColorStateList(this, R.color.protein_4)
//            binding.llProtein.layout5.backgroundTintList =
//                ContextCompat.getColorStateList(this, R.color.protein_5)
//            binding.llProtein.layout6.backgroundTintList =
//                ContextCompat.getColorStateList(this, R.color.protein_6)
//
//            binding.llCreatinine.layout1.backgroundTintList =
//                ContextCompat.getColorStateList(this, R.color.protein_1)
//            binding.llCreatinine.layout2.backgroundTintList =
//                ContextCompat.getColorStateList(this, R.color.protein_2)
//            binding.llCreatinine.layout3.backgroundTintList =
//                ContextCompat.getColorStateList(this, R.color.protein_3)
//            binding.llCreatinine.layout4.backgroundTintList =
//                ContextCompat.getColorStateList(this, R.color.protein_4)
//            binding.llCreatinine.layout5.backgroundTintList =
//                ContextCompat.getColorStateList(this, R.color.protein_5)
//
//            binding.llProtein.card7.visibility = View.GONE
//            binding.llProtein.layout1.backgroundTintList =
//                ContextCompat.getColorStateList(this, R.color.protein_1)
//            binding.llProtein.layout2.backgroundTintList =
//                ContextCompat.getColorStateList(this, R.color.protein_2)
//            binding.llProtein.layout3.backgroundTintList =
//                ContextCompat.getColorStateList(this, R.color.protein_3)
//            binding.llProtein.layout4.backgroundTintList =
//                ContextCompat.getColorStateList(this, R.color.protein_4)
//            binding.llProtein.layout5.backgroundTintList =
//                ContextCompat.getColorStateList(this, R.color.protein_5)
//            binding.llProtein.layout6.backgroundTintList =
//                ContextCompat.getColorStateList(this, R.color.protein_6)
//
//            binding.llProtein.card7.visibility = View.GONE
//            binding.llProtein.layout1.backgroundTintList =
//                ContextCompat.getColorStateList(this, R.color.protein_1)
//            binding.llProtein.layout2.backgroundTintList =
//                ContextCompat.getColorStateList(this, R.color.protein_2)
//            binding.llProtein.layout3.backgroundTintList =
//                ContextCompat.getColorStateList(this, R.color.protein_3)
//            binding.llProtein.layout4.backgroundTintList =
//                ContextCompat.getColorStateList(this, R.color.protein_4)
//            binding.llProtein.layout5.backgroundTintList =
//                ContextCompat.getColorStateList(this, R.color.protein_5)
//            binding.llProtein.layout6.backgroundTintList =
//                ContextCompat.getColorStateList(this, R.color.protein_6)
//        } else if (number == "Liver") {
//            var color = ContextCompat.getColor(this, R.color.red)
//            binding.llBlood.cardMenu.visibility = View.GONE
//            binding.llKetone.cardMenu.visibility = View.GONE
//
//        } else if (number == "Hydration") {
//            binding.llBlood.cardMenu.visibility = View.GONE
//            binding.llKetone.cardMenu.visibility = View.GONE
//            binding.llCreatinine.cardMenu.visibility = View.GONE
//
//        } else if (number == "Energy") {
//            binding.llBlood.cardMenu.visibility = View.GONE
//            binding.llKetone.cardMenu.visibility = View.GONE
//
//        } else if (number == "Immunity") {
//
//
//        } else if (number == "Bone") {
//            binding.llBlood.cardMenu.visibility = View.GONE
//            binding.llKetone.cardMenu.visibility = View.GONE
//            binding.llCreatinine.cardMenu.visibility = View.GONE
//        }
//    }
}