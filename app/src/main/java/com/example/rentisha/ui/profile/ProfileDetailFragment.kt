package com.example.rentisha.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.rentisha.Injection
import com.example.rentisha.databinding.FragmentProfileDetailBinding
import com.example.rentisha.api.Renter
import com.example.rentisha.viewmodels.RentishaViewModel
import retrofit2.Call
import retrofit2.Response


class ProfileDetailFragment : Fragment() {

    private val viewModel: RentishaViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(this, Injection.provideViewModelFactory(
            context = requireContext(),
            owner = this,
        ))
            .get(RentishaViewModel::class.java)
    }
    //private val navigationArgs: Pr by navArgs()

    private var _binding: FragmentProfileDetailBinding? = null
    private val binding get() = _binding!!
    lateinit var renterObject: Renter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileDetailBinding.inflate(inflater, container, false)
        val view = binding.root
        return view

        binding.setLifecycleOwner(viewLifecycleOwner)

        binding.viewModel = viewModel

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.editHouseFab.setOnClickListener {
            val action = ProfileDetailFragmentDirections.actionProfileDetailFragmentToRegistrationFragment(1)
            findNavController().navigate(action)
        }
        val renter= viewModel.getRenter(1)
        renter.enqueue(object : retrofit2.Callback<Renter>{

            override fun onResponse(call: Call<Renter>, response: Response<Renter>) {
                if (response.isSuccessful){
                    Log.d("response","success")
                    Log.d("responsebody",response.body().toString())
                    val renter = response.body()
                    if (renter != null) {
                        renterObject = renter
                        bind()
                    }
                    Log.d("renterObject",renterObject.toString())


                }else{
                    Log.d("response",response.message())
                }
            }

            override fun onFailure(call: Call<Renter>, t: Throwable) {
                Log.d("failure", t.printStackTrace().toString())
            }
        })
    }
    fun bind() {
        binding.apply {
            name.text =
                renterObject.firstname + " " + renterObject.middlename + " " + renterObject.surname
            email.text = renterObject.email
            phone.text = renterObject.phone
            val statusde = renterObject.status

            if (statusde == 1) {
                status.text = "Active"
            } else {
                status.text = "Inactive"
            }

            editHouseFab.setOnClickListener {
                val action =
                    ProfileDetailFragmentDirections.actionProfileDetailFragmentToRegistrationFragment(
                        1)
                findNavController().navigate(action)
            }

        }
    }


}