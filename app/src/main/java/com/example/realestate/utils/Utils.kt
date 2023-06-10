package com.example.realestate.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.example.realestate.data.models.Error
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.realestate.R
import com.example.realestate.data.models.ErrorResponse
import com.example.realestate.data.models.SearchParams
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val TAG = "Utils"

interface OnDialogClicked {
    fun onPositiveButtonClicked()
    fun onNegativeButtonClicked()
}

interface PermissionResult {
    fun onGranted()
    fun onNonGranted()
}

interface SelectionResult {
    fun onResultOk(data: Intent)
    fun onResultFailed()
}

interface ActivityResultListener {
    fun onResultOk(searchParams: SearchParams)
    fun onResultCancelled()
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

private fun showInContextUI(context: Context, onDialogClicked: OnDialogClicked) {
    makeDialog(
        context,
        onDialogClicked,
        context.getString(R.string.permission_required),
        context.getString(R.string.you_cant),
        negativeText = context.getString(R.string.no_thanks),
        positiveText = context.getString(R.string.authorise)

    ).show()
}

fun ComponentActivity.requestPermissionLauncher(permissionResult: PermissionResult): ActivityResultLauncher<String> {
    return this.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        Log.d(TAG, "requestPermissionLauncher isGranted = $isGranted")
        if (isGranted) {

            permissionResult.onGranted()
        } else {
            // Explain to the user that the feature is unavailable because the
            // features requires a permission that the user has denied. At the
            // same time, respect the user's decision. Don't link to system
            // settings in an effort to convince the user to change their
            // decision.
            permissionResult.onNonGranted()
        }
    }
}

fun ComponentActivity.startActivityResult(selectionResult: SelectionResult): ActivityResultLauncher<Intent> {
    return this.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data

            if (data != null) {
                selectionResult.onResultOk(data)
            } else {
                selectionResult.onResultFailed()
            }
        } else {
            selectionResult.onResultFailed()
        }
    }
}

fun ActivityResultLauncher<String>.requestStoragePermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this.launch(
            Manifest.permission.READ_MEDIA_IMAGES,
        )
//        this.launch(
//            Manifest.permission.READ_MEDIA_VIDEO,
//        )
    } else {
        this.launch(
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )
    }
}

fun Activity.handlePermission(onPermissionChecked: PermissionResult) {
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    when {
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED -> {
            // You can use the API that requires the permission.
            Log.d(TAG, "handlePermission: ${PackageManager.PERMISSION_GRANTED}")
            onPermissionChecked.onGranted()
        }
        shouldShowRequestPermissionRationale(
            this,
            permission
        ) -> {
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected. In this UI,
            // include a "cancel" or "no thanks" button that allows the user to
            // continue using your app without granting the permission.
            showInContextUI(
                this,
                object : OnDialogClicked {
                    override fun onPositiveButtonClicked() {
                        onPermissionChecked.onNonGranted()
                    }

                    override fun onNegativeButtonClicked() {
                        //cancel the dialog without doing nothing
                    }
                }
            )
        }
        else -> {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            onPermissionChecked.onNonGranted()
        }
    }
}

fun ActivityResultLauncher<Intent>.openGallery() {
    val intent = Intent(Intent.ACTION_GET_CONTENT)
    intent.apply {
        type = "video/*, image/*"
        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
    }
    this.launch(intent)
}

fun makeSnackBar(
    view: View,
    message: String,
    duration: Int
): Snackbar {
    return Snackbar.make(view, message, duration)
}
