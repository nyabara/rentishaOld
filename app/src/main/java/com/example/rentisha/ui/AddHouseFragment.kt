package com.example.rentisha.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.rentisha.Injection
import com.example.rentisha.R
import com.example.rentisha.database.DatabaseHouse
import com.example.rentisha.databinding.FragmentAddHouseBinding
import com.example.rentisha.domain.ElectricityTypes
import com.example.rentisha.domain.HouseTypes
import com.example.rentisha.util.REQUEST_PERMISSION
import com.example.rentisha.viewmodels.RentishaViewModel
import kotlinx.android.synthetic.main.fragment_add_house.*


class AddHouseFragment : Fragment() {
    private val navigationArgs: AddHouseFragmentArgs by navArgs()
    private var _binding: FragmentAddHouseBinding? = null
    private val binding get() = _binding!!
    val imageArrayList = arrayListOf<String>()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentAddHouseBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //val imageArrayList= arrayListOf<String>()

        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_PERMISSION)
        }
        Log.d("staticText", "entered start")
        //Getting list of houseTypes and populating the spinner
        val houseTypes = viewModel.getHouseTypeRepoAsync()
        Log.d("houseTypesAsync", houseTypes.toString())

        val electricityTypes = viewModel.getElectricityTypeRepoAsync()
        val adapterHouses = ArrayAdapter<HouseTypes>(requireContext(),
            android.R.layout.simple_spinner_item,
            houseTypes)
        adapterHouses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerHouseTypes.adapter = adapterHouses
        Log.d("staticText", "entered here")

        Log.d("staticText", "entered end")
        //getting list of electricityTypes and populating the spinner
        val adapterElectricityTypes =
            ArrayAdapter<ElectricityTypes>(requireContext(), android.R.layout.simple_spinner_item,
                electricityTypes)
        adapterElectricityTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerElectricityTypes.adapter = adapterElectricityTypes

        binding.uploadImage.setOnClickListener {
            val intent = Intent()
            intent.setType("image/*")
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1)
        }

        binding.locationAddressInput.setOnClickListener {
            findNavController().navigate(R.id.action_addHouseFragment_to_mapFragment)
        }
        val id = navigationArgs.id
        val latitude = navigationArgs.latitude.toDouble()
        val longitude = navigationArgs.longitude.toDouble()
        binding.locationAddressInput.setText("Get Adress: " + latitude.toString() + ", " + longitude,
            TextView.BufferType.SPANNABLE)



        if (id > 0) {
            //getting house details
            val house = viewModel.getHouse(id)
            //Getting HashMap of houseTypes
            val mapHouseTypes = viewModel.getHouseTypesAsMap(houseTypes)

            //Getting  HashMap of electricityTyPes
            val mapElectricityTypes = viewModel.getElectricityTypeAsMap(electricityTypes)
            //Calling a function that binds the house
            bindHouse(house, mapHouseTypes, mapElectricityTypes)

            binding.deleteBtn.visibility = View.VISIBLE
            binding.deleteBtn.setOnClickListener {
                deleteHouse(house)
            }
        } else {
            binding.saveBtn.setOnClickListener {
                addHouse()
                //Log.d("",imageArrayList.toTypedArray().toString())
                //viewModel.saveImageToFile(imageArrayList.toTypedArray())
            }
        }
    }

    private fun bindHouse(
        house: DatabaseHouse? = null, mapHouseTypes: HashMap<Int, HouseTypes>? = null,
        mapElectricityTypes: HashMap<Int, ElectricityTypes>? = null,
    ) {
        binding.apply {
            val houseType = house?.let { mapHouseTypes?.get(it.house_type_id) }
            Log.d("HouseType : ", houseType.toString())
            Log.d("mapHouseTypes", mapHouseTypes.toString())
            val housePosition = mapHouseTypes?.values?.indexOf(houseType)
            if (housePosition != null) {
                spinnerHouseTypes.setSelection(housePosition)
            }
            val electricityType = house?.let { mapElectricityTypes?.get(it.electricity_type_id) }
            Log.d("electricityType", electricityType.toString())
            Log.d("mapElectricityType", mapElectricityTypes.toString())
            val electricityHousePosition = mapElectricityTypes?.values?.indexOf(electricityType)
            if (electricityHousePosition != null) {
                spinnerElectricityTypes.setSelection(electricityHousePosition)
            }

            if (house != null) {
                locationAddressInput.setText(house.latitude.toString() + ", " + house.longitude,
                    TextView.BufferType.SPANNABLE)
                hasWater.isChecked = house.has_water
                hasWatchman.isChecked = house.has_watchman
                carParking.isChecked = house.car_booking
                compoundSwitch.isChecked = house.own_compound
                electricityDescriptionInput.setText(house.electricity_description,
                    TextView.BufferType.SPANNABLE)
                otherDescriptionInput.setText(house.other_description,
                    TextView.BufferType.SPANNABLE)
                //Log.d("otherdescription", house.other_description!!)
                if (house.status == 1) {
                    optionActive.isChecked
                } else {
                    optionDeactivate.isChecked
                }

            }


            saveBtn.setOnClickListener {
                updateHouse()


            }
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data?.clipData != null) {
                var x: Int = data.clipData!!.itemCount
                for (i in 0 until x) {
                    imageArrayList.add(data.clipData!!.getItemAt(i).uri.toString())

                }
                //viewModel.createInputDataForUri(imageArrayList.toTypedArray())
                Log.d("imageArrayList", imageArrayList.toString())
            } else if (data?.data != null) {
                var imageUrl = data.data!!.path
                if (imageUrl != null) {
                    imageArrayList.add(imageUrl)
                }
            }
        }
    }


    private fun isValidEntry() = viewModel.isValidEntry(navigationArgs.latitude.toDouble(),
        navigationArgs.longitude.toDouble(),
        spinnerHouseTypes.selectedItemPosition,
        spinnerElectricityTypes.selectedItemPosition
    )

    private fun addHouse() {
        if (isValidEntry()) {
            val electricityType = spinnerElectricityTypes.selectedItem as ElectricityTypes
            val electricitytypeid = electricityType.id
            val houseType = spinnerHouseTypes.selectedItem as HouseTypes
            val houseTypeId = houseType.id
            viewModel.addHouse(electricitytypeid,
                houseTypeId,
                binding.carParking.isChecked,
                "",
                "",
                binding.hasWatchman.isChecked,
                binding.hasWater.isChecked,
                "",
                navigationArgs.latitude.toDouble(),
                navigationArgs.longitude.toDouble(),
                "",
                1,
                binding.compoundSwitch.isChecked,
                1)
            Log.d("checkHouseType", houseTypeId.toString())
        }
    }

    private fun updateHouse() {
        if (isValidEntry()) {
            viewModel.updateHouse(navigationArgs.id,
                spinnerElectricityTypes.selectedItemPosition,
                spinnerHouseTypes.selectedItemPosition,
                binding.carParking.isChecked,
                "",
                "",
                binding.hasWatchman.isChecked,
                binding.hasWater.isChecked,
                "",
                navigationArgs.latitude.toDouble(),
                navigationArgs.longitude.toDouble(),
                "",
                1,
                binding.compoundSwitch.isChecked,
                1)
            Log.d("checkHouseType", spinnerHouseTypes.selectedItemPosition.toString())
        }
    }


    private fun deleteHouse(house: DatabaseHouse) {
        viewModel.deleteHouse(house)
        findNavController().navigate(
            R.id.action_addHouseFragment_to_houseListFragment
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}