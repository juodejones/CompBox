package com.rcappstudio.complaintbox.ui.staff

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.rcappstudio.complaintbox.R
import com.rcappstudio.complaintbox.databinding.ActivityStaffBinding
import com.rcappstudio.complaintbox.ui.FirebaseData
import com.rcappstudio.complaintbox.ui.staff.viewmodel.StaffViewModel
import com.rcappstudio.complaintbox.ui.staff.viewmodel.StaffViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StaffActivity : AppCompatActivity() {
    
    private lateinit var binding : ActivityStaffBinding
    @Inject
    lateinit var factory: StaffViewModelFactory
    private lateinit var viewModel: StaffViewModel
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    @Inject
    lateinit var firebaseData: FirebaseData

    private val department by lazy {
        sharedPreferences.getString("department","")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaffBinding.inflate(layoutInflater)
        supportActionBar!!.hide()
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, factory)[StaffViewModel::class.java]
        viewModel.setNavController(getNavController())
        initBottomNavigation()
        setNotificationToken()
    }

    private fun setNotificationToken(){
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            FirebaseDatabase.getInstance().getReference("Staff/$department/workers/${FirebaseAuth.getInstance().uid}/token")
                .setValue(it)
        }
    }

    private fun initBottomNavigation() {
        binding.staffBottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.staff_pending -> {
                    viewModel.switchToFragment(R.id.staffFragment1)
                }
                R.id.staff_solved -> {
                    viewModel.switchToFragment(R.id.staffFragment2)
                }
            }
            true
        }
        getNavController().addOnDestinationChangedListener{_, dest, _->
            if(dest.id == R.id.viewFragment || dest.id == R.id.mediaViewFragment){
                binding.staffBottomNavigationView.visibility = View.GONE
            } else{
                binding.staffBottomNavigationView.visibility = View.VISIBLE
            }
        }
    }

    private fun getNavController(): NavController {
        return (supportFragmentManager.findFragmentById(R.id.staffFragmentContainerView) as NavHostFragment).navController
    }


    override fun onBackPressed() {
        super.onBackPressed()
        binding.staffBottomNavigationView.selectedItemId = R.id.staff_pending
    }
}