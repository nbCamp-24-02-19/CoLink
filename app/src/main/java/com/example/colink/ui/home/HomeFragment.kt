package com.example.colink.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.colink.R
import com.example.colink.databinding.FragmentHomeBinding
import com.example.colink.util.dialog.setDialog
import com.example.colink.util.dialog.setLevelDialog

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onResume() {
        super.onResume()
        requireContext().setDialog(
            title = "안녕하세요",
            message = "'다이얼로그'가 잘 작성 되는지 '확인' 해보는 다이얼로그",
            image = R.drawable.img_dialog_project,
            confirmAction = {dialog -> dialog.dismiss()
                            requireContext().setDialog(
                                title = "반갑고",
                                message = "안녕하고",
                                confirmAction = {dialog -> dialog.dismiss()
                                                requireContext().setLevelDialog { Unit }.show()},
                                cancelAction = {dialog -> dialog.dismiss()}
                            ).show()},
            cancelAction = {dialog -> dialog.dismiss()}
        ).show()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}