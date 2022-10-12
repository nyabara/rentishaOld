package com.example.rentisha.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import androidx.navigation.fragment.navArgs
import com.example.rentisha.Injection
import com.example.rentisha.R
import com.example.rentisha.database.DatabaseHouse
import com.example.rentisha.databinding.FragmentHouseDetailBinding
import com.example.rentisha.api.Renter
import com.example.rentisha.viewmodels.RentishaViewModel


class HouseDetailFragment : Fragment() {

    private val viewModel: RentishaViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(this, Injection.provideViewModelFactory(
            context = activity,
            owner = this,
        ))
            .get(RentishaViewModel::class.java)
    }
    private val navigationArgs: HouseDetailFragmentArgs by navArgs()

    private var _binding: FragmentHouseDetailBinding? = null
    private val binding get() = _binding!!
    lateinit var renter: Renter
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
        databaseHouse = viewModel.getHouse(id)
        bind()



    }
    fun bind(){
        binding.apply {
            name.text = databaseHouse.house_type
            title.text = databaseHouse.other_description
            location.text = databaseHouse.latitude.toString()+", "+databaseHouse.longitude.toString()
            descriptions.text = databaseHouse.other_description
            if (databaseHouse.has_watchman){
                vacancy.text = "Currently has watchman"
            }
            else{
                vacancy.text = "Currently has no watchman"

            }
            editHouseFab.setOnClickListener {
                val action = HouseDetailFragmentDirections.actionHouseDetailFragmentToAddHouseFragment(getString(
                    R.string.edit_fragment_title),databaseHouse.houseId)
                findNavController().navigate(action)
            }
            location.setOnClickListener {
                launchMap()
            }

        }
    }
    private fun launchMap() {
        val address = databaseHouse.latitude.toString()+", "+databaseHouse.longitude.toString().let {
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