package com.example.rentisha.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rentisha.BaseApplication
import com.example.rentisha.R
import com.example.rentisha.databinding.FragmentHouseListBinding
import com.example.rentisha.databinding.FragmentHouseSearchBinding
import com.example.rentisha.domain.House
import com.example.rentisha.viewmodels.RentishaViewModel
import com.google.android.material.snackbar.Snackbar


class HouseListFragment : Fragment() {
    /**
     * One way to delay creation of the viewModel until an appropriate lifecycle method is to use
     * lazy. This requires that viewModel not be referenced before onActivityCreated, which we
     * do in this Fragment.
     */
    private val viewModel: RentishaViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(this, RentishaViewModel.Factory(activity?.application as BaseApplication))
            .get(RentishaViewModel::class.java)
    }
    private var _binding: FragmentHouseListBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHouseListBinding.inflate(inflater,container,false)
        val view = binding.root

        // Set the lifecycleOwner so DataBinding can observe LiveData
        binding.setLifecycleOwner(viewLifecycleOwner)

        binding.viewModel = viewModel

        val adapter = HouseListAdapter(HouseListener { house ->
            viewModel.onHouseClicked(house)
            //Toast.makeText(this,"$viewModel.place.toString()", Toast.LENGTH_SHORT).show()

//            Snackbar.make(binding.root, house.url, Snackbar.LENGTH_SHORT).show()
            val action = HouseListFragmentDirections.actionHouseListFragmentToHouseDetailFragment(house.id)
            this.findNavController().navigate(action)
//            viewModel.house.observe(viewLifecycleOwner, Observer<House>() { house ->
//                if (house!=null){
//                    this.findNavController().navigate(R.id.action_houseListFragment_to_houseDetailFragment)
//                }
//            })




        })
        viewModel.houselist.observe(viewLifecycleOwner, Observer<List<House>> { houses ->
            houses?.apply {
                adapter.submitList(houses)
            }
        })
        binding.recyclerView.adapter = adapter
        binding.addHouseFab.setOnClickListener {
            findNavController().navigate(
                R.id.action_houseListFragment_to_addHouseFragment
            )
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        // Observer for the network error.
        viewModel.eventNetworkError.observe(viewLifecycleOwner, Observer<Boolean> { isNetworkError ->
            if (isNetworkError) onNetworkError()
        })
        return view
    }

    /**
     * Method for displaying a Toast error message for network errors.
     */
    private fun onNetworkError() {
        if(!viewModel.isNetworkErrorShown.value!!) {
            Toast.makeText(activity, "Network Error", Toast.LENGTH_LONG).show()
            viewModel.onNetworkErrorShown()
        }
    }


}