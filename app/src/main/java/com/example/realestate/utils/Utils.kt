package com.example.realestate.utils

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.example.realestate.data.models.Error
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.realestate.R
import com.example.realestate.data.models.ErrorResponse
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.FieldPosition

interface OnDialogClicked {
    fun onPositiveButtonClicked()
    fun onNegativeButtonClicked()
}

interface OnPostClickListener {
    fun onClick(postId: String)
}

interface AdditionalCode {
    fun <T> onResponse(responseBody: Response<T>)
    fun onFailure()
}

fun <T> handleApiRequest(
    apiCall: Call<T>,
    loadingLiveData: MutableLiveData<Boolean>,
    dataLiveData: MutableLiveData<T?>,
    TAG: String,
    additionalCode: AdditionalCode? = null
) {
    loadingLiveData.postValue(true)

    apiCall.enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {

            if (response.isSuccessful) {
                dataLiveData.postValue(response.body())
            } else {
                getError(TAG, response.errorBody(), response.code())
                dataLiveData.postValue(null)
            }
            additionalCode?.onResponse(response)
            loadingLiveData.postValue(false)
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            loadingLiveData.postValue(false)
            dataLiveData.postValue(null)
            Log.e(TAG, "onFailure: ${t.message}")
            additionalCode?.onFailure()
        }
    })
}

private fun getError(
    TAG: String,
    responseBody: ResponseBody?,
    code: Int
): Error? {
    return try {

        val test = responseBody?.charStream()?.readText()
        Log.e(TAG, "JSONObject or msg : $test ")
        val error = Gson().fromJson(test, ErrorResponse::class.java)
        Log.e(TAG, "error: $error")

        Error(error.message, code)
    } catch (e: Exception) {
        Log.e(TAG, "getError: $e.stackTrace")
        val error = e.message?.let { Error(it, code) }
        Log.e(TAG, "error parsing JSON error message: $error")
        return error
    }
}

fun circularProgressBar(context: Context): CircularProgressDrawable {
    val circularProgressDrawable = CircularProgressDrawable(context)
    circularProgressDrawable.apply {
        strokeWidth = 5f
        centerRadius = 30f
        start()
    }
    return circularProgressDrawable
}

fun Context.toast(message: String, length: Int) =
    Toast.makeText(this, message, length).show()

fun makeDialog(
    context: Context,
    onDialogClicked: OnDialogClicked,
    title: String?,
    message: String?,
    view: View? = null,
    negativeText: String = context.resources.getString(R.string.Cancel),
    positiveText: String = context.resources.getString(R.string.Yes)

): AlertDialog {
    val myDialog = AlertDialog
        .Builder(context)
        .setTitle(title)
        .setMessage(message)
        .setView(view)
        .setCancelable(false)
        .setPositiveButton(positiveText) { _, _ ->
            onDialogClicked.onPositiveButtonClicked()
        }

        .setNegativeButton(negativeText) { _, _ ->
            onDialogClicked.onNegativeButtonClicked()
        }
        .create()

    myDialog.setOnCancelListener {
        it.dismiss()
    }

    return myDialog
}

fun RecyclerView.handleRefreshWithScrolling(swipeRefresh: SwipeRefreshLayout) {
    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            val isRvDragging = newState == RecyclerView.SCROLL_STATE_DRAGGING
            swipeRefresh.isEnabled = !isRvDragging
        }
    })
}

//fun LinearLayout.uncheckAllBut(position: Int){
//    for ()
//}
