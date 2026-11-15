# CARFT

Pleos Connect 기반 노코드 차량 자동화 앱

## 빠른 시작

### 필요 사항
- Android Studio
- JDK 11+
- Gradle 8.1+

### 프로젝트 실행

1. **Android Studio에서 열기**
   ```bash
   open -a "Android Studio" .
   ```

2. **Gradle 싱크**
   - Android Studio의 "Sync Now" 클릭

3. **에뮬레이터 생성**
   - AVD Manager (Tools → Device Manager)
   - Pixel 6, API 34, AAOS 선택

4. **앱 실행**
   - Run → Run 'app'
   - 또는 Shift+F10

## 폴더 구조

```
app/
├── src/main/kotlin/com/carft/
│   ├── domain/        # 비즈니스 로직 (규칙 엔진, 모델)
│   ├── ui/            # Compose 화면
│   ├── infrastructure/# VehicleAdapter (Pleos SDK)
│   ├── data/          # 저장소
│   └── config/        # DI 설정
└── src/main/res/      # 리소스
```

## 아키텍처

- **UI**: Jetpack Compose
- **상태**: StateFlow + ViewModel
- **규칙 엔진**: 3값 논리 (참/거짓/모름)
- **저장소**: Room DB (확장용) / JSON (현재)
- **테스트**: FakeVehicleAdapter (가상 모드)

## 시연 시나리오

1. 홈 → [새 옵션 만들기]
2. 조립판에 브릭 추가:
   - 상황: "뒷좌석 오른쪽 벨트 매면"
   - 조건: "실내 온도 10°C 이하"
   - 동작: 공조 24°C, 열선 3단, 음성
3. [테스트] → 가상 상태 입력 → [시뮬레이션]

## 다음 단계 (P1)

- [ ] Pleos SDK 실제 연동
- [ ] Room DB 구현
- [ ] 실행 기록 저장
- [ ] 충돌 해결 UI
