package com.mughitszufar.notes2.ui.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mughitszufar.notes2.R
import com.mughitszufar.notes2.adapter.ListAdapter
import com.mughitszufar.notes2.databinding.FragmentListBinding
import com.mughitszufar.notes2.model.TodoData
import com.mughitszufar.notes2.ui.add.SwipeToDelete
import com.mughitszufar.notes2.utils.hideKeyBoard
import com.mughitszufar.notes2.viewmodel.SharedViewModel
import com.mughitszufar.notes2.viewmodel.ToDoViewModel
import jp.wasabeef.recyclerview.animators.LandingAnimator


class ListFragment : Fragment(),SearchView .OnQueryTextListener {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private val toDoViewModel: ToDoViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()
    private val adapter:ListAdapter by lazy { ListAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.sharedViewModel = sharedViewModel
        toDoViewModel.getAllData.observe(viewLifecycleOwner, Observer { data ->
            sharedViewModel.checkDataBaseEmpty(data)
            adapter.setData(data)
        })
        setupRecyclerView()
        hideKeyBoard(requireActivity())
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_fragment_menu, menu)

        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_delete_all -> confirmRemoval()
            R.id.menu_priority_high -> {toDoViewModel.sortByHighPriority.observe(this, Observer {
                adapter.setData(it)
            })}
            R.id.menu_priority_low -> {toDoViewModel.sortByLowPriority.observe(this, Observer {

            })}
        }
        return super.onOptionsItemSelected(item)
    }

    private fun confirmRemoval() {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Semua")
            .setMessage("Apakah Anda Yakin Ingin Menghapus Semua Catatan ?")
            .setPositiveButton("Yes"){_,_ ->
                toDoViewModel.deleteAllData()
                Toast.makeText(requireContext(), "Berhasil Dihapus", Toast.LENGTH_SHORT).show()

            }
            .setNegativeButton("No", null)
            .create()
            .show()

    }

    private fun setupRecyclerView() {
        val rv = binding.rvTodo
        rv.adapter = adapter
        rv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rv.itemAnimator = LandingAnimator().apply {
            addDuration = 300
        }

        //swipe delete
        swipeToDelete(rv)

    }

    private fun swipeToDelete(rv: RecyclerView) {
        val swipeDeleteCallBack = object : SwipeToDelete(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val delete = adapter.datalist[viewHolder.adapterPosition]

                //delete item
                toDoViewModel.deleteData(delete)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)

                //restoredelete item
                restoreDeleteItem(viewHolder.itemView, delete)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeDeleteCallBack)
        itemTouchHelper.attachToRecyclerView(rv)
    }

    private fun restoreDeleteItem(view: View, delete: TodoData) {
        val snackbar = Snackbar.make(
                view,
                "Delete: ${delete.title}",
                Snackbar.LENGTH_LONG
        )
        snackbar.setAction("undo"){
            toDoViewModel.insertData(delete)
        }
        snackbar.show()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null){
            searchData(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null){
            searchData(newText)
        }
        return true
    }

    private fun searchData(query: String) {
        val searchQuery = "%$query%"

        toDoViewModel.searchDatabase(searchQuery).observe(viewLifecycleOwner, Observer { list ->
            list?.let {
                adapter.setData(it)
            }
        })
    }
}
