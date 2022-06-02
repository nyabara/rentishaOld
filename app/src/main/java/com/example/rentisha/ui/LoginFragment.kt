package com.example.rentisha.ui

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.rentisha.R
import com.example.rentisha.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var textEmail: EditText
    private lateinit var textPassword:EditText

    //private val sharedViewModel: PlaceViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textEmail = binding.emailEditText
        textPassword = binding.passwordEditText

        textEmail.setOnKeyListener { view,keyCode, _ -> handleKeyEvent(view, keyCode) }
        textPassword.setOnKeyListener { view,keyCode, _ -> handleKeyEvent(view, keyCode) }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
           //viewModel = sharedViewModel
            // TODO: initialize the LoginFragment variables
            loginFragment = this@LoginFragment
        }
    }

    /**
     * Navigate to the side menu fragment.
     */
    fun goToNextScreen() {
        // TODO: Navigate to the HouseLisActivity
        findNavController().navigate(R.id.action_loginFragment_to_houseListFragment)
    }
    fun goToRegistrationScreen(){
        findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
    }

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