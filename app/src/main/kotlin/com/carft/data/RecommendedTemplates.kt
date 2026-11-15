package com.carft.data

import com.carft.domain.model.ActionBlock
import com.carft.domain.model.ConditionBlock
import com.carft.domain.model.Option
import com.carft.domain.model.Rule
import com.carft.domain.model.TriggerBlock

data class RecommendedTemplate(
    val id: String,
    val tag: String,
    val popular: Boolean,
    val description: String,
    val option: Option
)

object RecommendedTemplates {
    fun all(): List<RecommendedTemplate> = listOf(
        RecommendedTemplate(
            id = "cold-morning",
            tag = "아침 루틴",
            popular = true,
            description = "출발 전에 차 안을 미리 데워요",
            option = Option(
                name = "추운 아침 예열",
                rule = Rule(
                    trigger = TriggerBlock.SeatOccupied(seat = "driver"),
                    conditions = listOf(ConditionBlock.TemperatureRange(minTemp = -40f, maxTemp = 10f)),
                    actions = listOf(ActionBlock.SetHvacTemperature(targetTemp = 25f), ActionBlock.SetSeatHeat(level = 2))
                )
            )
        ),
        RecommendedTemplate(
            id = "rainy-day",
            tag = "날씨",
            popular = false,
            description = "와이퍼가 켜지면 조명과 음성으로 알려요",
            option = Option(
                name = "비 오는 날 안내",
                rule = Rule(
                    trigger = TriggerBlock.WiperOn(),
                    actions = listOf(ActionBlock.SetAmbientLight(color = "blue"), ActionBlock.PlayTextToSpeech(text = "비가 와요. 천천히 운전하세요."))
                )
            )
        ),
        RecommendedTemplate(
            id = "school-run",
            tag = "가족",
            popular = false,
            description = "아침 정한 시각에 조명과 음성으로 출발을 알려요",
            option = Option(
                name = "등원 준비",
                rule = Rule(
                    trigger = TriggerBlock.TimeOfDay(hour = 7, minute = 30),
                    actions = listOf(
                        ActionBlock.SetAmbientLight(color = "orange"),
                        ActionBlock.PlayTextToSpeech(text = "등원 준비 완료. 출발할게요.", requiresStationary = true)
                    )
                )
            )
        )
    )

    fun find(id: String): RecommendedTemplate? = all().firstOrNull { it.id == id }
}
