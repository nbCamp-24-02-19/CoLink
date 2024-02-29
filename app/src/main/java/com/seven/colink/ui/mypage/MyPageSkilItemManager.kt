package com.seven.colink.ui.mypage

import com.seven.colink.R

object MyPageSkilItemManager {

//    const val DEFAULT_ID:Int = 0
//    const val PLUS_ID: Int = -1

    private var skilMyPageItem = listOf(
        MyPageItem.skilItems(1,"코틀린",R.drawable.ic_kotlin)
    )
    private val pulsMyPageItem = MyPageItem.plusItems(skilMyPageItem.size +1, R.drawable.ic_add_24)

    fun getAllItem(): List<MyPageItem> = skilMyPageItem + pulsMyPageItem

    fun addItem(language: String){
        var icon = R.drawable.ic_kotlin
        when(language){
            "Adobe Photoshop" -> {
                icon = R.drawable.ic_ps
            }
            "Adobe Illustrator" ->{
                icon = R.drawable.ic_ai
            }
            "Adobe XD" ->{
                icon = R.drawable.ic_xd
            }
            "Bash" ->{
                icon = R.drawable.ic_bash
            }
            "Blender" ->{
                icon = R.drawable.ic_blender
            }
            "C" ->{
                icon = R.drawable.ic_c
            }
            "C#" ->{
                icon = R.drawable.ic_csharp
            }
            "C++" ->{
                icon = R.drawable.ic_cplus
            }
            "CorelDRAW" ->{
                icon = R.drawable.ic_coreldraw
            }
            "Figma" ->{
                icon = R.drawable.ic_figma
            }
            "Go" ->{
                icon = R.drawable.ic_go
            }
            "Groovy" ->{
                icon = R.drawable.ic_groovy
            }
            "HTML" ->{
                icon = R.drawable.ic_html
            }
            "Java" ->{
                icon = R.drawable.ic_java
            }
            "JavaScript" ->{
                icon = R.drawable.ic_javascript
            }
            "Kubernetes" ->{
                icon = R.drawable.ic_kubernetes
            }
            "Lua" ->{
                icon = R.drawable.ic_lua
            }
            "MATLAB" ->{
                icon = R.drawable.ic_matlab
            }
            "Maya" ->{
                icon = R.drawable.ic_maya
            }
            "Objective-C" ->{
                icon = R.drawable.ic_objectivec
            }
            "PHP" ->{
                icon = R.drawable.ic_php
            }
            "PowerShell" ->{
                icon = R.drawable.ic_powershell
            }
            "Python" ->{
                icon = R.drawable.ic_python
            }
            "R" ->{
                icon = R.drawable.ic_r
            }
            "Ruby" ->{
                icon = R.drawable.ic_ruby
            }
            "Rust" ->{
                icon = R.drawable.ic_rust
            }
            "SAS" ->{
                icon = R.drawable.ic_sas
            }
            "Shell" ->{
                icon = R.drawable.ic_shell
            }
            "Sketch" ->{
                icon = R.drawable.ic_sketch
            }
            "SQL" ->{
                icon = R.drawable.ic_sql
            }
            "Substance Designer/Painter" ->{
                icon = R.drawable.ic_substance
            }
            "Swift" ->{
                icon = R.drawable.ic_swift
            }
            "TypeScript" ->{
                icon = R.drawable.ic_ts
            }
            "Dart" ->{
                icon = R.drawable.ic_dart
            }
            "VBA" ->{
                icon = R.drawable.ic_vba
            }
            "기타(etc)" ->{
                icon = R.drawable.ic_etc
            }

        }
        skilMyPageItem = skilMyPageItem + MyPageItem.skilItems(skilMyPageItem.size+1,language,icon)
    }
    fun removeItem(language: String){
        skilMyPageItem = skilMyPageItem.filter { it.language != language }
    }

}