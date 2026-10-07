package com.example.snacknstretch

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager

class BatteryReceiver(private val viewModel: TimerViewModel) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_POWER_CONNECTED -> {
                viewModel.setCharging(true)
            }
            Intent.ACTION_POWER_DISCONNECTED -> {
                viewModel.setCharging(false)
            }
            Intent.ACTION_BATTERY_LOW -> {
                viewModel.setBatteryLow(true)
            }
            Intent.ACTION_BATTERY_OKAY -> {
                viewModel.setBatteryLow(false)
            }
            Intent.ACTION_BATTERY_CHANGED -> {
                val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL
                viewModel.setCharging(isCharging)

                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                if (level != -1 && scale != -1) {
                    val batteryPct = level * 100 / scale.toFloat()
                    viewModel.setBatteryLow(batteryPct <= 15.0f)
                }
            }
        }
    }
}
