# Auto Transaction Logger
트렌젝션 요청 응답에 대한 로그를 제공

## 로깅 지원 데이터
- 요청
  - Header
  - Body
  - Param
- 응답
  - Header
  - Body
  - Param

## 로깅 방법
로그를 어떻게 제공할지 선택가능
각 로깅방법에 대해서 사용하고자 하는 로그에 대해서 true 변경

1. 콘솔 로깅
2. DB 로깅
   1. 요청값 로깅
   2. 응답값 로깅

## 로그 마스크 제공
로그데이터에서 민감 데이터를 선택적으로 마스킹 처리 