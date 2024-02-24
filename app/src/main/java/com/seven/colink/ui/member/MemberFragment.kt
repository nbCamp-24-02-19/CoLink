package com.seven.colink.ui.member

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.seven.colink.data.firebase.repository.AuthRepositoryImpl
import com.seven.colink.databinding.FragmentMemberBinding
import kotlinx.coroutines.launch

class MemberFragment : Fragment() {

    private var _binding: FragmentMemberBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val memberViewModel =
            ViewModelProvider(this).get(MemberViewModel::class.java)

        _binding = FragmentMemberBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textNotifications
        memberViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}