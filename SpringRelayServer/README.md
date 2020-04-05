# 전문에서 수신된 릴레이 서버

- 클라이언트에서 수신된 전문을 그대로 릴레이 해주기 위한 목적의 socket server입니다. 
	
# relay 서버 종류
- resources/application.properties 파일의 socket.server.relayType 에 정의 되며 아래와 같은 유형을 가진다. 
- 서버 유형 

```bash
 - ST : start relay server :: 릴레이서버 처음 시작점이 되는 서버  
 - FN : end relay server   :: 릴레이서버 마지막이 되며 최종 도착 호스트에 전달하고 리턴 메시지를 받는 역할을 한다
 - SA : stand alone :: 독립형 서버로 받은 메시지를 되돌려주는 역할.(테스트용)

```


# Usage

- jdk 1.8 이상 버전을 기준으로 작성되었으며, jar파일을 실행하는 방법으로 실행한 뒤 로그를 보면 된다. 
- jdk 1.6 버전으로 컴파일 및 배포됨.
- relayPoolServer-0.2-JDK-1.6.jar 로 배포
- 첫번째 인자값만 유효. 
- 
 
```bash
 - start 
 ]$ java -jar relayPoolServer-0.2-JDK-1.6.jar KIS_GateWay1 FN &    
    ==> env 에 등록된 KIS_GateWay1 의 경로를 기준으로 하고 FN 으로 띄운다는 의미.(FN은 의미 없는 경로 이며 알아보기 위한 값)
 
 - stop 
 ]$ ps -ef | grep java
UID       PID   PPID  C STIME TTY      TIME     CMD     
kis      10205  9451  2 20:00 pts/3    00:00:02 java -jar relayPoolServer-0.2-JDK-1.6.jar KIS_GateWay1 FN &
kis      10225  9451  2 20:00 pts/3    00:00:02 java -jar relayPoolServer-0.2-JDK-1.6.jar KIS_GateWay ST &
kis      10291  9451  0 20:02 pts/3    00:00:00 grep java

 
 "ps -ef | grep java" 혹은 "ps -ef | grep 실행파일명" 명령을 실행 후 
 위와 같이 리스트가 나오는 경우   PID 의 번호를 확인 한 뒤 
 
 ]$ kill -9 10205(종료할 PID) 
 명령어로 서버 다운 시킴
 
 
```

- 로그 저장 경로
 
```bash
	resource/log4j.properties 파일에 경로가 설정되어 있음. 	
```

- application.properties 내용 (st => application.properties_ST_server 내용 참조, fn => application.properties_FN_server 내용 참조)

```bash
# application.properties

# Server Type
# ST : start relay server  
# FN : end relay server  
# R0~R9 : between relay server 
# SA : stand alone
socket.server.relayType=SA   <== 서버 종류
#socket.server.relayType=ST
#socket.server.relayType=R0
#socket.server.relayType=R1
#socket.server.relayType=SA

# socket server - stand alone 모드 
# 변수 생성 방법 (ST,R0 - R9, FN)
# socket.server.{socket.server.relayType value}.ip
# socket.server.{socket.server.relayType value}.port

socket.server.SA.ip=127.0.0.1
socket.server.SA.port=23510


# socket server 시작 
socket.server.ST.ip=127.0.0.1
socket.server.ST.port=23510

# socket server relay 0
socket.server.R0.ip=127.0.0.1
socket.server.R0.port=23515

## socket server relay 1
#socket.server.R1.ip=127.0.0.1
#socket.server.R1.port=23979

# socket server 마지막
socket.server.FN.ip=127.0.0.1
socket.server.FN.port=23590

# 마지막 socket server 가 토스할 서버 정보.
socket.host.ip=127.0.0.1
socket.host.port=23690

# thread number : thread 풀에서 사용가능한 최대 쓰레드 수. 
thread.maxNum=1000


# encoding code
socket.server.encoding=UTF-8


```




