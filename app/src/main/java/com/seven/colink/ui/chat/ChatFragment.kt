package com.seven.colink.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.seven.colink.data.firebase.repository.AuthRepositoryImpl
import com.seven.colink.data.firebase.repository.PostRepositoryImpl
import com.seven.colink.data.firebase.repository.UserRepositoryImpl
import com.seven.colink.databinding.FragmentChatBinding
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.ui.post.model.RecruitInfo
import com.seven.colink.util.status.GroupType
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
    ): View? {
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
                val a = AuthRepositoryImpl(FirebaseAuth.getInstance()).register("zxcasd@wdsad.com", "tlarbtkd")
                UserRepositoryImpl(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance()).registerUser(UserEntity(
                    uid = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                    email = "zxcasd@wdsad.com",
                    password = "tlarbtkd",
                    id = "ID123",
                    name = "John Doe",
                    photoUrl = "https://example.com/photo.jpg",
                    phoneNumber = "123-456-7890",
                    level = 5,
                    specialty = "Mobile Development",
                    grade = 4.5,
                    skill = listOf("Kotlin", "Swift", "Flutter"),
                    blog = listOf("https://blog.example.com/post1", "https://blog.example.com/post2"),
                    info = "Experienced mobile developer",
//                    joinDate = "2022-01-01"
                ))
            }
        }

        login.setOnClickListener {
            lifecycleScope.launch {
//                val id = AuthRepositoryImpl(FirebaseAuth.getInstance()).signIn("zxcasd@wdsad.com", "tlarbtkd")
//                val user = UserRepositoryImpl(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance()).getUserDetails(id?.uid ?: "error")
//                withContext(Dispatchers.Main) {
//                    if (user!= null) {
//                        testInfo.text = user.toString()
//                    } else {
//                        Toast.makeText(context, "등록된 사용자가 없습니다", Toast.LENGTH_SHORT).show()
//                    }
//                }
            }
        }

        logout.setOnClickListener {
            lifecycleScope.launch {
                AuthRepositoryImpl(FirebaseAuth.getInstance()).signOut()
            }
        }

        makegle.setOnClickListener {
            val newPost = PostEntity(
                key = "unique_post_key",
                authId = "user123",
                title = "My First Post",
                imageUrl = "https://example.com/image.jpg",
                groupType = GroupType.PROJECT,
                description = "This is a sample post description.",
                tags = listOf("sample", "firstPost"),
                precautions = "Be kind to everyone.",
                recruitInfo = "Looking for a designer",
                recruit = RecruitInfo("Designer", 2, 1, 0),
//                datetime = "2023-10-02T12:00:00",
                views = 0,
                startDate = "2023-10-02",
                endDate = "2023-12-31"
            )

            lifecycleScope.launch {
                val a = PostRepositoryImpl(FirebaseFirestore.getInstance()).registerPost(newPost)
                Toast.makeText(context, a.message , Toast.LENGTH_SHORT).show()
            }
        }

        seegle.setOnClickListener {
            val key = "unique_post_key"
            lifecycleScope.launch {
                val post = PostRepositoryImpl(FirebaseFirestore.getInstance()).getPost(key)
                testInfo.text = post.toString()
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}