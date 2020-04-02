package com.deepak.besaat.view.activities.termsAndConditions.model

import com.deepak.besaat.network.Repository
import com.deepak.besaat.utils.Validations
import com.deepak.besaat.viewModel.BaseViewModel

class TermsAndPrivacyModel(validations: Validations, repository: Repository) :
    BaseViewModel() {
    val validations = validations
    val repository = repository
    }
