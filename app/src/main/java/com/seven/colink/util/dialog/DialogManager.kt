package com.seven.colink.util.dialog

import android.content.Context
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter
import com.seven.colink.R
import com.seven.colink.databinding.UtilCustomBasicDialogBinding
import com.seven.colink.databinding.UtilCustomCalendarDialogBinding
import com.seven.colink.databinding.UtilCustomGroupDialogBinding
import com.seven.colink.databinding.UtilCustomLevelDialogBinding
import com.seven.colink.databinding.UtilCustomListDialogBinding
import com.seven.colink.databinding.UtilCustomScheduleColorDialogBinding
import com.seven.colink.databinding.UtilMemberInfoDialogBinding
import com.seven.colink.domain.entity.UserEntity
import org.threeten.bp.LocalDate
import com.seven.colink.util.dialog.adapter.DialogAdapter
import com.seven.colink.util.dialog.adapter.LevelDialogAdapter
import com.seven.colink.util.dialog.adapter.MemberListAdapter
import com.seven.colink.util.dialog.adapter.ScheduleColorAdapter
import com.seven.colink.util.dialog.enum.ColorEnum
import com.seven.colink.util.dialog.enum.LevelEnum
import com.seven.colink.util.scheduleAlarm
import com.seven.colink.util.status.GroupType

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
    /*    if (start != -1 && end != -1 && start < end) {
            spannableString.setSpan(
                StyleSpan(Typeface.BOLD),
                start,
                end + 1,
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }*/
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
        onClick = action,
        dialog = dialog
    )

    adapter.submitList(this)
    binding.rcDiaList.adapter = adapter
    binding.rcDiaList.layoutManager = LinearLayoutManager(context)
    binding.tvDiaTitle.text = title

    return dialog
}

fun Context.setLevelDialog(
    nowLevel: Int = 1,
    action: (LevelEnum) -> Unit,
): AlertDialog {
    val binding = UtilCustomLevelDialogBinding.inflate(LayoutInflater.from(this))
    val dialog = AlertDialog.Builder(this)
        .setView(binding.root)
        .show()

    dialog.window?.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )

    val adapter = LevelDialogAdapter(
        selected = nowLevel,
        onClick = action,
        dialog = dialog
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

fun List<UserEntity>.setUserInfoDialog(
    context: Context,
    action: (UserEntity, isRefuseButton: Boolean) -> Unit,
): AlertDialog {
    val binding = UtilMemberInfoDialogBinding.inflate(LayoutInflater.from(context))
    val dialog = AlertDialog.Builder(context)
        .setView(binding.root)
        .show()

    dialog.window?.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )

    val adapter = MemberListAdapter { position, user, isRefuseButton ->
        action(user, isRefuseButton)
        dialog.dismiss()
    }

    binding.recyclerViewMember.adapter = adapter
    adapter.submitList(this)

    return dialog
}


fun setUpGroupDialog(
    context: Context,
    groupType: GroupType,
    confirmAction: (AlertDialog) -> Unit,
    cancelAction: (AlertDialog) -> Unit
): AlertDialog {
    val binding = UtilCustomGroupDialogBinding.inflate(LayoutInflater.from(context))
    val dialog = AlertDialog.Builder(context)
        .setView(binding.root)
        .show()

    dialog.window?.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )

    binding.tvDialogDescription.text = context.getString(
        R.string.group_dialog_description, context.getString(
            if (
                groupType == GroupType.PROJECT
            ) R.string.project_kor else R.string.study_kor
        )
    )

    binding.btMoveGroupPage.setOnClickListener { confirmAction(dialog) }
    binding.btFinish.setOnClickListener { cancelAction(dialog) }

    return dialog
}

fun Context.setScheduleColor(
    nowColor: Int = 5,
    action: (ColorEnum) -> Unit,
): AlertDialog {
    val binding = UtilCustomScheduleColorDialogBinding.inflate(LayoutInflater.from(this))
    val dialog = AlertDialog.Builder(this, R.style.RoundedDialogTheme)
        .setView(binding.root)
        .create()

    val window = dialog.window
    window?.attributes?.windowAnimations = R.style.DialogSlide
    window?.setGravity(Gravity.BOTTOM)
    dialog.show()

    val displayMetrics = resources.displayMetrics
    dialog.window?.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        displayMetrics.heightPixels
    )

    val adapter = ScheduleColorAdapter(
        selected = nowColor,
        onClick = action,
        dialog = dialog
    )
    adapter.submitList(
        listOf(
            ColorEnum.UNKNOWN,
            ColorEnum.RED,
            ColorEnum.ORANGE,
            ColorEnum.GREEN,
            ColorEnum.BLUE,
            ColorEnum.PURPLE,
            ColorEnum.GRAY
        )
    )
    binding.rcScheduleColor.adapter = adapter
    binding.rcScheduleColor.layoutManager = LinearLayoutManager(this)
    binding.tvDialogTitle.text = "색상"

    val nowColorEnum = ColorEnum.values().find { it.color == nowColor }
    if (nowColorEnum != null) {
        val colorResId = nowColorEnum.color ?: R.color.main_color
        binding.toolBar.setBackgroundColor(ContextCompat.getColor(this, colorResId))
    }

    return dialog
}

fun Context.setScheduleAlarm(
    nowColor: Int,
    action: (String) -> Unit,
): AlertDialog {
    val binding = UtilCustomScheduleColorDialogBinding.inflate(LayoutInflater.from(this))
    val dialog = AlertDialog.Builder(this, R.style.RoundedDialogTheme)
        .setView(binding.root)
        .create()

    val window = dialog.window
    window?.attributes?.windowAnimations = R.style.DialogSlide
    window?.setGravity(Gravity.BOTTOM)
    dialog.show()

    val displayMetrics = resources.displayMetrics
    dialog.window?.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        displayMetrics.heightPixels
    )

    val adapter = DialogAdapter(
        onClick = action,
        dialog = dialog
    )

    adapter.submitList(scheduleAlarm)
    binding.rcScheduleColor.adapter = adapter
    binding.rcScheduleColor.layoutManager = LinearLayoutManager(this)
    binding.tvDialogTitle.text = "알림"

    val nowColorEnum = ColorEnum.values().find { it.color == nowColor }
    if (nowColorEnum != null) {
        val colorResId = nowColorEnum.color ?: R.color.main_color
        binding.toolBar.setBackgroundColor(ContextCompat.getColor(this, colorResId))
    }

    return dialog
}

fun Context.setUpCalendarDialog(
    date: String?,
    confirmAction: (startDate: String, endDate: String) -> Unit,
    cancelAction: () -> Unit,
): AlertDialog {
    val binding = UtilCustomCalendarDialogBinding.inflate(LayoutInflater.from(this))
    val dialog = AlertDialog.Builder(this)
        .setView(binding.root)
        .show()

    dialog.window?.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )

    val calendarView = binding.calendarView
    calendarView.setHeaderTextAppearance(R.style.CalendarWidgetHeader)
    calendarView.setWeekDayFormatter(ArrayWeekDayFormatter(resources.getTextArray(R.array.custom_weekdays)))

    val dateRange = date?.split("~")
    val startDate = dateRange?.getOrNull(0)?.trim()
    val endDate = dateRange?.getOrNull(1)?.trim()

    val startLocalDate = startDate?.takeIf { it.isNotEmpty() }?.let { LocalDate.parse(it) }
    val endLocalDate = endDate?.takeIf { it.isNotEmpty() }?.let { LocalDate.parse(it) }

    startLocalDate?.let { start ->
        endLocalDate?.let { end ->
            val startMonth = start.withDayOfMonth(1)
            calendarView.setCurrentDate(startMonth)

            var currentDate = start
            while (currentDate <= end) {
                val calendarDay = CalendarDay.from(currentDate)
                calendarView.setDateSelected(calendarDay, true)
                currentDate = currentDate.plusDays(1)
            }
        }
    }

    binding.btCancel.setOnClickListener {
        cancelAction()
        dialog.dismiss()
    }

    binding.btConfirm.setOnClickListener {
        val selectedDates = calendarView.selectedDates
        if (selectedDates.isNotEmpty()) {
            val selectedStartDate = selectedDates.first().date.toString()
            val selectedEndDate = selectedDates.last().date.toString()
            confirmAction(selectedStartDate, selectedEndDate)
        }
        dialog.dismiss()
    }

    return dialog
}
