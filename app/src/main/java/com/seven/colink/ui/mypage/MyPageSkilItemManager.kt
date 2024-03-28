package com.seven.colink.ui.mypage

import com.seven.colink.R
import com.seven.colink.ui.mypage.adapter.MyPageSkilAdapter
import com.seven.colink.util.skillCategory

object MyPageSkilItemManager {

//    const val DEFAULT_ID:Int = 0
//    const val PLUS_ID: Int = -1



    private lateinit var skiladapter: MyPageSkilAdapter
    private var skilMyPageItem = listOf(
        MyPageItem.skilItems(1,"코틀린",R.drawable.ic_kotlin)
    )
    private val pulsMyPageItem = MyPageItem.plusItems(skilMyPageItem.size +1, R.drawable.ic_add_24)

    private fun updateSkill(user: MyPageUserModel) {
        skiladapter.changeDataset(user.skill?.map { MyPageItem.skilItems(skillCategory.indexOf(it), it, addItem(it))  }
            ?.plus(MyPageItem.plusItems(99,R.drawable.ic_add_24)) ?: emptyList())
    }

    fun getAllItem(): List<MyPageItem> = skilMyPageItem + pulsMyPageItem


    fun addItem(language: String) =
         when(language){
            "Adobe Photoshop" -> {
                R.drawable.ic_ps
            }
            "Adobe Illustrator" ->{
                R.drawable.ic_ai
            }
            "Adobe XD" ->{
                R.drawable.ic_xd
            }
            "Bash" ->{
                R.drawable.ic_bash
            }
            "Blender" ->{
                R.drawable.ic_blender
            }
            "C" ->{
                R.drawable.ic_c
            }
            "C#" ->{
                R.drawable.ic_csharp
            }
            "C++" ->{
                R.drawable.ic_cplus
            }
            "CorelDRAW" ->{
                 R.drawable.ic_coreldraw
            }
            "Figma" ->{
                R.drawable.ic_figma
            }
            "Go" ->{
                R.drawable.ic_go
            }
            "Groovy" ->{
                R.drawable.ic_groovy
            }
            "HTML" ->{
                R.drawable.ic_html
            }
            "Java" ->{
                R.drawable.ic_java
            }
            "JavaScript" ->{
                R.drawable.ic_javascript
            }
            "Kubernetes" ->{
                R.drawable.ic_kubernetes
            }
            "Lua" ->{
                R.drawable.ic_lua
            }
            "MATLAB" ->{
                R.drawable.ic_matlab
            }
            "Maya" ->{
                R.drawable.ic_maya
            }
            "Objective-C" ->{
                R.drawable.ic_objectivec
            }
            "PHP" ->{
                R.drawable.ic_php
            }
            "PowerShell" ->{
                R.drawable.ic_powershell
            }
            "Python" ->{
                R.drawable.ic_python
            }
            "R" ->{
                R.drawable.ic_r
            }
            "Ruby" ->{
                R.drawable.ic_ruby
            }
            "Rust" ->{
                R.drawable.ic_rust
            }
            "SAS" ->{
                R.drawable.ic_sas
            }
            "Shell" ->{
                R.drawable.ic_shell
            }
            "Sketch" ->{
                R.drawable.ic_sketch
            }
            "SQL" ->{
                R.drawable.ic_sql
            }
            "Substance Designer/Painter" ->{
                R.drawable.ic_substance
            }
            "Swift" ->{
                R.drawable.ic_swift
            }
            "TypeScript" ->{
            R.drawable.ic_ts
            }
            "Dart" ->{
                R.drawable.ic_dart
            }
            "VBA" ->{
                R.drawable.ic_vba
            }
             "Kotlin" ->{
                 R.drawable.ic_kotlin
             }
            "기타(etc)" ->{
                R.drawable.ic_etc
            }else ->{
                0
        }
    }



}