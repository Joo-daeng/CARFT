package com.carft

import android.app.Application
import android.car.Car
import android.car.drivingstate.CarUxRestrictionsManager
import android.util.Log
import com.carft.domain.rule.ConditionEvaluator
import com.carft.domain.rule.RuleEngine
import com.carft.infrastructure.pleos.PleosSpeaker
import com.carft.infrastructure.pleos.PleosVehicle
import com.carft.infrastructure.pleos.PleosVehicleAdapter
import com.carft.runtime.OptionRunner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class CarftApplication : Application() {
    lateinit var pleos: PleosVehicle
        private set
    lateinit var speaker: PleosSpeaker
        private set
    lateinit var runner: OptionRunner
        private set

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onCreate() {
        super.onCreate()
        pleos = PleosVehicle(this).also { it.start() }
        speaker = PleosSpeaker(this).also { it.start() }
        val adapter = PleosVehicleAdapter(pleos, speaker)
        runner = OptionRunner(appScope, pleos.state, RuleEngine(ConditionEvaluator(adapter), adapter)).also { it.start() }
        watchDrivingState()
    }

    private fun watchDrivingState() {
        runCatching {
            val car = Car.createCar(this)
            val manager = car.getCarManager(Car.CAR_UX_RESTRICTION_SERVICE) as CarUxRestrictionsManager
            pleos.setDriving(manager.currentCarUxRestrictions.isRequiresDistractionOptimization)
            manager.registerListener { pleos.setDriving(it.isRequiresDistractionOptimization) }
        }.onFailure { Log.w("Carft", "ux restrictions unavailable", it) }
    }
}
