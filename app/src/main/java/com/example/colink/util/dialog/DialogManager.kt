package com.example.colink.util.dialog

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.example.colink.databinding.UtilCustomBasicDialogBinding
import com.example.colink.databinding.UtilCustomListDialogBinding
import com.example.colink.util.dpToPx

fun Context.setDialog(
    title: String,
    message: String,
    image: Int? = null,
    confirmAction: (AlertDialog) -> Unit,
    cancelAction: (AlertDialog) -> Unit
): AlertDialog {
    val binding = UtilCustomBasicDialogBinding.inflate(LayoutInflater.from(this))
    val dialog = AlertDialog.Builder(this)
        .setView(binding.root)
        .create()

    dialog.setOnShowListener {
        setupDialog(
            context = this,
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
    context: Context,
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
        tvDiaMessage.textSize = 16.dpToPx(context).toFloat()
        tvDiaMessage.textSize = 12.dpToPx(context).toFloat()
    } else {
        ivDiaImg.isVisible = false
    }
}

fun List<Any>.setDialog(
    context: Context,
    title: String,
    list: List<String>,
    confirmAction: (AlertDialog) -> Unit,
): AlertDialog {
    val binding = UtilCustomListDialogBinding.inflate(LayoutInflater.from(context))
    val dialog = AlertDialog.Builder(context)
        .setView(binding.root)
        .create()

//    val adapter = DialogAdapter()

    return dialog
}