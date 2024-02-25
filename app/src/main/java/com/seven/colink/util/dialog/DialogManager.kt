package com.seven.colink.util.dialog

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.seven.colink.R
import com.seven.colink.databinding.UtilCustomBasicDialogBinding
import com.seven.colink.databinding.UtilCustomLevelDialogBinding
import com.seven.colink.databinding.UtilCustomListDialogBinding
import com.seven.colink.util.dialog.adapter.DialogAdapter
import com.seven.colink.util.dialog.adapter.LevelDialogAdapter
import com.seven.colink.util.dialog.enum.LevelEnum

fun Context.setDialog(
    title: String,
    message: String,
    image: Int? = null,
    confirmAction: (AlertDialog) -> Unit,
    cancelAction: (AlertDialog) -> Unit
): AlertDialog {
    val binding = UtilCustomBasicDialogBinding.inflate(LayoutInflater.from(this))
    val dialog = AlertDialog.Builder(this, R.style.RoundedDialogTheme)
        .setView(binding.root)
        .create()

    dialog.setOnShowListener {
        setupDialog(
            dialog = dialog,
            binding = binding,
            title = title,
            message = message,
            image = image,
            confirmAction = confirmAction,
            cancelAction = cancelAction
        )
    }

    return dialog
}

private fun setupDialog(
    dialog: AlertDialog,
    binding: UtilCustomBasicDialogBinding,
    title: String,
    image: Int? = null,
    message: String,
    confirmAction: (AlertDialog) -> Unit,
    cancelAction: (AlertDialog) -> Unit
) = with(binding) {
    tvDiaTitle.text = title

    val spannableMessage = SpannableStringBuilder()
    val spannableString = SpannableString(message)
    val start = message.indexOf('\'')
    val end = message.lastIndexOf('\'')

    //''안에 텍스트는 bold처리
    if (start != -1 && end != -1 && start < end) {
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            start,
            end + 1,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    spannableMessage.append(spannableString)

    tvDiaMessage.text = spannableMessage
    btDiaConfirm.setOnClickListener { confirmAction(dialog) }
    btDiaCancel.setOnClickListener { cancelAction(dialog) }

    if (image != null) {
        ivDiaImg.isVisible = true
        ivDiaImg.setImageResource(image)
        tvDiaTitle.textSize = 16F
        tvDiaMessage.textSize = 12F
    } else {
        ivDiaImg.isVisible = false
    }
}

fun List<String>.setDialog(
    context: Context,
    title: String,
    action: (String) -> Unit,
): AlertDialog {
    val binding = UtilCustomListDialogBinding.inflate(LayoutInflater.from(context))
    val dialog = AlertDialog.Builder(context, R.style.RoundedDialogTheme)
        .setView(binding.root)
        .create()

    val adapter = DialogAdapter(
        onClick = action
    )

    adapter.submitList(this)
    binding.rcDiaList.adapter = adapter
    binding.rcDiaList.layoutManager = LinearLayoutManager(context)
    binding.tvDiaTitle.text = title

    return dialog
}

fun Context.setLevelDialog(
    nowLevel: Int = 1,
    action: (Int) -> Unit
):AlertDialog {
    val binding = UtilCustomLevelDialogBinding.inflate(LayoutInflater.from(this))
    val dialog = AlertDialog.Builder(this)
        .setView(binding.root)
        .show()  // 다이얼로그 표시

    dialog.window?.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )

    val adapter = LevelDialogAdapter(
        selected = nowLevel,
        onClick = action,
    )

    adapter.submitList(
        listOf(
            LevelEnum.LEVEL1,
            LevelEnum.LEVEL2,
            LevelEnum.LEVEL3,
            LevelEnum.LEVEL4,
            LevelEnum.LEVEL5,
            LevelEnum.LEVEL6,
            LevelEnum.LEVEL7,
        )
    )
    binding.rcLevelDia.adapter = adapter
    binding.rcLevelDia.layoutManager = LinearLayoutManager(this)
    return dialog
}