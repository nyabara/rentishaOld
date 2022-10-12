package com.example.rentisha.ui.profile

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.rentisha.Injection
import com.example.rentisha.R
import com.example.rentisha.databinding.FragmentRegistrationBinding
import com.example.rentisha.api.Renter
import com.example.rentisha.viewmodels.RentishaViewModel
import retrofit2.Call
import retrofit2.Response

class RegistrationFragment : Fragment() {
    private val navigationArgs: RegistrationFragmentArgs by navArgs()
    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!
    private lateinit var textFirstName: EditText
    private lateinit var textMiddleName: EditText
    private lateinit var textSirName: EditText
    private lateinit var textEmail: EditText
    private lateinit var textPhone: EditText
    private lateinit var textPassword: EditText
    lateinit var renterObject: Renter

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegistrationBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textFirstName = binding.firstNameEditText
        textMiddleName = binding.middleNameEditText
        textSirName = binding.sirNameEditText
        textPhone = binding.phoneEditText
        textEmail = binding.emailEditText
        textPassword = binding.passwordEditText

        textFirstName.setOnKeyListener { view,keyCode, _ -> handleKeyEvent(view, keyCode) }
        textMiddleName.setOnKeyListener { view,keyCode, _ -> handleKeyEvent(view, keyCode) }
        textSirName.setOnKeyListener { view,keyCode, _ -> handleKeyEvent(view, keyCode) }
        textPhone.setOnKeyListener { view,keyCode, _ -> handleKeyEvent(view, keyCode) }
        textEmail.setOnKeyListener { view,keyCode, _ -> handleKeyEvent(view, keyCode) }
        textPassword.setOnKeyListener { view,keyCode, _ -> handleKeyEvent(view, keyCode) }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            registrationFragment = this@RegistrationFragment
        }

        val id = navigationArgs.id
        if (id > 0) {


            val renter= viewModel.getRenter(1)
            renter.enqueue(object : retrofit2.Callback<Renter>{

                override fun onResponse(call: Call<Renter>, response: Response<Renter>) {
                    if (response.isSuccessful){
                        Log.d("response","success")
                        Log.d("responsebody",response.body().toString())
                        val renter = response.body()
                        if (renter != null) {
                            renterObject = renter
                            bindRenter(renterObject)
                        }

                        Log.d("renterObject",renterObject.toString())


                    }
                    else{
                        Log.d("response",response.message())
                    }
                }

                override fun onFailure(call: Call<Renter>, t: Throwable) {
                    Log.d("failure", t.printStackTrace().toString())
                }
            })

            binding.deleteBtn.visibility = View.VISIBLE
            binding.deleteBtn.setOnClickListener {
                //deleteRenter(house)
            }
        } else {
            binding.saveBtn.setOnClickListener {
                //addHouse()
            }
        }
    }




    /**
     * Navigate to the side menu fragment.
     */
    private fun bindRenter(renter: Renter) {
        binding.apply{
            firstNameEditText.setText(renter.firstname, TextView.BufferType.SPANNABLE)
            middleNameEditText.setText(renter.middlename, TextView.BufferType.SPANNABLE)
            sirNameEditText.setText(renter.surname, TextView.BufferType.SPANNABLE)
            emailEditText.setText(renter.email, TextView.BufferType.SPANNABLE)
            phoneEditText.setText(renter.phone,TextView.BufferType.SPANNABLE)
            saveBtn.setOnClickListener {
                updateRenter(1)
            }
        }

    }

    fun goToLoginScreen(){
        addRenter()

    }

    private fun addRenter() {
        if (isValidUser()) {
            viewModel.addRenter(
                textFirstName.text.toString(),
               textMiddleName.text.toString(),
               textSirName.text.toString(),
               textEmail.text.toString(),
               textPhone.text.toString(),
                textPassword.text.toString(),
                1
            )
            findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)
        }
    }
    private fun updateRenter(id: Int) {
        if (isValidUser()) {
            viewModel.updateRenter(id,
                textFirstName.text.toString(),
                textMiddleName.text.toString(),
                textSirName.text.toString(),
                textEmail.text.toString(),
                textPhone.text.toString(),
                textPassword.text.toString(),
                1
            )
            findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)
        }
    }
    private fun isValidUser() = viewModel.isValidUser(
        textEmail.text.toString(),
        textPhone.text.toString()
    )


    private fun handleKeyEvent(view: View, keyCode: Int): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            // Hide the keyboard
            val inputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            return true
        }
        return false
    }


}