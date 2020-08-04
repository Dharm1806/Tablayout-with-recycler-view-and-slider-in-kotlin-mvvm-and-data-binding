package com.tekmindz.covidhealthcare.repository.responseModel

import com.tekmindz.covidhealthcare.repository.requestModels.EditProfileRequest

data class EmergencyContact(val body: EditProfileRequest) : BaseResponse()