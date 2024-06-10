package com.example.clockinapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.app.DatePickerDialog
import android.graphics.Color
import android.widget.Button
import android.widget.LinearLayout
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class Graph : AppCompatActivity() {

    private lateinit var pieChartContainer: LinearLayout
    private lateinit var selectDateButton: Button
    private val database = FirebaseDatabase.getInstance().reference
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    private var startDate: Date? = null
    private var endDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_graph)

        pieChartContainer = findViewById(R.id.pie_chart_container)
        selectDateButton = findViewById(R.id.select_date_button)

        selectDateButton.setOnClickListener {
            showDateRangePicker()
        }

        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/
    }

    private fun showDateRangePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(this, { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            startDate = calendar.time

            val endCalendar = Calendar.getInstance()
            val endDatePickerDialog = DatePickerDialog(this, { _, endYear, endMonth, endDayOfMonth ->
                endCalendar.set(endYear, endMonth, endDayOfMonth)
                endDate = endCalendar.time
                fetchDataFromDatabase()
            }, endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH))
            endDatePickerDialog.show()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.show()
    }

    private fun fetchDataFromDatabase() {
        if (startDate == null || endDate == null) return

        val startDateString = dateFormat.format(startDate!!)
        val endDateString = dateFormat.format(endDate!!)

        // Fetch goals
        database.child("goals")
            .orderByChild("date")
            .startAt(startDateString)
            .endAt(endDateString)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(goalSnapshot: DataSnapshot) {
                    pieChartContainer.removeAllViews()

                    // For each goal entry, fetch corresponding time entries
                    for (goal in goalSnapshot.children) {
                        val date = goal.child("date").getValue(String::class.java) ?: continue
                        val minGoal = goal.child("minDailyGoal").getValue(Float::class.java) ?: 0f
                        val maxGoal = goal.child("maxDailyGoal").getValue(Float::class.java) ?: 0f
                        val userId = goal.child("userId").getValue(String::class.java) ?: continue

                        fetchTimeEntries(date, userId, minGoal, maxGoal)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    databaseError.toException().printStackTrace()
                }
            })
    }

    private fun fetchTimeEntries(date: String, userId: String, minGoal: Float, maxGoal: Float) {
        val startTimestamp = dateToTimestamp(date).toDouble()
        val endTimestamp = (startTimestamp + 24 * 60 * 60 * 1000 - 1).toDouble()  // End of the day

        database.child("entries").child(userId)
            .orderByChild("timestamp")
            .startAt(startTimestamp)
            .endAt(endTimestamp)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(entrySnapshot: DataSnapshot) {
                    var totalHoursWorked = 0f

                    for (entry in entrySnapshot.children) {
                        // Assuming each entry has a duration in hours
                        val duration = entry.child("duration").getValue(Float::class.java) ?: 0f
                        totalHoursWorked += duration
                    }

                    addPieChart(date, totalHoursWorked, minGoal, maxGoal)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    databaseError.toException().printStackTrace()
                }
            })
    }


    private fun addPieChart(date: String, hoursWorked: Float, minGoal: Float, maxGoal: Float) {
        val pieChart = PieChart(this)
        pieChart.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(hoursWorked, "Hours Worked"))
        entries.add(PieEntry(minGoal, "Min Goal"))
        entries.add(PieEntry(maxGoal, "Max Goal"))

        val pieDataSet = PieDataSet(entries, date)
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS, 255)
        pieDataSet.valueTextColor = Color.BLACK
        pieDataSet.valueTextSize = 15f

        val pieData = PieData(pieDataSet)
        pieChart.data = pieData
        pieChart.description.text = date
        pieChart.animateY(2000)

        pieChartContainer.addView(pieChart)
    }

    private fun dateToTimestamp(date: String): Long {
        return dateFormat.parse(date)?.time ?: 0
    }
}