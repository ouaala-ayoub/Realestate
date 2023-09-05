package com.example.realestate.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.realestate.R
import com.example.realestate.data.models.Error
import com.example.realestate.data.models.ErrorResponse
import com.example.realestate.data.models.MediaType
import com.example.realestate.data.models.SearchParams
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.*


const val TAG = "Utils"

interface OnLikedClickListener {
    fun onClicked(postId: String)
    fun onDeleteClickedListener(postId: String)
}

interface Task {
    fun onSuccess(user: FirebaseUser?)
    fun onFail(e: Exception?)
}

interface HandleSubmitInterface {
    fun onNextClicked(viewPager: ViewPager2)
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

interface OnAddToFavClicked {
    fun onChecked(postId: String)
    fun onUnChecked(postId: String)
}

interface OnVerificationCompleted {
    fun onCodeSent(verificationId: String)

    fun onCompleted(credential: PhoneAuthCredential) = null

    fun onFail(e: FirebaseException) = null
}

interface AdditionalCode<T> {
    fun onResponse(responseBody: Response<T>)
    fun onFailure()
}

fun AutoCompleteTextView.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    val tmp = this[index1]
    this[index1] = this[index2]
    this[index2] = tmp
}

fun List<String>.capitalizeFirstLetter(): List<String> {
    return map { str ->
        str.upperFirstLetter()
    }
}

fun List<String>.lowerFirstLetter(): List<String> {
    return map { str ->
        str.lowerFirstLetter()
    }
}

fun String.lowerFirstLetter(): String {
    return if (isNotEmpty()) {
        substring(0, 1).lowercase(Locale.US) + substring(1)
    } else {
        this// If the string is empty, return it as is
    }
}

fun String.upperFirstLetter(): String {
    return if (isNotEmpty()) {
        substring(0, 1).uppercase(Locale.US) + substring(1)
    } else {
        this// If the string is empty, return it as is
    }
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

fun FragmentActivity.disableBackButton(viewLifecycleOwner: LifecycleOwner) {
    this.onBackPressedDispatcher.addCallback(
        viewLifecycleOwner,
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }

        })
}

fun formatNumberWithCommas(number: Number): String {
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)
    return numberFormat.format(number)
}

fun formatNumberWithSpaces(number: Number): String {
    val numberFormat = NumberFormat.getInstance(Locale.getDefault())
    return numberFormat.format(number)
}

fun reverseFormatNumberWithCommas(formattedNumber: String): Double {
    val numberFormat = NumberFormat.getInstance(Locale.US)
    return try {
        val number = numberFormat.parse(formattedNumber)?.toDouble()
        number ?: 0.0 // Return 0.0 if parsing fails
    } catch (e: Exception) {
        0.0 // Handle parsing errors as needed
    }
}

fun <T> handleApiRequest(
    apiCall: Call<T>,
    loadingLiveData: MutableLiveData<Boolean>?,
    dataLiveData: MutableLiveData<T?>? = null,
    TAG: String,
    additionalCode: AdditionalCode<T>? = null
) {
    loadingLiveData?.postValue(true)

    apiCall.enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {

            if (response.isSuccessful) {
                dataLiveData?.postValue(response.body())
                Log.d(TAG, "onResponse: ${response.body()}")
            } else {
                getError(TAG, response.errorBody(), response.code())
                dataLiveData?.postValue(null)
            }
            additionalCode?.onResponse(response)
            loadingLiveData?.postValue(false)
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            loadingLiveData?.postValue(false)
            dataLiveData?.postValue(null)
            Log.e(TAG, "onFailure: ${t.message}")
            additionalCode?.onFailure()
        }
    })
}

fun getError(
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

fun ImageView.loadImage(imageName: String?, errorImage: Int = R.drawable.baseline_broken_image_24) {
    val imageUrl = context.getString(R.string.image_url, imageName)
    Glide.with(this)
//        .load(imageUrl)
        .load(imageName)
        .centerCrop()
        .placeholder(circularProgressBar(context))
        .error(errorImage)
        .into(this)
}

fun ImageView.loadSvg(imageUrl: String, errorImage: Int = R.drawable.baseline_broken_image_24) {
    val requestBuilder = GlideToVectorYou
        .init()
        .with(context)
        .requestBuilder

    requestBuilder
        .load(imageUrl)
        .transition(DrawableTransitionOptions.withCrossFade())
        .apply(
            RequestOptions()
                .centerCrop()
                .placeholder(circularProgressBar(context))
                .error(errorImage)
        )
        .into(this)
}

fun StyledPlayerView.loadVideo(videoName: String, myPlayer: ExoPlayer) {
    val videoUrl = this.context.getString(R.string.video_url, videoName)
    player = myPlayer
//    setVideoPath(videoUrl)
    val mediaItem =
        MediaItem.fromUri(videoName)
    // Set the media item to be played.
    myPlayer.setMediaItem(mediaItem)
    // Prepare the player.
    myPlayer.prepare()
    // Start the playback.
    myPlayer.play()
}

fun Fragment.doOnFail() {
    requireContext().toast(getString(R.string.error), Toast.LENGTH_SHORT)
    findNavController().popBackStack()
}

fun Activity.doOnFail() {
    toast(getString(R.string.error), Toast.LENGTH_SHORT)
    finish()
}

fun ImageView.loadImageUri(imageUri: Uri) {
    Glide.with(this)
        .load(imageUri)
        .centerCrop()
        .placeholder(circularProgressBar(context))
        .error(R.drawable.baseline_broken_image_24)
        .into(this)
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
    val posButton = getButton(AlertDialog.BUTTON_POSITIVE)
    val params = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )
    params.setMargins(margin, 0, 0, 0)
    posButton.layoutParams = params
}

//fun RecyclerView.handleRefreshWithScrolling(swipeRefresh: SwipeRefreshLayout) {
////    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
////        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
////            super.onScrollStateChanged(recyclerView, newState)
////            val isRvDragging = newState == RecyclerView.SCROLL_STATE_DRAGGING
////            swipeRefresh.isEnabled = !isRvDragging
////        }
////    })
//    addOnScrollListener(object : RecyclerView.OnScrollListener() {
//        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//            swipeRefresh.isEnabled = !recyclerView.canScrollVertically(-1)
//        }
//    })
//}

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

fun Fragment.requestMultiplePermissions(permissionResult: LocationPermission): ActivityResultLauncher<Array<String>> {
    return this.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->

        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
                permissionResult.onGrantedPrecise()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
                permissionResult.onGrantedApproximate()
            }
            else -> {
                // No location access granted.
                permissionResult.onNonGranted()
            }
        }
    }
}

interface LocationPermission {
    fun onGrantedPrecise()
    fun onGrantedApproximate()
    fun onNonGranted()
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

fun ActivityResultLauncher<String>.requestCallPermission() {
    this.launch(Manifest.permission.CALL_PHONE)
}

fun Activity.handlePermission(onPermissionChecked: PermissionResult, permissions: List<String>) {

    val granted = permissions.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }
    val showRationale = permissions.any {
        shouldShowRequestPermissionRationale(this, it)
    }
    when {
        granted -> {
            onPermissionChecked.onGranted()
        }
        showRationale -> {
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

fun ActivityResultLauncher<Array<String>>.requestLocationPermission() {
    launch(
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
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
    list: List<String?>,
): ArrayAdapter<String?> {
    val adapter = ArrayAdapter(context, R.layout.list_item, list)
    setAdapter(adapter)
    return adapter
}

fun MaterialAutoCompleteTextView.setUpAndHandleSearch(
    list: List<String?>,
    onSelected: OnSelected? = null,
): ArrayAdapter<String?> {
    val adapter = setWithList(list)
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
            onSelected?.onSelected(s)
            adapter.filter.filter(s)
        }
    })
    return adapter
}


fun EditText.updateLiveData(liveData: MutableLiveData<String>, lower: Boolean = false) {
    doOnTextChanged { text, _, _, _ ->
        val textRes = text.toString()
        liveData.value = if (lower)
            textRes.lowerFirstLetter()
        else
            textRes
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

inline fun <reified T> goToActivity(context: Context) {
    val intent = Intent(context, T::class.java)
    context.startActivity(intent)
}

//fun Spinner.setWithList(items: List<String>, onSelected: OnSelected) {
//    val spinnerAdapter =
//        ArrayAdapter(context, android.R.layout.simple_spinner_item, items)
//    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//
//    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//        override fun onItemSelected(
//            parent: AdapterView<*>?,
//            view: View?,
//            position: Int,
//            id: Long
//        ) {
//            val selectedItem = items[position]
//            // Do something with the selected item
//            onSelected.onSelected(selectedItem)
//        }
//
//        override fun onNothingSelected(parent: AdapterView<*>?) {
//            // Handle case when no item is selected
//        }
//    }
//    adapter = spinnerAdapter
//}

interface OnSelected {
    fun onSelected(selectedItem: Editable?)
}

fun Context.getType(uri: Uri): String? {
    val cr = contentResolver
    return cr.getType(uri)
}

//suspend fun isImage(mediaLink: String): Boolean = withContext(Dispatchers.IO) {
//    val url = URL(mediaLink)
//    val connection: URLConnection = url.openConnection()
//    val contentType: String? = connection.contentType
//
//    contentType?.startsWith("image/", ignoreCase = true) == true
//}

fun CollapsingToolbarLayout.enableScroll() {
    val params = layoutParams as AppBarLayout.LayoutParams
    params.scrollFlags = (
            AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                    or AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
            )
    layoutParams = params
}

fun CollapsingToolbarLayout.disableScroll() {
    val params = layoutParams as AppBarLayout.LayoutParams
    params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL
    layoutParams = params
}

fun progressDialog(
    context: Context,
    view: View,
): AlertDialog {

    val builder = AlertDialog.Builder(context)

    builder.apply {
        setCancelable(false)
        setView(view)
    }
    return builder.create()
}

fun getMediaType(url: String, TAG: String): MediaType {
    val extension = MimeTypeMap.getFileExtensionFromUrl(url)
    val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)

    Log.d(TAG, "mimeType: $mimeType")

    return when {
        mimeType?.startsWith("image/") == true -> MediaType.IMAGE
        mimeType?.startsWith("video/") == true -> MediaType.VIDEO
        else -> MediaType.UNKNOWN
    }
}

fun getFileExtensionFromUri(context: Context, uri: Uri): String? {
    val contentResolver = context.contentResolver
    val mimeType = contentResolver.getType(uri)
    return mimeType?.let {
        MimeTypeMap.getSingleton().getExtensionFromMimeType(it)?.lowercase()
    }
}

fun getMediaTypeFromUri(context: Context, uri: Uri): MediaType {
    val contentResolver = context.contentResolver
    val mimeType = contentResolver.getType(uri)
    return when {
        mimeType?.startsWith("image/") == true -> MediaType.IMAGE
        mimeType?.startsWith("video/") == true -> MediaType.VIDEO
        else -> MediaType.UNKNOWN
    }
}

fun TextView.defineField(value: String?) {
    text = getValueOrDefault(value)
}

fun getValueOrDefault(value: String?): String {
    return value.takeIf { it?.isNotEmpty() == true } ?: "-"
}

fun squareMeterToSquareFoot(meter: Double) = meter * 10.7633911105
fun squareFeetToSquareMeters(squareFeet: Double) = squareFeet * 0.092903
fun formatDecimal(value: Double): String {
    return String.format(Locale.US, "%.3f", value)
}

fun ViewGroup.initialiseCategoryButtons(
    category: String?,
    lastSelected: RadioButton?,
    TAG: String
): RadioButton? {
    val selected = children.find { view ->
        val selected = view as RadioButton
        selected.text.toString().lowerFirstLetter() == category
    } as RadioButton?

    if (lastSelected != selected)
        selected?.isChecked = true

    return selected
}

fun List<String>.sortToAdd(): MutableList<String> {
    val result = mutableListOf<String>()
    val mid = size / 2

    for (i in 0 until mid) {
        result.add(this[i])
        result.add(this[size - 1 - i])
    }

    if (size % 2 != 0) {
        result.add(this[mid])
    }

    return result
}

fun String.isNumeric(): Boolean {
    return all { char -> char.isDigit() } && !isNullOrEmpty()
}

fun Activity.openTheWebsite(websiteUrl: String) {
    val openURL = Intent(Intent.ACTION_VIEW)
    openURL.data = Uri.parse(websiteUrl)
    startActivity(openURL)
}

fun getDetailIcon(key: String, context: Context): Drawable? {
    val drawableRes = when (key) {
        "Property Condition" -> {
            R.drawable.baseline_content_paste_search_24
        }
        "Number Of rooms" -> {
            R.drawable.baseline_bed_24
        }
        "Number of bathrooms" -> {
            R.drawable.baseline_bathroom_24
        }
        "Floor Info" -> {
            R.drawable.skyscraper_svgrepo_com
        }
        "Space" -> {
            R.drawable.measure_area_svgrepo_com
        }
        "Furnished" -> {
            R.drawable.furniture_svgrepo_com
        }
        "Balcony" -> {
            R.drawable.antique_balcony_svgrepo_com
        }
        "New" -> {
            R.drawable.baseline_fiber_new_24
        }
        "Swimming Pool" -> {
            R.drawable.baseline_pool_24
        }
        "Gym" -> {
            R.drawable.gym_svgrepo_com
        }
        "Parking" -> {
            R.drawable.baseline_local_parking_24
        }
        "Elevator" -> {
            R.drawable.elevator_svgrepo_com
        }
        "Security" -> {
            R.drawable.secure_shield_password_protect_safe_svgrepo_com
        }
        "Downtown" -> {
            R.drawable.city_svgrepo_com
        }
        "Shopping Mall" -> {
            R.drawable.cart_svgrepo_com
        }
        "Transportation" -> {
            R.drawable.taxi_4_svgrepo_com
        }
        "Tram" -> {
            R.drawable.tram_svgrepo_com
        }
        else -> {
            R.drawable.baseline_broken_image_24
        }
    }
    return ContextCompat.getDrawable(context, drawableRes)
}

