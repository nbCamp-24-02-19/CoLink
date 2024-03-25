package com.seven.colink.ui.group.calendar.material

import android.content.Context
import android.graphics.Color
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import com.seven.colink.R
import com.seven.colink.ui.group.calendar.model.ScheduleModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

object CalendarDecorators {
    fun dayDecorator(context: Context): DayViewDecorator {
        return object : DayViewDecorator {
            private val drawable = ContextCompat.getDrawable(context, R.drawable.calendar_selector)
            override fun shouldDecorate(day: CalendarDay): Boolean = true
            override fun decorate(view: DayViewFacade) {
                view.setSelectionDrawable(drawable!!)
            }
        }
    }

    fun todayDecorator(context: Context): DayViewDecorator {
        return object : DayViewDecorator {
            private val backgroundDrawable =
                ContextCompat.getDrawable(context, R.drawable.calendar_circle_today)
            private val today = CalendarDay.today()

            override fun shouldDecorate(day: CalendarDay?): Boolean = day == today

            override fun decorate(view: DayViewFacade?) {
                view?.apply {
                    setBackgroundDrawable(backgroundDrawable!!)
                    addSpan(
                        ForegroundColorSpan(
                            ContextCompat.getColor(
                                context,
                                R.color.main_color
                            )
                        )
                    )
                }
            }
        }
    }

    fun selectedMonthDecorator(context: Context, selectedMonth: Int): DayViewDecorator {
        return object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay): Boolean = day.month != selectedMonth
            override fun decorate(view: DayViewFacade) {
                view.addSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            context,
                            R.color.enabled_date_color
                        )
                    )
                )
            }
        }
    }

    fun sundayDecorator(): DayViewDecorator {
        return object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay): Boolean {
                val calendar = Calendar.getInstance()
                calendar.set(day.year, day.month - 1, day.day)
                return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
            }

            override fun decorate(view: DayViewFacade) {
                view.addSpan(ForegroundColorSpan(Color.BLACK))
            }
        }
    }

    fun saturdayDecorator(): DayViewDecorator {
        return object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay): Boolean {
                val calendar = Calendar.getInstance()
                calendar.set(day.year, day.month - 1, day.day)
                return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
            }

            override fun decorate(view: DayViewFacade) {
                view.addSpan(ForegroundColorSpan(Color.BLACK))
            }
        }
    }

    fun eventDecorator(context: Context, scheduleList: List<ScheduleModel>): DayViewDecorator {
        return object : DayViewDecorator {
            private val eventDates = HashSet<CalendarDay>()

            init {
                scheduleList.forEach { schedule ->
                    schedule.startDate?.let { startDate ->
                        val startDateTime = LocalDate.parse(
                            startDate,
                            DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")
                        )
                        val endDateTime = schedule.endDate?.let { endDate ->
                            LocalDate.parse(
                                endDate,
                                DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")
                            )
                        } ?: startDateTime

                        val datesInRange = getDateRange(startDateTime, endDateTime)
                        eventDates.addAll(datesInRange)
                    }
                }
            }

            override fun shouldDecorate(day: CalendarDay?): Boolean {
                return eventDates.contains(day)
            }

            override fun decorate(view: DayViewFacade) {
                view.addSpan(DotSpan(10F, ContextCompat.getColor(context, R.color.main_color)))
            }

            private fun getDateRange(startDate: LocalDate, endDate: LocalDate): List<CalendarDay> {
                val datesInRange = mutableListOf<CalendarDay>()
                var currentDate = startDate
                while (!currentDate.isAfter(endDate)) {
                    datesInRange.add(
                        CalendarDay.from(
                            currentDate.year,
                            currentDate.monthValue,
                            currentDate.dayOfMonth
                        )
                    )
                    currentDate = currentDate.plusDays(1)
                }
                return datesInRange
            }
        }
    }
}
