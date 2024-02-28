package com.seven.colink.ui.chat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.algolia.search.saas.Client
import com.algolia.search.saas.Query
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.seven.colink.BuildConfig
import com.seven.colink.data.firebase.repository.AuthRepositoryImpl
import com.seven.colink.data.firebase.repository.PostRepositoryImpl
import com.seven.colink.data.firebase.type.DataBaseType
import com.seven.colink.databinding.FragmentChatBinding
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.ui.sign.signin.SignInActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatFragment : Fragment() {

    companion object {
        fun newInstance() = ChatFragment()
    }
    private var _binding: FragmentChatBinding? = null
    private lateinit var viewModel: ChatViewModel

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        initButton()
    }

    private fun initButton() = with(binding) {

        signup.setOnClickListener {
            lifecycleScope.launch {
                startActivity(Intent(requireActivity(), SignInActivity::class.java))
            }
        }

        login.setOnClickListener {
            lifecycleScope.launch {
                val a = AuthRepositoryImpl(FirebaseAuth.getInstance()).getCurrentUser()
                Toast.makeText(requireActivity(), a.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        logout.setOnClickListener {
            lifecycleScope.launch {
                AuthRepositoryImpl(FirebaseAuth.getInstance()).signOut()
            }
        }

        makegle.setOnClickListener {
            lifecycleScope.launch {
                val result = PostRepositoryImpl(FirebaseFirestore.getInstance(), Client(
                    BuildConfig.ALGOLIA_APP_ID,
                    BuildConfig.ALGOLIA_API_KEY
                ).getIndex(DataBaseType.POST.title)).searchQuery("팀원")

                // 검색 결과를 문자열로 변환
                val resultText = result.map { post ->
                    "Title: ${post.title}, description: ${post.description}"
                }

                // 변환된 문자열을 텍스트 뷰에 설정
                binding.testInfo.text = resultText.toString()
            }
        }

        seegle.setOnClickListener {
            val key = "unique_post_key"
            lifecycleScope.launch {
                val post = PostRepositoryImpl(FirebaseFirestore.getInstance(),Client(
                    BuildConfig.ALGOLIA_APP_ID,
                    BuildConfig.ALGOLIA_API_KEY
                ).getIndex(DataBaseType.POST.title)).getPost(key)
                testInfo.text = (post.getOrNull() as PostEntity).title
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}