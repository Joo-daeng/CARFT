package com.carft.data

import android.content.Context
import android.util.Log
import com.carft.domain.model.ActionBlock
import com.carft.domain.model.Block
import com.carft.domain.model.CombinationLogic
import com.carft.domain.model.ConditionBlock
import com.carft.domain.model.Option
import com.carft.domain.model.Rule
import com.carft.domain.model.TriggerBlock
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class PersistentStore(context: Context) {
    private val optionsFile = File(context.filesDir, "options.json")
    private val settingsFile = File(context.filesDir, "settings.json")

    fun loadOptions(): List<Option>? = read(optionsFile) { json ->
        val array = JSONArray(json)
        (0 until array.length()).map { option(array.getJSONObject(it)) }
    }

    fun saveOptions(options: List<Option>) = write(optionsFile, JSONArray(options.map { option(it) }).toString())

    fun loadSettings(): AppSettings? = read(settingsFile) { json ->
        val o = JSONObject(json)
        AppSettings(
            watching = o.optBoolean("watching", true),
            voiceWhileDriving = o.optBoolean("voiceWhileDriving", true),
            conflictPolicy = runCatching { ConflictPolicy.valueOf(o.getString("conflictPolicy")) }.getOrDefault(ConflictPolicy.AskEveryTime),
            onboardingDone = o.optBoolean("onboardingDone", false)
        )
    }

    fun saveSettings(settings: AppSettings) = write(
        settingsFile,
        JSONObject()
            .put("watching", settings.watching)
            .put("voiceWhileDriving", settings.voiceWhileDriving)
            .put("conflictPolicy", settings.conflictPolicy.name)
            .put("onboardingDone", settings.onboardingDone)
            .toString()
    )

    private fun <T> read(file: File, parse: (String) -> T): T? {
        if (!file.exists()) return null
        return runCatching { parse(file.readText()) }
            .onFailure { Log.w(TAG, "${file.name} 읽기 실패", it) }
            .getOrNull()
    }

    private fun write(file: File, text: String) {
        runCatching {
            val temp = File(file.parentFile, "${file.name}.tmp")
            temp.writeText(text)
            temp.renameTo(file)
        }.onFailure { Log.w(TAG, "${file.name} 저장 실패", it) }
    }

    private fun option(o: Option): JSONObject = JSONObject()
        .put("id", o.id)
        .put("name", o.name)
        .put("enabled", o.isEnabled)
        .put("createdAt", o.createdAt)
        .put("ruleId", o.rule.id)
        .put("logic", o.rule.combinationLogic.operator)
        .put("trigger", block(o.rule.trigger))
        .put("conditions", JSONArray(o.rule.conditions.map { block(it) }))
        .put("actions", JSONArray(o.rule.actions.map { block(it) }))

    private fun option(j: JSONObject): Option = Option(
        id = j.getString("id"),
        name = j.getString("name"),
        isEnabled = j.optBoolean("enabled", true),
        createdAt = j.optLong("createdAt", System.currentTimeMillis()),
        rule = Rule(
            id = j.getString("ruleId"),
            trigger = block(j.getJSONObject("trigger")) as TriggerBlock,
            conditions = j.getJSONArray("conditions").let { a -> (0 until a.length()).map { block(a.getJSONObject(it)) as ConditionBlock } },
            combinationLogic = CombinationLogic(j.optString("logic", "AND")),
            actions = j.getJSONArray("actions").let { a -> (0 until a.length()).map { block(a.getJSONObject(it)) as ActionBlock } }
        )
    )

    private fun block(b: Block): JSONObject {
        val j = JSONObject().put("id", b.id)
        return when (b) {
            is TriggerBlock.Manual -> j.put("type", "trigger.manual")
            is TriggerBlock.SeatOccupied -> j.put("type", "trigger.seatOccupied").put("seat", b.seat)
            is TriggerBlock.SeatBeltBuckled -> j.put("type", "trigger.seatBelt").put("seat", b.seat)
            is TriggerBlock.TemperatureThreshold -> j.put("type", "trigger.temperature").put("threshold", b.threshold.toDouble())
            is TriggerBlock.WiperOn -> j.put("type", "trigger.wiper")
            is ConditionBlock.IsSeated -> j.put("type", "condition.seated").put("seat", b.seat)
            is ConditionBlock.TemperatureRange -> j.put("type", "condition.temperature").put("min", b.minTemp.toDouble()).put("max", b.maxTemp.toDouble())
            is ConditionBlock.IsDay -> j.put("type", "condition.hour").put("hour", b.hour)
            is ActionBlock.SetHvacTemperature -> j.put("type", "action.hvac").put("temp", b.targetTemp.toDouble()).put("stopOnly", b.requiresStationary)
            is ActionBlock.SetSeatHeat -> j.put("type", "action.seatHeat").put("seat", b.seat).put("level", b.level).put("stopOnly", b.requiresStationary)
            is ActionBlock.SetAmbientLight -> j.put("type", "action.ambient").put("color", b.color).put("brightness", b.brightness).put("stopOnly", b.requiresStationary)
            is ActionBlock.PlayTextToSpeech -> j.put("type", "action.tts").put("text", b.text).put("stopOnly", b.requiresStationary)
            else -> error("저장할 수 없는 블록: ${b::class.simpleName}")
        }
    }

    private fun block(j: JSONObject): Block {
        val id = j.getString("id")
        val stopOnly = j.optBoolean("stopOnly", false)
        return when (val type = j.getString("type")) {
            "trigger.manual" -> TriggerBlock.Manual(id)
            "trigger.seatOccupied" -> TriggerBlock.SeatOccupied(id, j.getString("seat"))
            "trigger.seatBelt" -> TriggerBlock.SeatBeltBuckled(id, j.getString("seat"))
            "trigger.temperature" -> TriggerBlock.TemperatureThreshold(id, j.getDouble("threshold").toFloat())
            "trigger.wiper" -> TriggerBlock.WiperOn(id)
            "condition.seated" -> ConditionBlock.IsSeated(id, j.getString("seat"))
            "condition.temperature" -> ConditionBlock.TemperatureRange(id, j.getDouble("min").toFloat(), j.getDouble("max").toFloat())
            "condition.hour" -> ConditionBlock.IsDay(id, j.getInt("hour"))
            "action.hvac" -> ActionBlock.SetHvacTemperature(id, j.getDouble("temp").toFloat(), stopOnly)
            "action.seatHeat" -> ActionBlock.SetSeatHeat(id, j.getString("seat"), j.getInt("level"), stopOnly)
            "action.ambient" -> ActionBlock.SetAmbientLight(id, j.getString("color"), j.optInt("brightness", 50), stopOnly)
            "action.tts" -> ActionBlock.PlayTextToSpeech(id, j.getString("text"), stopOnly)
            else -> error("알 수 없는 블록 종류: $type")
        }
    }

    private companion object {
        const val TAG = "Carft.Store"
    }
}
