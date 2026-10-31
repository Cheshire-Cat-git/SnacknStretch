package com.example.snacknstretch

import android.view.LayoutInflater
import android.view.View
import com.example.snacknstretch.databinding.ItemTimerBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup

class RestTimerAdapter(
    private val toggleGroup: MaterialButtonToggleGroup,
    private val viewModel: TimerViewModel
) {

    fun bind() {
        val context = toggleGroup.context
        val inflater = LayoutInflater.from(context)
        val binding = ItemTimerBinding.inflate(inflater, toggleGroup, false)

        toggleGroup.isSingleSelection = true
        toggleGroup.isSelectionRequired = false

        val sourceGroup = binding.toggleGroupTimer
        val buttons = mutableListOf<MaterialButton>()

        while (sourceGroup.childCount > 0) {
            val child = sourceGroup.getChildAt(0)
            sourceGroup.removeView(child)
            if (child is MaterialButton) {
                child.id = View.generateViewId()
                // Initially clear icon so only the chosen button shows it
                child.icon = null
                buttons.add(child)
            }
        }

        for (button in buttons) {
            toggleGroup.addView(button)
        }

        toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            val button = group.findViewById<MaterialButton>(checkedId)
            if (button != null) {
                if (isChecked) {
                    button.setIconResource(R.drawable.ic_check)
                    button.setIconTintResource(R.color.timer_text)
                    button.iconSize = (16 * context.resources.displayMetrics.density).toInt()
                    button.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START

                    val time = button.text.toString().toIntOrNull()
                    viewModel.setRestTime(time)
                } else {
                    button.icon = null
                    if (group.checkedButtonId == View.NO_ID) {
                        viewModel.setRestTime(null)
                    }
                }
            }
        }
    }
}
