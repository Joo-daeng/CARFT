package com.carft.domain.model

object SampleOptions {
    fun all(): List<Option> = listOf(
        Option(
            name = "하린이 아침",
            priority = 1,
            rule = Rule(
                trigger = TriggerBlock.SeatBeltBuckled(seat = "rearRight"),
                conditions = listOf(ConditionBlock.TemperatureRange(minTemp = -40f, maxTemp = 10f)),
                actions = listOf(
                    ActionBlock.SetHvacTemperature(targetTemp = 24f),
                    ActionBlock.SetSeatHeat(seat = "driver", level = 3),
                    ActionBlock.PlayTextToSpeech(text = "하린이가 탔어요. 히터와 열선을 켰어요.")
                )
            )
        ),
        Option(
            name = "출근 모드",
            priority = 2,
            rule = Rule(
                trigger = TriggerBlock.SeatOccupied(seat = "driver"),
                actions = listOf(
                    ActionBlock.SetHvacTemperature(targetTemp = 22f),
                    ActionBlock.SetAmbientLight(color = "white")
                )
            )
        ),
        Option(
            name = "비 오는 등원",
            priority = 3,
            rule = Rule(
                trigger = TriggerBlock.WiperOn(),
                actions = listOf(
                    ActionBlock.SetAmbientLight(color = "blue"),
                    ActionBlock.PlayTextToSpeech(text = "비가 와요. 천천히 운전하세요.")
                )
            )
        )
    )
}
