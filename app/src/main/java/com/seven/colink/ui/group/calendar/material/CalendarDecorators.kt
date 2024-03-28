package com.seven.colink.ui.group.calendar.material

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.ForegroundColorSpan
import android.text.style.LineBackgroundSpan
import androidx.core.content.ContextCompat
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.seven.colink.R
import java.util.Calendar

object CalendarDecorators {
    fun dayDecorator(context: Context, background: Int): DayViewDecorator {
        return object : DayViewDecorator {
            private val drawable = ContextCompat.getDrawable(context, background)
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

    fun sundayDecorator(context: Context): DayViewDecorator {
        return object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay): Boolean {
                val calendar = Calendar.getInstance()
                calendar.set(day.year, day.month - 1, day.day)
                return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
            }

            override fun decorate(view: DayViewFacade) {
                view.addSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            context,
                            R.color.calender_color_red
                        )
                    )
                )
            }
        }
    }

    fun saturdayDecorator(context: Context): DayViewDecorator {
        return object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay): Boolean {
                val calendar = Calendar.getInstance()
                calendar.set(day.year, day.month - 1, day.day)
                return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
            }

            override fun decorate(view: DayViewFacade) {
                view.addSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            context,
                            R.color.calender_color_blue
                        )
                    )
                )
            }
        }
    }

    fun eventDecorator(context: Context, colorRes: IntArray, date: CalendarDay): DayViewDecorator {
        return object : DayViewDecorator {
            private val colors: IntArray = IntArray(colorRes.size)

            init {
                for (i in colorRes.indices) {
                    colors[i] = ContextCompat.getColor(context, colorRes[i])
                }
            }

            override fun shouldDecorate(day: CalendarDay?): Boolean {
                return date == day
            }

            override fun decorate(view: DayViewFacade) {
                view.addSpan(CustomMultipleDotSpan(7f, colors))
            }
        }
    }
}

class CustomMultipleDotSpan(private val radius: Float, private val colors: IntArray) :
    LineBackgroundSpan {
    override fun drawBackground(
        canvas: Canvas,
        paint: Paint,
        left: Int,
        right: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        text: CharSequence,
        start: Int,
        end: Int,
        lineNumber: Int
    ) {
        val totalDots = minOf(colors.size, 5)
        val dotSpacing = 24
        val startX = (left + right) / 2 - (totalDots - 1) * dotSpacing / 2

        for (i in 0 until totalDots) {
            val oldColor = paint.color
            if (colors[i] != 0) {
                paint.color = colors[i]
            }
            canvas.drawCircle(startX + i * dotSpacing.toFloat(), bottom + radius, radius, paint)
            paint.color = oldColor
        }
    }
}
