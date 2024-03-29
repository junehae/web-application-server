# 실습을 위한 개발 환경 세팅
* https://github.com/slipp/web-application-server 프로젝트를 자신의 계정으로 Fork한다. Github 우측 상단의 Fork 버튼을 클릭하면 자신의 계정으로 Fork된다.
* Fork한 프로젝트를 eclipse 또는 터미널에서 clone 한다.
* Fork한 프로젝트를 eclipse로 import한 후에 Maven 빌드 도구를 활용해 eclipse 프로젝트로 변환한다.(mvn eclipse:clean eclipse:eclipse)
* 빌드가 성공하면 반드시 refresh(fn + f5)를 실행해야 한다.

# 웹 서버 시작 및 테스트
* webserver.WebServer 는 사용자의 요청을 받아 RequestHandler에 작업을 위임하는 클래스이다.
* 사용자 요청에 대한 모든 처리는 RequestHandler 클래스의 run() 메서드가 담당한다.
* WebServer를 실행한 후 브라우저에서 http://localhost:8080으로 접속해 "Hello World" 메시지가 출력되는지 확인한다.

# 각 요구사항별 학습 내용 정리
* 구현 단계에서는 각 요구사항을 구현하는데 집중한다. 
* 구현을 완료한 후 구현 과정에서 새롭게 알게된 내용, 궁금한 내용을 기록한다.
* 각 요구사항을 구현하는 것이 중요한 것이 아니라 구현 과정을 통해 학습한 내용을 인식하는 것이 배움에 중요하다. 

### 요구사항 1 - http://localhost:8080/index.html로 접속시 응답
* 이 실습을 하기 전에는 InputStream과 BufferedReader가 무엇인지 모르는 상태였다. 이 실습을 하며 저 두 클래스를 사용할 수 있게 되었다. 이 요구 사항의 2단계에는 http 요청 정보의 첫 번째 라인에서 요청 URL을 추출하는 작업이 있다. 이 단계에서 나는 무엇을 하라고 하는지 이해를 하지 못하여서 첫 번째 라인 대신 요청 URL을 추출하는 로그를 출력했는데 이것이 맞는지 잘 모르겠다. 개발자의 중요한 능력 중 하나가 요청 사항을 이해하는 능력이라고 생각하는데 이 요구 사항을 구현하며 문제를 이해하는 능력을 백준사이트의 알고리즘 문제들을 풀면서 이 능력을 키워나가야겠다고 생각했다.

### 요구사항 2 - get 방식으로 회원가입
* 

### 요구사항 3 - post 방식으로 회원가입
* 

### 요구사항 4 - redirect 방식으로 이동
* 

### 요구사항 5 - cookie
* 

### 요구사항 6 - stylesheet 적용
* 

### heroku 서버에 배포 후
* 