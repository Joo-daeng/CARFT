# core:designsystem

CARFT 화면이 함께 쓰는 디자인 토큰, 아이콘, 공용 컴포넌트. 기준은 Figma 프로토타입(`O9zKnLOvCnJRpj2xje5qpj`)이다.

## 쓰는 법

```kotlin
CarftTheme {
    CarftScreen(topBar = { CarftTopBar("실행 기록", onBack = onBack) }) {
        BlockChip("뒷좌석 벨트를 매면", BlockKind.Situation, icon = CarftIcon.Belt)
    }
}
```

- 색·글꼴·모서리는 `CarftColors`, `CarftType`, `CarftShapes`, `CarftSpacing`만 쓴다. 화면 코드에 hex 색이나 sp 값을 직접 쓰지 않는다.
- Figma 수치는 1px = 1dp로 옮긴다. Pleos Connect 화면이 mdpi(160dpi)라 Figma 1920px 프레임이 앱 영역과 거의 같다.

## 들어 있는 것

| 구분 | 이름 |
| --- | --- |
| 토큰 | `CarftColors`(배경·표면·텍스트·블록 4색·상태색), `CarftType`(Asta Sans 18~60), `CarftShapes`, `CarftSpacing` |
| 아이콘 | `CarftIcon` 28종 + `CarftLogo` (Figma 아이콘 컴포넌트에서 변환) |
| 블록 | `BlockChip`(상황·조건·논리·동작·정차 중만, 크기 3단계), `BlockSlot`(빈 자리), `BlockDot` |
| 상태 | `StatusPill`, `OptionStatus`(대기 중·실행 중·정상 실행·일부만·확인 불가·충돌·해결됨·꺼짐), `WatchBanner` |
| 입력 | `CarftButton`(Primary·Secondary·Outline·Danger·DangerSoft·Ghost, 크기 3단계), `CarftIconButton`, `CarftToggle`, `CarftSegmented` |
| 면 | `CarftCard`, `CardHeader`, `MetricTile`, `VerticalDivider`, `HorizontalDivider` |
| 틀 | `CarftScreen`, `CarftTopBar`(뒤로·제목·부제·액션), `CarftBrandBar`(홈 머리) |
| 오버레이 | `CarftSheet`, `SheetMenuItem`, `DimLayer`, `CarftToast`, `ConflictAlert` |

## 한눈에 보기

debug 빌드에는 런처에 **CARFT 디자인** 앱이 따로 생긴다. 모든 컴포넌트를 한 화면에서 볼 수 있다.

```bash
adb shell am start -n com.hyundai.carlego/.DesignCatalogActivity
```

Android Studio에서는 `catalog/DesignCatalog.kt`의 Preview로 본다.

## 고칠 때

- 컴포넌트를 추가·변경하면 `DesignCatalog`에도 예시를 넣는다.
- 두 사람이 동시에 같은 컴포넌트를 고치지 않도록, 이 모듈 변경은 화면 작업과 다른 PR로 올린다.
- 아이콘 추가: Figma 아이콘 컴포넌트를 SVG로 받아 `res/drawable/ic_carft_{이름}.xml` 벡터로 바꾸고 `CarftIcon`에 한 줄 추가한다.

## 폰트

`res/font/asta_sans.ttf`는 Asta Sans(42dot, OFL 1.1) 가변 폰트를 KS X 1001 한글 2,350자 + 라틴·기호로 줄인 것이다(굵기 300~800 유지). 여기에 없는 글자는 시스템 폰트로 표시된다. 라이선스는 `FONT_LICENSE_OFL.txt`.
