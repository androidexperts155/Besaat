package com.deepak.besaat.network

import com.deepak.besaat.model.providerSignUpDetailToServer.ProviderSignUpDetailToServer
import com.deepak.besaat.model.userDetailModel.UserDetail

interface AllLocalHandler {
    companion object {
        var userDetail = UserDetail()
        var providerSignUpDetailToServer = ProviderSignUpDetailToServer()
    }
}