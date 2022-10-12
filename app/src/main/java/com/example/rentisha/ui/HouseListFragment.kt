package com.example.rentisha.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.map
import androidx.recyclerview.widget.RecyclerView
import com.example.rentisha.Injection
import com.example.rentisha.R
import com.example.rentisha.database.DatabaseHouse
import com.example.rentisha.databinding.FragmentHouseListBinding
import com.example.rentisha.viewmodels.RentishaViewModel
import com.example.rentisha.viewmodels.UiAction
import com.example.rentisha.viewmodels.UiModel
import com.example.rentisha.viewmodels.UiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


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
        ViewModelProvider(activity, Injection.provideViewModelFactory(
            context = requireContext(),
            owner = activity,
        ))
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

//        val adapter = HouseListAdapter(HouseListener { house ->
//            viewModel.onHouseClicked(house)
//
//
//            val action = HouseListFragmentDirections.actionHouseListFragmentToAddHouseFragment(getString(R.string.edit_fragment_title),house.houseId)
//            this.findNavController().navigate(action)
//
//
//
//
//        },viewModel)
        // bind the state
        binding.bindState(
            uiState = viewModel.state,
            pagingData = viewModel.pagingDataFlow,
            uiActions = viewModel.accept
        )


//        viewLifecycleOwner.lifecycleScope.launch {
//            viewModel.houselist.collect {
//                adapter.submitData(it)
//                Log.d("articles","  Hello")
//            }
//        }

//        binding.recyclerView.adapter = adapter
        binding.addHouseFab.setOnClickListener {
            val action =HouseListFragmentDirections.actionHouseListFragmentToAddHouseFragment(getString(R.string.add_fragment_title))
            findNavController().navigate(action)
        }
        //binding.recyclerView.layoutManager = LinearLayoutManager(context)
        return view
    }

    /**
     * Binds the [UiState] provided  by the [SearchRepositoriesViewModel] to the UI,
     * and allows the UI to feed back user actions to it.
     */
    private fun FragmentHouseListBinding.bindState(
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<UiModel>>,
        uiActions: (UiAction) -> Unit
    ) {
        val houseListAdapter = HouseListAdapter(HouseListener { house ->
            val action = HouseListFragmentDirections.
            actionHouseListFragmentToAddHouseFragment(getString(R.string.edit_fragment_title),house.houseId)
            findNavController().navigate(action)},viewModel!!)
        val header = RentishaLoadStateAdapter { houseListAdapter.retry() }
        recyclerView.adapter = houseListAdapter.withLoadStateHeaderAndFooter(
            header = header,
            footer = RentishaLoadStateAdapter { houseListAdapter.retry() }
        )
        bindSearch(
            uiState = uiState,
            onQueryChanged = uiActions
        )

        bindList(
            header = header,
            houselistAdapter = houseListAdapter,
            uiState = uiState,
            pagingData = pagingData,
            onScrollChanged = uiActions
        )
    }

    private fun FragmentHouseListBinding.bindSearch(
        uiState: StateFlow<UiState>,
        onQueryChanged: (UiAction.Search) -> Unit
    ) {
        searchHouse.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateHouseListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }
        searchHouse.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateHouseListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }

        lifecycleScope.launch {
            uiState
                .map { it.query }
                .distinctUntilChanged()
                .collect(searchHouse::setText)
        }
    }

    private fun FragmentHouseListBinding.updateHouseListFromInput(onQueryChanged: (UiAction.Search) -> Unit) {
        searchHouse.text.trim().let {
            if (it.isNotEmpty()) {
                recyclerView.scrollToPosition(0)
                onQueryChanged(UiAction.Search(query = it.toString()))
            }
        }
    }

    private fun FragmentHouseListBinding.bindList(
        header: RentishaLoadStateAdapter,
        houselistAdapter: HouseListAdapter,
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<UiModel>>,
        onScrollChanged: (UiAction.Scroll) -> Unit
    ) {

       val stringdata = pagingData.map {
                pagingData -> pagingData.map { uiModel -> uiModel.toString()
        }
        }

        Log.d("PagedListFlow",stringdata.toString())
        retryButton.setOnClickListener { houselistAdapter.retry() }
//        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                if (dy != 0) onScrollChanged(UiAction.Scroll(currentQuery = ""))
//            }
//        })
        val notLoading = houselistAdapter.loadStateFlow
            .asRemotePresentationState()
            .map { it == RemotePresentationState.PRESENTED }

//        val hasNotScrolledForCurrentSearch = uiState
//            .map { it.hasNotScrolledForCurrentSearch }
//            .distinctUntilChanged()

//        val shouldScrollToTop = combine(
//            notLoading,
//            hasNotScrolledForCurrentSearch,
//            Boolean::and
//        )
//            .distinctUntilChanged()

        lifecycleScope.launch {
            pagingData.collectLatest(houselistAdapter::submitData)
        }

//        lifecycleScope.launch {
//            shouldScrollToTop.collect { shouldScroll ->
//                if (shouldScroll) recyclerView.scrollToPosition(0)
//            }
//
//        }

        lifecycleScope.launch {
            houselistAdapter.loadStateFlow.collect { loadState ->
                // Show a retry header if there was an error refreshing, and items were previously
                // cached OR default to the default prepend state
                header.loadState = loadState.mediator
                    ?.refresh
                    ?.takeIf { it is LoadState.Error && houselistAdapter.itemCount > 0 }
                    ?: loadState.prepend

                val isListEmpty = loadState.refresh is LoadState.NotLoading && houselistAdapter.itemCount == 0
                // show empty list
                emptyList.isVisible = isListEmpty
                // Only show the list if refresh succeeds, either from the the local db or the remote.
                recyclerView.isVisible =  loadState.source.refresh is LoadState.NotLoading || loadState.mediator?.refresh is LoadState.NotLoading
                // Show loading spinner during initial load or refresh.
                progressBar.isVisible = loadState.mediator?.refresh is LoadState.Loading
                // Show the retry state if initial load or refresh fails.
                retryButton.isVisible = loadState.mediator?.refresh is LoadState.Error && houselistAdapter.itemCount == 0
                // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                errorState?.let {
                    Toast.makeText(
                        requireContext(),
                        "\uD83D\uDE28 Wooops ${it.error}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.layout_menu, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_profile ->{
                val action = HouseListFragmentDirections.actionHouseListFragmentToProfileDetailFragment()
                this.findNavController().navigate(action)
                return true
            }
            else -> super.onOptionsItemSelected(item)

        }

    }



}