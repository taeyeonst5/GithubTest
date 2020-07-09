package com.allen_chou.githubtest.ui.main

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.allen_chou.githubtest.R
import com.allen_chou.githubtest.api.User
import com.allen_chou.githubtest.extensions.checkNetworkIsConnect
import com.allen_chou.githubtest.extensions.hideSoftKeyboard
import com.allen_chou.githubtest.extensions.logd
import com.allen_chou.githubtest.paging.NetWorkState
import com.allen_chou.githubtest.paging.UserDataSource
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.main_fragment.*


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var inputMethodManager: InputMethodManager

    private lateinit var userAdapter: UserAdapter
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        inputMethodManager =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        edt_search.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                search()
                true
            } else {
                false
            }
        }
        initRecycler()
    }

    private fun initRecycler() {
        userAdapter = UserAdapter()
        rv_user.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userAdapter
        }
    }

    private fun search() {
        if (!requireActivity().checkNetworkIsConnect()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.text_check_network_state),
                Toast.LENGTH_SHORT
            )
                .show()
            return
        }

        edt_search.text.trim().toString().let {
            if (it.isNotEmpty()) {
                userAdapter.submitList(null)
                viewModel.query(it)
                logd("query:$it")

                //need observe after query
                setObserve()

            }
        }

        activity?.hideSoftKeyboard(inputMethodManager)
        edt_search.clearFocus()
    }

    private fun setObserve() {
        viewModel.pagedList.observe(
            viewLifecycleOwner,
            Observer<PagedList<User>> { pagedList ->
                userAdapter.submitList(pagedList)
            })

        viewModel.netWorkLiveData.observe(viewLifecycleOwner, Observer { networkState ->
            when (networkState) {
                is NetWorkState.LOADING -> {
                    logd("observer loading")
                    pg_loading.visibility = View.VISIBLE
                }
                is NetWorkState.FAILED -> {
                    logd("observer failed")
                    showErrorSnackbar(networkState)
                    pg_loading.visibility = View.GONE
                }
                is NetWorkState.FINISH -> {
                    logd("observer finish")
                    if (networkState.dataSize == 0 && networkState.isInitLoad) {
                        showEmptyView()
                    } else {
                        showList()
                    }
                    pg_loading.visibility = View.GONE
                }
            }
        })
    }

    private fun showList() {
        rv_user.visibility = View.VISIBLE
        group_error_ui.visibility = View.GONE
    }

    private fun showEmptyView() {
        rv_user.visibility = View.GONE
        group_error_ui.visibility = View.VISIBLE
    }

    private fun showErrorSnackbar(networkState: NetWorkState.FAILED) {
        val errorMsg = if (networkState.errorMsg == UserDataSource.TEXT_IOE) {
            getString(R.string.text_check_network_state)
        } else {
            networkState.errorMsg
        }
        Snackbar.make(
            main,
            String.format(getString(R.string.text_load_fail_message), errorMsg),
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction(getString(R.string.text_retry)) {
                logd("call retry")
                networkState.retry?.invoke()
            }.show()
    }

}
