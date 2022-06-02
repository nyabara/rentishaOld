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
import androidx.navigation.fragment.findNavController
import com.example.rentisha.R
import com.example.rentisha.databinding.FragmentLoginBinding
import com.example.rentisha.databinding.FragmentRegistrationBinding

class RegistrationFragment : Fragment() {
    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!
    private lateinit var textFirstName: EditText
    private lateinit var textMiddleName: EditText
    private lateinit var textSirName: EditText
    private lateinit var textEmail: EditText
    private lateinit var textPhone: EditText
    private lateinit var textPassword: EditText


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
    }

    /**
     * Navigate to the side menu fragment.
     */

    fun goToLoginScreen(){
        findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)
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