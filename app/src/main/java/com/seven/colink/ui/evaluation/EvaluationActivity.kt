package com.seven.colink.ui.evaluation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import androidx.core.view.size
import androidx.viewpager2.widget.ViewPager2
import com.seven.colink.databinding.ActivityEvaluationBinding

class EvaluationActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityEvaluationBinding.inflate(layoutInflater)
    }

    private lateinit var evalAdapter: EvaluationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val dataList = mutableListOf(
            EvaluationData.EvalProject("프로젝트를 함께 한 '이순신'님을 평가해주세요."),
            EvaluationData.EvalProject("프로젝트를 함께 한 '세종대왕'님을 평가해주세요."),
            EvaluationData.EvalProject("프로젝트를 함께 한 '징기즈칸'님을 평가해주세요."),
            EvaluationData.EvalStudy("스터디를 함께 한 '링컨'님을 평가해주세요."),
            EvaluationData.EvalStudy("스터디를 함께 한 '손흥민'님을 평가해주세요.")
        )

        evalAdapter = EvaluationAdapter(dataList.toMutableList())
        binding.vpEvalViewpager.adapter = evalAdapter
        binding.vpEvalViewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val pageCount = 5
        binding.pbEvalProgress.max = pageCount -1

        binding.vpEvalViewpager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int){
                binding.pbEvalProgress.progress = position
            }
        })

    }

}