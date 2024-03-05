package com.seven.colink.ui.mypage

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.storage
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.PostRepository
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.ui.sign.signup.model.SignUpUserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.HashMap
import java.util.Objects
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val postRepository: PostRepository
): ViewModel() {

    private val _userDetails = MutableLiveData<MyPageUserModel>()
    private val _userPosts = MutableLiveData<List<MyPagePostModel>>()
    val userDetails: LiveData<MyPageUserModel> = _userDetails
    val userPost: LiveData<List<MyPagePostModel>> = _userPosts
    init {
        loadUserDetails()
        loadUserPost()
    }
    private fun loadUserDetails() {
        viewModelScope.launch {
            val result = userRepository.getUserDetails(authRepository.getCurrentUser().message)
            result.onSuccess { user ->
                _userDetails.postValue(user?.convertUserEntity())
            }.onFailure { exception ->
                Log.e("ViewModel", "Error fetching user details", exception)
            }
        }
    }

    private fun loadUserPost() {
        viewModelScope.launch {
            val result = postRepository.getPostByAuthId(authRepository.getCurrentUser().message)
            result.onSuccess { post ->
                _userPosts.postValue(post.map { it.convertPostEntity() })
            }
        }
    }

    fun updateSkill(skill: String){
       viewModelScope.launch {
           val result = userRepository.getUserDetails(authRepository.getCurrentUser().message)
           result.onSuccess {
               it?.copy(skill = it.skill?.plus(skill)?.distinct())
                   ?.let { it1 -> userRepository.registerUser(it1) }
               loadUserDetails()
           }
       }
    }

    fun removeSkill(skill: String){
//        viewModelScope.launch {
//            val result = userRepository.getUserDetails(authRepository.getCurrentUser().message)
//            val db = FirebaseFirestore.getInstance()
//            val docRef: DocumentReference = db.collection("users").document(result.toString())
//            val updates =  hashMapOf< String , Any>(skill to FieldValue.delete())
//            docRef.update(updates).addOnCompleteListener {  }
//            loadUserDetails()
//        }


//        viewModelScope.launch{
//            val result = userRepository.getUserDetails(authRepository.getCurrentUser().message)
//            val db = FirebaseFirestore.getInstance()
//            result.onSuccess {
//                db.collection("users").document(result.toString()).delete()//???이러면 스킬이 지워지는게 아닌게 아닌지??..?
//            }
//        }


    }

    private fun PostEntity.convertPostEntity() = MyPagePostModel(
        key = key,
        title = title,
        ing = status,
        grouptype = groupType,
        time = registeredDate
    )

    // TODO: Implement the ViewModel

    private fun UserEntity.convertUserEntity() = MyPageUserModel(
        name = name,
        email = email,
        profile = photoUrl,
        mainSpecialty = mainSpecialty,
        specialty = specialty,
        skill = skill,
        level = level,
        info = info,
        git = git,
        blog = blog,
        link = link,
        score = grade
    )

}