package com.example.rentisha.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import androidx.navigation.fragment.navArgs
import com.example.rentisha.BaseApplication
import com.example.rentisha.R
import com.example.rentisha.database.DatabaseHouse
import com.example.rentisha.databinding.FragmentHouseDetailBinding
import com.example.rentisha.viewmodels.RentishaViewModel


import java.lang.reflect.Field


class HouseDetailFragment : Fragment() {

    private val viewModel: RentishaViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(this, RentishaViewModel.Factory(activity?.application as BaseApplication))
            .get(RentishaViewModel::class.java)
    }
    private val navigationArgs: HouseDetailFragmentArgs by navArgs()

    private var _binding: FragmentHouseDetailBinding? = null
    private val binding get() = _binding!!
    lateinit var databaseHouse: DatabaseHouse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHouseDetailBinding.inflate(inflater,container,false)
        val view =binding.root
        // Set the lifecycleOwner so DataBinding can observe LiveData
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = navigationArgs.uid
        viewModel.getHouse(id).observe(this.viewLifecycleOwner, Observer { it ->
            databaseHouse = it
            bind()
       })
    }
    fun bind(){
        binding.apply {
            name.text = databaseHouse.name
            title.text = databaseHouse.title
            location.text = databaseHouse.address
            descriptions.text = databaseHouse.description
            if (databaseHouse.isAvailable){
                vacancy.text = "Currently has vacancy"
            }
            else{
                vacancy.text = "Currently out of vacancy"

            }
            editHouseFab.setOnClickListener {
                val action = HouseDetailFragmentDirections.actionHouseDetailFragmentToAddHouseFragment(databaseHouse.id)
                findNavController().navigate(action)
            }
            location.setOnClickListener {
                launchMap()
            }

        }
    }
    private fun launchMap() {
        val address = databaseHouse.address.let {
            it.replace(", ", ",")
            it.replace(". ", " ")
            it.replace(" ", "+")
        }
        val gmmIntentUri = Uri.parse("geo:0,0?q=$address")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}