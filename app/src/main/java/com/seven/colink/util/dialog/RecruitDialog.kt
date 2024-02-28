package com.seven.colink.util.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.seven.colink.R
import com.seven.colink.databinding.UtilCustomRecruitDialogBinding
import com.seven.colink.domain.entity.RecruitInfo
import com.seven.colink.util.Constants.Companion.LIMITED_PEOPLE
import com.seven.colink.util.PersonnelUtils
import com.seven.colink.util.showToast

class RecruitDialog(
    private val personnel: Int,
    private val recruitTypes: List<String>,
    private val onConfirmed: (RecruitInfo) -> Unit,
    private val onCancelled: () -> Unit
) : DialogFragment() {
    private var _binding: UtilCustomRecruitDialogBinding? = null
    private val binding: UtilCustomRecruitDialogBinding get() = _binding!!
    private var selectedPersonnelCount = 0
    private var totalPersonnelCount = personnel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = UtilCustomRecruitDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.apply {
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            isCancelable = true
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun initView() = with(binding) {
        setupSpinner()
        setupButtons()

        tvTotalPersonnel.text = getString(R.string.total_personnel, personnel)
        includeStepperPersonnel.tvRecruitPersonnel.text = selectedPersonnelCount.toString()
        tvSelectedPersonnel.text = getString(R.string.personnel, selectedPersonnelCount)

        includeStepperPersonnel.ivPlusPersonnel.setOnClickListener {
            incrementCount()
        }
        includeStepperPersonnel.ivMinusPersonnel.setOnClickListener {
            decrementCount()
        }
    }

    private fun updateCountTextView() = with(binding) {
        tvSelectedPersonnel.text = getString(R.string.personnel, selectedPersonnelCount)
        includeStepperPersonnel.tvRecruitPersonnel.text = "$selectedPersonnelCount"
        tvTotalPersonnel.text = getString(R.string.total_personnel, totalPersonnelCount)
    }

    private fun performCountOperation(operation: (Int, Int) -> Pair<Int, Int>) {
        val (updateSelectedCount, updateTotalCount) = operation(
            selectedPersonnelCount,
            totalPersonnelCount
        )
        selectedPersonnelCount = updateSelectedCount
        totalPersonnelCount = updateTotalCount
        updateCountTextView()
    }

    private fun incrementCount() {
        performCountOperation { selected, total ->
            PersonnelUtils.incrementCount(selected, total, LIMITED_PEOPLE)
        }
    }

    private fun decrementCount() {
        performCountOperation { selected, total ->
            PersonnelUtils.decrementCount(selected, total)
        }
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.recruit_dialog_items,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRecruit.adapter = adapter
    }

    private fun setupButtons() = with(binding) {
        btNegative.setOnClickListener {
            onCancelled()
            dismiss()
        }
        btPositive.setOnClickListener {
            val spinnerSelectedItem = binding.spinnerRecruit.selectedItem.toString()

            when {
                recruitTypes.contains(spinnerSelectedItem) -> {
                    requireContext().showToast(getString(R.string.recruit_dialog_type_already_exists))
                }

                selectedPersonnelCount > 0 -> {
                    onConfirmed(
                        RecruitInfo(
                            type = spinnerSelectedItem,
                            maxPersonnel = selectedPersonnelCount
                        )
                    )
                    dismiss()
                }

                else -> {
                    requireContext().showToast(getString(R.string.recruit_dialog_message))
                }
            }
        }
    }

}