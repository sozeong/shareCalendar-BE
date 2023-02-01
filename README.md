# 삼쩜삼 백엔드 엔지니어 채용 과제

## 🏚 swagger 주소
http://localhost:8080/swagger-ui.html

## 📌 요구사항 구현여부

### 필수 요구사항
⭕ - 과제 구현 시 Java11, Spring Boot2.6.4, JPA, H2, Gradle을 빠짐없이 모두 활용합니다.<br>
⭕ - 프로젝트 character set은 UTF-8로 설정합니다.<br>
⭕ - DB는 H2 Embeded DB를 사용합니다.<br>
⭕ - 회원가입, 환급액 계산, 유저 정보 조회 API를 구현합니다.<br>
⭕ - 모든 요청과 응답에 대하여 application/json 타입으로 구현합니다.<br>
⭕ - 각 기능 및 제약사항에 대한 단위 테스트를 작성해야하며 gradle 기준으로 실행되야 합니다.<br>
⭕ - swagger를 이용하여 API확인 및 API실행이 가능하도록 구현합니다.<br>
⭕ - 민감정보(주민등록번호, 비밀번호 등)는 암호화 된 상태로 저장합니다.<br>
⭕ - README파일을 이용하여 Swagger 주소 및 요구사항 구현여부, 구현방법, 그리고 검증결과에 대해 작성합니다. 

### API 구축문제
⭕ 1. 다음의 요건을 만족하는 회원가입 API를 작성해 주세요. <br>
⭕ 2. 가입한 회원을 로그인 하는 API를 작성해주세요.<br>
⭕ 3. 가입한 회원정보를 가져오는 API를 작성해주세요.<br>
⭕ 4. 가입한 유저의 정보를 스크랩합니다.<br>
⭕ 5. 유저의 스크랩 정보를 바탕으로 유저의 환급액을 계산합니다.

## 👀 구현방법

### 회원 테이블 생성 DDL
<pre>
CREATE TABLE `USERS` (
  `userNo` int(11) NOT NULL AUTO_INCREMENT COMMENT '회원번호',
  `userId` varchar(30) NOT NULL COMMENT '아이디',
  `password` varchar(500) DEFAULT NULL COMMENT '패스워드',
  `name` varchar(30) DEFAULT NULL COMMENT '이름',
  `regNo` varchar(100) DEFAULT NULL COMMENT '주민등록번호',
  PRIMARY KEY (`userNo`)
)
</pre>
<pre>
CREATE TABLE `USERS_SCRAP` (
  `scrapNo` int(11) NOT NULL AUTO_INCREMENT COMMENT '스크랩번호',
  `userNo` int(11) NOT NULL COMMENT '회원번호',
  `totalSalary` int(11) NOT NULL COMMENT '총급여액',
  `calculatedTax` int(11) NOT NULL COMMENT '산출세액',
  `regDate` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '산출세액',
  PRIMARY KEY (`scrapNo`)
) 
</pre>


### 1. 다음의 요건을 만족하는 회원가입 API를 작성해 주세요. (/szs/signup)

- 회원가입 가능 유저를 userList에 담아서 체크
<pre>
  List<User> userList = new ArrayList<>();
  userList.add(new User(null, null, null, "홍길동", "860824-1655068"));
  userList.add(new User(null, null, null, "김둘리", "921108-1582816"));
  userList.add(new User(null, null, null, "마징가", "880601-2455116"));
  userList.add(new User(null, null, null, "베지터", "910411-1656116"));
  userList.add(new User(null, null, null, "손오공", "820326-2715702"));
</pre>

- 이름/주민번호로 가입 이력 체크
- 아이디 중복 체크
- 비밀번호 암호화(SHA512) 주민번호 암호화(AES256)
- DB 저장

### 2. 가입한 회원을 로그인 하는 API를 작성해주세요. (/szs/login)
- 아이디/비밀번호 가입 이력 체크
- 아이디로 JWT TOKEN 생성 (만료기간 1day)

### 3. 가입한 회원정보를 가져오는 API를 작성해주세요. (/szs/me)
- JWT TOKEN 확인
- 아이디 가입 이력 체크

### 4. 가입한 유저의 정보를 스크랩합니다. (/szs/scrap)
- JWT TOKEN 확인
- 스크랩 URL: <https://codetest.3o3.co.kr/v1/scrap> POST 요청하여 data 조회
- 총급여액/산출세액 계산하여 DB 저장

### 5. 유저의 스크랩 정보를 바탕으로 유저의 환급액을 계산합니다. (/szs/refund)
- JWT TOKEN 확인
- 환급액 계산
- 금액 숫자로 변환

## ✔ 검증결과

### 1. 다음의 요건을 만족하는 회원가입 API를 작성해 주세요. (/szs/signup)
- 회원가입 성공
<pre>
  {
  "status": "201",
  "message": "회원가입 불가 유저",
  "data": null
}
</pre>

- 회원가입 불가 유저
<pre>
{
  "status": "201",
  "message": "회원가입 불가 유저",
  "data": null
}
</pre>
- 회원가입 이력 존재
<pre>
{
  "status": "202",
  "message": "이미 존재하는 회원",
  "data": null
}
</pre>
- 아이디 중복
<pre>
{
  "status": "203",
  "message": "중복되는 회원 아이디",
  "data": null
}
</pre>

### 2. 가입한 회원을 로그인 하는 API를 작성해주세요. (/szs/login)

- 로그인 성공
<pre>
{
  "status": "200",
  "message": "로그인",
  "data": {
    "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJ0ZXN0IiwiaWF0IjoxNjU5NDUyMjQzLCJleHAiOjE2NTk1Mzg2NDMsInN1YiI6ImhvbmcifQ.KL8WVCaz5KzbPokoRSqIBROFSO6x-IFVaZYwKfytNqs",
    "type": "BEARER"
  }
}
</pre>

- 아이디/비밀번호 불일치
<pre>
{
  "status": "201",
  "message": "아이디 또는 비밀번호를 확인해주세요.",
  "data": null
}
</pre>

### 3. 가입한 회원정보를 가져오는 API를 작성해주세요. (/szs/me)
- 내 정보 보기 성공
<pre>
{
  "status": "200",
  "message": "내정보",
  "data": {
    "userNo": "3",
    "userId": "hong",
    "name": "홍길동",
    "regNo": "860824-1655068"
  }
}
</pre>

- 회원 아이디 존재하지 않음
<pre>
{
  "status": "201",
  "message": "해당 User를 찾을 수 없습니다.",
  "data": null
}
</pre>

- JWT TOKEN 오류
<pre>
{
  "status": "202",
  "message": "TOKEN 오류",
  "data": null
}
</pre>

### 4. 가입한 유저의 정보를 스크랩합니다. (/szs/scrap)
- 스크랩 성공
<pre>
 {
  "status": "200",
  "message": "동기스크랩",
  "data": {
    "jsonList": {
      "scrap002": [
        {
          "총사용금액": "2,000,000",
          "소득구분": "산출세액"
        }
      ],
      "scrap001": [
        {
          "소득내역": "급여",
          "총지급액": "24,000,000",
          "업무시작일": "2020.10.03",
          "기업명": "(주)활빈당",
          "이름": "홍길동",
          "지급일": "2020.11.02",
          "업무종료일": "2020.11.02",
          "주민등록번호": "860824-1655068",
          "소득구분": "근로소득(연간)",
          "사업자등록번호": "012-34-56789"
        }
      ],
      "errMsg": "",
      "company": "삼쩜삼",
      "svcCd": "test01",
      "userId": "1"
    },
    "appVer": "2021112501",
    "hostNm": "jobis-codetest",
    "workerResDt": "2022-08-03T13:35:19.042652",
    "workerReqDt": "2022-08-03T13:35:19.042723"
  }
}
</pre>

- 토큰 오류
<pre>
{
  "status": "202",
  "message": "TOKEN 오류",
  "data": null
}
</pre>

- 스크랩 실패(api error)
<pre>
{
  "status": "203",
  "message": "스크랩 실패",
  "data": {
    "code": "-1",
    "message": "요청하신 값은 스크랩 가능유저가 아닙니다."
  }
}
</pre>

- 기타 오류
<pre>
{
  "status": "999",
  "message": "기타 오류",
  "data": null
}
</pre>

### 5. 유저의 스크랩 정보를 바탕으로 유저의 환급액을 계산합니다. (/szs/refund)

- 스크랩 성공
<pre>
{
  "status": "200",
  "message": "환급액",
  "data": {
    "한도": "66만원",
    "이름": "마징가",
    "공제액": "122만 5천원",
    "환급액": "66만원"
  }
}
</pre>

- 회원 아이디 존재하지 않음
<pre>
{
  "status": "201",
  "message": "해당 User를 찾을 수 없습니다.",
  "data": null
}
</pre>

- 토큰 오류
<pre>
{
  "status": "202",
  "message": "TOKEN 오류",
  "data": null
}
</pre>

- 스크랩 정보 없음
<pre>
{
  "status": "203",
  "message": "스크랩 정보 없음",
  "data": null
}
</pre>

- 기타 오류
<pre>
{
  "status": "999",
  "message": "기타 오류",
  "data": null
}
</pre>


