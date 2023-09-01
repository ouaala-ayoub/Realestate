package com.example.realestate.utils

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.text.DecimalFormat
import java.text.ParseException


class NumberTextWatcher(
    private val editText: EditText,
    private val liveData: MutableLiveData<String?>
) : TextWatcher {
    companion object {
        private const val TAG = "NumberTextWatcher"
    }

    private val decimalFormat: DecimalFormat = DecimalFormat("#,###")

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        // Not needed for this implementation
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        // Not needed for this implementation
    }

    override fun afterTextChanged(s: Editable) {
        editText.removeTextChangedListener(this) // To prevent recursive calls
        val input = s.toString()
        if (input.isEmpty()) {
            editText.setText("")
            liveData.postValue(null)
        } else {
            try {

                //TODO fix that shit

                // reverse format the input to get the numeric value
                val numericValue = reverseFormatNumberWithCommas(input)

                Log.d(TAG, "numericValue: $numericValue")
                liveData.postValue(numericValue.toString())
                // Reformat the numeric value with commas
                val formattedValue = formatNumberWithCommas(numericValue)

                editText.setText(formattedValue)
                editText.setSelection(formattedValue.length)
            } catch (e: ParseException) {
                // Handle the exception as needed (e.g., invalid input)
                e.printStackTrace()
            }
        }
        editText.addTextChangedListener(this) // Reattach the TextWatcher
    }
}
