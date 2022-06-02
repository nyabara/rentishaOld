package com.example.rentisha.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.rentisha.BaseApplication
import com.example.rentisha.R
import com.example.rentisha.database.DatabaseHouse
import com.example.rentisha.databinding.FragmentAddHouseBinding
import com.example.rentisha.viewmodels.RentishaViewModel


class AddHouseFragment : Fragment() {

    private val navigationArgs: AddHouseFragmentArgs by navArgs()
    private var _binding: FragmentAddHouseBinding? = null

    private lateinit var house: DatabaseHouse

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: RentishaViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(this, RentishaViewModel.Factory(activity?.application as BaseApplication))
            .get(RentishaViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddHouseBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = navigationArgs.id
        if (id > 0) {

            // TODO: Observe a Forageable that is retrieved by id, set the forageable variable,
            //  and call the bindForageable method
            viewModel.getHouse(id).observe(viewLifecycleOwner, Observer {
                house = it
                bindHouse(house)
            })

            binding.deleteBtn.visibility = View.VISIBLE
            binding.deleteBtn.setOnClickListener {
                deleteHouse(house)
            }
        } else {
            binding.saveBtn.setOnClickListener {
                addHouse()
            }
        }
    }

    private fun deleteHouse(house: DatabaseHouse) {
        viewModel.deleteHouse(house)
        findNavController().navigate(
            R.id.action_addHouseFragment_to_houseListFragment
        )
    }

    private fun addHouse() {
        if (isValidEntry()) {
            viewModel.addHouse(
                binding.titleInput.text.toString(),
                binding.descriptionInput.text.toString(),
                binding.urlInput.text.toString(),
                binding.updatedInput.text.toString(),
                binding.nameInput.text.toString(),
                binding.isAvailableCheckbox.isChecked,
                binding.locationAddressInput.text.toString(),
                binding.thumbnailInput.text.toString()
            )
            findNavController().navigate(
                R.id.action_addHouseFragment_to_houseListFragment
            )
        }
    }

    private fun updateHouse() {
        if (isValidEntry()) {
            viewModel.updateHouse(
                id = navigationArgs.id,
                name = binding.nameInput.text.toString(),
                address = binding.locationAddressInput.text.toString(),
                title = binding.titleInput.text.toString(),
                description = binding.descriptionInput.text.toString(),
                url = binding.urlInput.text.toString(),
                isAvailable = binding.isAvailableCheckbox.isChecked,
                thumbnail = binding.thumbnailInput.text.toString(),
            updated = binding.updatedInput.text.toString())
            findNavController().navigate(
                R.id.action_addHouseFragment_to_houseListFragment
            )
        }
    }

    private fun bindHouse(house:DatabaseHouse) {
        binding.apply{
            nameInput.setText(house.name, TextView.BufferType.SPANNABLE)
            locationAddressInput.setText(house.address, TextView.BufferType.SPANNABLE)
            isAvailableCheckbox.isChecked = house.isAvailable
            descriptionInput.setText(house.description, TextView.BufferType.SPANNABLE)
            titleInput.setText(house.title,TextView.BufferType.SPANNABLE)
            saveBtn.setOnClickListener {
                updateHouse()
            }
        }

    }

    private fun isValidEntry() = viewModel.isValidEntry(
        binding.nameInput.text.toString(),
        binding.locationAddressInput.text.toString()
    )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}