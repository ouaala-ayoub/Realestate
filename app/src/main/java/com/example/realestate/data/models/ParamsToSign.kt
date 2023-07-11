package com.example.realestate.data.models

import com.google.gson.annotations.SerializedName

data class ParamsToSign(
    @SerializedName("paramsToSign")
    val paramsToSign: MutableMap<Any?, Any?>
)
