package com.example.snacknstretch

import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.snacknstretch.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: TimerViewModel by viewModels()
    private lateinit var batteryReceiver: BatteryReceiver

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(this, "Notification permission is required to show reminders", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        batteryReceiver = BatteryReceiver(viewModel)
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
            addAction(Intent.ACTION_BATTERY_LOW)
            addAction(Intent.ACTION_BATTERY_OKAY)
            addAction(Intent.ACTION_BATTERY_CHANGED)
        }
        ContextCompat.registerReceiver(
            this,
            batteryReceiver,
            filter,
            ContextCompat.RECEIVER_EXPORTED
        )

        viewModel.isCharging.observe(this) { isCharging ->
            binding.tbOnlyCharging.isChecked = isCharging
        }

        viewModel.isBatteryLow.observe(this) { isLow ->
            binding.tbBatteryLow.isChecked = !isLow
        }

        binding.tbBatteryLow.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setRequiresBatteryNotLow(isChecked)
        }

        binding.tbOnlyCharging.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setRequiresCharging(isChecked)
        }

        binding.btnSchedule.setOnClickListener {
            scheduleWork()
        }

        binding.btnCancel.setOnClickListener {
            cancelWork()
        }

        init()
    }

    private fun scheduleWork() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(viewModel.requiresBatteryNotLow.value ?: false)
            .setRequiresCharging(viewModel.requiresCharging.value ?: false)
            .build()

        val workInterval = (viewModel.workTime.value?.times(60000L)) ?: 15
        val restInterval = (viewModel.restTime.value?.times(60000L)) ?: 15

        val workRequest = PeriodicWorkRequestBuilder<WorkTimerWorker>(workInterval.toLong(), TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        val restRequest = PeriodicWorkRequestBuilder<RestTimerWorker>(restInterval.toLong(), TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "work_timer_task",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "rest_timer_task",
            ExistingPeriodicWorkPolicy.UPDATE,
            restRequest
        )

        Toast.makeText(this, "Reminders Scheduled Successfully!", Toast.LENGTH_SHORT).show()
    }

    private fun cancelWork() {
        WorkManager.getInstance(this).cancelUniqueWork("work_timer_task")
        WorkManager.getInstance(this).cancelUniqueWork("rest_timer_task")
        Toast.makeText(this, "Reminders Cancelled", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(batteryReceiver)
    }

    fun init(){
        binding.title.text = getString(R.string.app_name)
        binding.caption.text = getString(R.string.caption)
        binding.tvWork.text = getString(R.string.work_title)
        binding.tvBreak.text = getString(R.string.break_title)
        binding.tvBattery.text = getString(R.string.battery_txt)
        binding.tvCharge.text = getString(R.string.charging_txt)
        binding.tvConstraints.text = getString(R.string.constraints)
        binding.btnSchedule.text = getString(R.string.schedule_session)
        binding.btnCancel.text = getString(R.string.cancel_session)

        WorkTimerAdapter(binding.workTimerItem, viewModel).bind()
        RestTimerAdapter(binding.breakTimerItem, viewModel).bind()
    }
}
