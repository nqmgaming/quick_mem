package com.pwhs.quickmem.data.dto.flashcard

import com.google.gson.annotations.SerializedName
import com.pwhs.quickmem.core.data.FlipCardStatus
import com.pwhs.quickmem.core.data.Rating

data class FlashCardResponseDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("term")
    val term: String,
    @SerializedName("definition")
    val definition: String,
    @SerializedName("definitionImageURL")
    val definitionImageURL: String?,
    @SerializedName("hint")
    val hint: String?,
    @SerializedName("explanation")
    val explanation: String?,
    @SerializedName("studySetId")
    val studySetId: String,
    @SerializedName("rating")
    val rating: String,
    @SerializedName("flipStatus")
    val flipStatus: String,
    @SerializedName("isStarred")
    val isStarred: Boolean,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String
)