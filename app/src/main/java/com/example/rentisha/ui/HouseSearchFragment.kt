package com.example.rentisha.ui

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.rentisha.R
import com.example.rentisha.databinding.FragmentHouseSearchBinding

/**
 * A simple [Fragment] subclass.
 * Use the [HouseSearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HouseSearchFragment : Fragment() {
    private var _binding: FragmentHouseSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var textSearch: EditText
    private lateinit var searchButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHouseSearchBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textSearch = binding.placeOfSearchEditText
        searchButton = binding.searchButton
        
        textSearch.setOnKeyListener { view,keyCode, _ -> handleKeyEvent(view, keyCode) }
        //searchButton.setOnClickListener { searchPlace() }
    }

//    private fun searchPlace() {
//        val stringInEditextSearch =textSearch.text
//        val action =
//            com.example.rentisha.HouseSearchFragmentDirections.actionHouseSearchFragmentToHouseMapListActivity3(
//                stringInEditextSearch.toString(), "Ayoma"
//            )
//        findNavController().navigate(action)
//    }


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



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.layout_menu, menu)

        val layoutButton = menu.findItem(R.id.action_switch_layout)
        setIcon(layoutButton)
    }

    private fun setIcon(menuItem: MenuItem?) {
        if (menuItem == null)
            return

//        menuItem.icon =
//            if (isLinearLayoutManager)
//                ContextCompat.getDrawable(this.requireContext(), R.drawable.ic_grid_layout)
//            else ContextCompat.getDrawable(this.requireContext(), R.drawable.ic_linear_layout)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_switch_layout -> {
//                isLinearLayoutManager = !isLinearLayoutManager
//                chooseLayout()
//                setIcon(item)

                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}