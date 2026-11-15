# 기여 규칙

기획·결정·배경은 컨텍스트 레포 `saramsaram82/hyundai-pleos-context`에 있다. 규칙의 근거와 전체 설명은 그 레포의 `conventions/collaboration.md`를 본다.

## 처음 한 번

```bash
git config core.hooksPath .githooks
```

## 흐름

1. `git switch main && git pull`
2. `git switch -c feat/wiper-block`
3. 커밋 → `git push -u origin feat/wiper-block`
4. PR 생성, 템플릿 작성
5. 리뷰 승인 후 **Squash and merge**

`main`에 직접 커밋·push하지 않는다.

## 브랜치 이름

`{타입}/{kebab-case 영문 설명}` — 예: `feat/wiper-block`, `fix/seat-value-inverted`

## 커밋 메시지·PR 제목

`{타입}: {한국어 요약}` 제목 한 줄 — 예: `feat: 와이퍼 상황 블록 추가`

타입: `feat` `fix` `refactor` `chore` `style` `docs` `test` `perf` `ci`

PR 제목도 같은 형식이다. Squash merge 시 PR 제목이 `main` 커밋 메시지가 된다.

## PR 자동 설정

PR을 열면 자동으로 처리된다.

- 리뷰어: `.github/CODEOWNERS`의 담당자 중 PR 작성자를 뺀 사람
- 라벨: PR 제목의 타입과 같은 라벨 (`feat:` → `feat`). 제목을 고치면 라벨도 바뀐다
- 담당자: PR 작성자
- PR 제목이 형식에 맞지 않으면 `PR 자동 설정` 체크가 실패한다
