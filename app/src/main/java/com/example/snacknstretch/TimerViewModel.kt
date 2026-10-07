package com.example.snacknstretch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimerViewModel : ViewModel() {
    private val _workTime = MutableLiveData<Int?>(15)
    val workTime: LiveData<Int?> get() = _workTime

    private val _restTime = MutableLiveData<Int?>(15)
    val restTime: LiveData<Int?> get() = _restTime

    private val _isCharging = MutableLiveData<Boolean>(false)
    val isCharging: LiveData<Boolean> get() = _isCharging

    private val _isBatteryLow = MutableLiveData<Boolean>(false)
    val isBatteryLow: LiveData<Boolean> get() = _isBatteryLow

    private val _requiresBatteryNotLow = MutableLiveData<Boolean>(false)
    val requiresBatteryNotLow: LiveData<Boolean> get() = _requiresBatteryNotLow

    private val _requiresCharging = MutableLiveData<Boolean>(false)
    val requiresCharging: LiveData<Boolean> get() = _requiresCharging

    fun setWorkTime(time: Int?) {
        _workTime.value = time
    }

    fun setRestTime(time: Int?) {
        _restTime.value = time
    }

    fun setCharging(charging: Boolean) {
        _isCharging.value = charging
    }

    fun setBatteryLow(low: Boolean) {
        _isBatteryLow.value = low
    }

    fun setRequiresBatteryNotLow(required: Boolean) {
        _requiresBatteryNotLow.value = required
    }

    fun setRequiresCharging(required: Boolean) {
        _requiresCharging.value = required
    }
}
