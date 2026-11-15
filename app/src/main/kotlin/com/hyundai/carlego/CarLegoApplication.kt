package com.hyundai.carlego

import android.app.Application
import android.car.Car
import android.car.drivingstate.CarUxRestrictionsManager
import android.util.Log
import com.hyundai.carlego.domain.rule.ConditionEvaluator
import com.hyundai.carlego.domain.rule.RuleEngine
import com.hyundai.carlego.infrastructure.pleos.PleosSpeaker
import com.hyundai.carlego.infrastructure.pleos.PleosVehicle
import com.hyundai.carlego.infrastructure.pleos.PleosVehicleAdapter
import com.hyundai.carlego.runtime.OptionRunner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class CarLegoApplication : Application() {
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
        }.onFailure { Log.w("CarLego", "ux restrictions unavailable", it) }
    }
}
