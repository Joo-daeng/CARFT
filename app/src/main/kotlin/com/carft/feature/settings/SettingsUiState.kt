package com.carft.feature.settings

import com.carft.data.ConflictPolicy

data class PermissionUi(val name: String, val usage: String)

data class SettingsUiState(
    val watching: Boolean = true,
    val voiceWhileDriving: Boolean = true,
    val conflictPolicy: ConflictPolicy = ConflictPolicy.AskEveryTime,
    val appVersion: String = "버전 0.9 · Pleos Connect",
    val permissions: List<PermissionUi> = DEFAULT_PERMISSIONS
)

val DEFAULT_PERMISSIONS = listOf(
    PermissionUi("CAR_SEATS", "착좌 · 벨트 · 열선"),
    PermissionUi("CAR_CLIMATE", "공조 · 실내 온도"),
    PermissionUi("CAR_LIGHTS", "앰비언트 조명"),
    PermissionUi("CAR_WINDOWS", "와이퍼 상태"),
    PermissionUi("TTS_SERVICE", "음성 안내")
)
