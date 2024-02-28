package com.seven.colink.ui.mypage

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import com.seven.colink.R
import com.seven.colink.databinding.MypageCustomDialogBinding
import com.seven.colink.databinding.MypageEditDialogBinding


class MyPageCustomDialog(private val context: Context, MyPageCustomDialogInterface: MyPageCustomDialogInterface): Dialog(context){

    private lateinit var binding: MypageEditDialogBinding

    val TAG: String = "로그"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MypageEditDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }




}