package com.carft

import android.app.Application
import android.car.Car
import android.car.drivingstate.CarUxRestrictionsManager
import android.util.Log
import com.carft.app.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class CarftApplication : Application() {
    lateinit var container: AppContainer
        private set

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this, appScope).also { it.start() }
        watchDrivingState()
    }

    private fun watchDrivingState() {
        runCatching {
            val car = Car.createCar(this)
            val manager = car.getCarManager(Car.CAR_UX_RESTRICTION_SERVICE) as CarUxRestrictionsManager
            container.vehicle.setDriving(manager.currentCarUxRestrictions.isRequiresDistractionOptimization)
            manager.registerListener { container.vehicle.setDriving(it.isRequiresDistractionOptimization) }
        }.onFailure { Log.w("Carft", "ux restrictions unavailable", it) }
    }
}

val android.content.Context.appContainer: AppContainer
    get() = (applicationContext as CarftApplication).container
