package com.pwhs.quickmem.domain.repository

import com.pwhs.quickmem.core.utils.Resources
import com.pwhs.quickmem.domain.model.study_set.CreateStudySetRequestModel
import com.pwhs.quickmem.domain.model.study_set.CreateStudySetResponseModel
import com.pwhs.quickmem.domain.model.study_set.GetStudySetResponseModel
import com.pwhs.quickmem.domain.model.study_set.UpdateStudySetRequestModel
import com.pwhs.quickmem.domain.model.study_set.UpdateStudySetResponseModel
import kotlinx.coroutines.flow.Flow

interface StudySetRepository {
    suspend fun createStudySet(
        token: String,
        createStudySetRequestModel: CreateStudySetRequestModel
    ): Flow<Resources<CreateStudySetResponseModel>>

    suspend fun getStudySetById(
        token: String,
        studySetId: String
    ): Flow<Resources<GetStudySetResponseModel>>

    suspend fun getStudySetsByOwnerId(
        token: String,
        ownerId: String
    ): Flow<Resources<List<GetStudySetResponseModel>>>

    suspend fun updateStudySet(
        token: String,
        studySetId: String,
        updateStudySetRequestModel: UpdateStudySetRequestModel
    ): Flow<Resources<UpdateStudySetResponseModel>>

    suspend fun deleteStudySet(
        token: String,
        studySetId: String
    ): Flow<Resources<Unit>>

    suspend fun resetProgress(
        token: String,
        studySetId: String,
        resetType: String
    ): Flow<Resources<Unit>>
}