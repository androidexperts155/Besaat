import android.app.Activity
import com.deepak.besaat.BesaatApplication
import com.deepak.besaat.network.NetworkConnectionInterceptor
import com.deepak.besaat.network.Repository
import com.deepak.besaat.utils.*
import com.deepak.besaat.view.activities.customerSupport.viewModel.CustomerSupportViewModel
import com.deepak.besaat.view.activities.home.viewModel.HomeActivityViewModel
import com.deepak.besaat.viewModel.*
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module


object AppModule {
    fun appModule(app: BesaatApplication): Module = module {
        single { FrequentFunctions(app) }
        single { SharedPref(app) }
        single { CommonFunctions() }
        single { FragmentFucntions() }
        single { NetworkConnectionInterceptor(app) }
        single { Repository(get()) }
        single { Validations() }
        single { DateFunctions() }

        viewModel { SignUpViewModel(get()) }
        viewModel { PhoneSignUpViewModel(app, get(), get(), get(), get()) }
        viewModel { CreateProfileViewModel(get(), get(), get(), get()) }
        viewModel { DeliveryTypesViewModel() }
        viewModel { AddServiceDetailViewModel(get(), get()) }
        viewModel { AddCourierDetailViewModel(get(), get()) }
        viewModel { HomeFragmentViewModel(get(), get()) }
        viewModel { CodeVerificationViewModel(get(), get(), get(), get()) }
        viewModel { PaymentInformationViewModel(get(), get(), get()) }
        viewModel { NewRequestStoreViewModel(get()) }
        viewModel { StoreListingActivityViewModel(Activity(), get()) }
        viewModel { ServicesListingActivityViewModel(get(),get()) }
        viewModel { NewRequestServiceViewModel(get(),get(),get()) }
        viewModel { NewRequestCourierViewModel(get(),get(),get()) }
        viewModel { ChatViewModel(get(),get(),get()) }
        viewModel { MyOrdersViewModel(get(),get(),get()) }
        viewModel { CourierGuysListingActivityViewModel(get(),get()) }
        viewModel { CourierUserDetailsViewModel(get(),get(),get()) }
        viewModel { ServiceProviderProfileViewModel(get()) }
        viewModel { CustomerProfileViewModell(Activity(), get()) }
        viewModel { PastDetailsViewModel(get()) }
        viewModel { UpcomingDetailsViewModel(get()) }
        viewModel { HomeActivityViewModel(get(), get()) }
        viewModel { CustomerSupportViewModel(get(), get(), get(), get()) }
        viewModel { StoresNearByListingViewModel(get(),get()) }
        viewModel { MyOrdersOffersViewModel(get(),get(),get()) }
        viewModel { CancelOrderRequestViewModel(get(),get()) }
        viewModel { RequestStatusViewModel(get(),get()) }
        viewModel { JobDetailsViewModel(get(),get()) }

        // DB viewModel
//        viewModel { DBViewModel(get()) }

    }
}