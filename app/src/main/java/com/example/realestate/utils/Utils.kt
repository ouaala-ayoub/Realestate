package com.example.realestate.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.realestate.R
import com.example.realestate.data.models.Error
import com.example.realestate.data.models.ErrorResponse
import com.example.realestate.data.models.Post
import com.example.realestate.data.models.SearchParams
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


const val TAG = "Utils"

interface HandleSubmitInterface {
    fun onNextClicked(viewPager: ViewPager2, post: Post)
    fun onBackClicked(viewPager: ViewPager2)
}

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

fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    val tmp = this[index1]
    this[index1] = this[index2]
    this[index2] = tmp
}

fun Intent.getContentAsList(): List<Uri> {
    val data = this
    var imagesList = mutableListOf<Uri>()

    if (data.data != null) {
        imagesList = mutableListOf(data.data!!)
    } else if (data.clipData?.itemCount != null) {
        val itemCount = data.clipData?.itemCount
        for (i in 0 until itemCount!!) {
            imagesList.add(this.clipData?.getItemAt(i)!!.uri)
        }
    }

    return imagesList
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

fun AlertDialog.separateButtonsBy(margin: Int) {
    val negButton = this.getButton(AlertDialog.BUTTON_POSITIVE)
    val params = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )
    params.setMargins(margin, 0, 0, 0)
    negButton.layoutParams = params
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
    val dialog = makeDialog(
        context,
        onDialogClicked,
        context.getString(R.string.permission_required),
        context.getString(R.string.you_cant),
        negativeText = context.getString(R.string.no_thanks),
        positiveText = context.getString(R.string.authorise)

    )
    dialog.apply {
        show()
        separateButtonsBy(10)
    }
}

fun ComponentActivity.requestPermissionLauncher(permissionResult: PermissionResult): ActivityResultLauncher<String> {
    return this.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        Log.d(TAG, "requestPermissionLauncher isGranted = $isGranted")
        if (isGranted) {
            permissionResult.onGranted()
        } else {
            permissionResult.onNonGranted()
        }
    }
}

fun Fragment.requestPermissionLauncher(permissionResult: PermissionResult): ActivityResultLauncher<String> {
    return this.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        Log.d(TAG, "requestPermissionLauncher isGranted = $isGranted")
        if (isGranted) {
            permissionResult.onGranted()
        } else {
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

fun Fragment.startActivityResult(selectionResult: SelectionResult): ActivityResultLauncher<Intent> {
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
            onPermissionChecked.onGranted()
        }
        shouldShowRequestPermissionRationale(
            this,
            permission
        ) -> {
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
            onPermissionChecked.onNonGranted()
        }
    }
}

fun ActivityResultLauncher<Intent>.openGallery() {
    val intent = Intent(Intent.ACTION_GET_CONTENT)
    intent.apply {
        type = "image/*, video/*"
        putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
    }

    this.launch(Intent.createChooser(intent, "Select Picture"))
}

fun makeSnackBar(
    view: View,
    message: String,
    duration: Int
): Snackbar {
    return Snackbar.make(view, message, duration)
}

fun MaterialAutoCompleteTextView.setWithList(
    list: List<String>,
    context: Context
): ArrayAdapter<String> {
    val adapter = ArrayAdapter(context, R.layout.list_item, list)
    this.setAdapter(adapter)
    return adapter
}

fun MaterialAutoCompleteTextView.setUpAndHandleSearch(list: List<String>, context: Context): ArrayAdapter<String> {
    val adapter = setWithList(list, context)
    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(
            s: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            s: CharSequence?,
            start: Int,
            before: Int,
            count: Int
        ) {
        }

        override fun afterTextChanged(s: Editable?) {
            adapter.filter.filter(s)
        }
    })
    return adapter
}


fun EditText.updateLiveData(liveData: MutableLiveData<String>) {
    this.doOnTextChanged { text, _, _, _ ->
        liveData.value = text.toString()
    }
}

fun showLeaveDialog(activity: Activity) {
    val dialog = makeDialog(
        activity,
        object : OnDialogClicked {
            override fun onPositiveButtonClicked() {
                activity.finish()
            }

            override fun onNegativeButtonClicked() {}
        },
        activity.getString(R.string.quit_post_title),
        activity.getString(R.string.quit_post_message)
    )
    dialog.apply {
        show()
        separateButtonsBy(10)
    }
}
