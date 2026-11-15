# CARFT

Pleos Connect 기반 노코드 차량 자동화 Android 앱 (현대 앱 공모전 출품작).

## 컨텍스트

기획(`spec.md`), 확정 결정(`decisions.md`), Figma 캡처(`refs/figma/`), Pleos SDK 조사(`research/`)는 컨텍스트 레포 `hyundai-pleos-context`에 있다. 이 레포 옆(`../hyundai-pleos-context`)에 클론되어 있으면 작업 전에 관련 문서를 읽는다. 없으면 사용자에게 위치를 묻는다.

## 아키텍처

- UI: Jetpack Compose, 상태: ViewModel + StateFlow
- `domain/`은 Android·Pleos SDK에 의존하지 않는다. SDK 호출은 `infrastructure/`의 `VehicleAdapter` 구현에만 둔다
- 가상 모드는 `FakeVehicleAdapter`로 동작한다
- 공용 색·타이포·아이콘은 `core/designsystem`을 쓴다

## Git 규칙

@CONTRIBUTING.md

- `main`에서 작업 중이면 커밋 전에 규칙에 맞는 브랜치를 만든다
- 훅(`.githooks/`)이 막으면 `--no-verify`로 우회하지 말고 원인을 고친다. 사용자가 명시적으로 지시한 경우만 예외
- `git push`와 PR 생성은 사용자가 그 시점에 지시했을 때만 한다
- 파일을 명시해 스테이징한다 (`git add -A` 금지)
