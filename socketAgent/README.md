# 전문 수신 client


# Usage

- 디렉토리 구성 

```bash
       -- 디렉토리 구성 
               클라이언트 실행파일 :     *.jar  
       logs     : log 생성 디렉토리        
       recvfiles: 수신파일 저장 디렉토리 (수동으로 미리 생성 필요)
       sendfiles: 송신파일 정보 파일 및 송신할 저장 디렉토리 (수동으로 미리 생성 필요)
       resources: 설정파일 저장 디렉토리 

```

- export 파일 명

```bash
  - 2020.03.15 : socketAgent-0.3-JDK-1.6.jar  > JDK 1.6  기반으로 컴파일 
  - 2020.03.11 : socketAgent-0.2-jdk1.6.jar   > JDK 1.6  기반으로 컴파일
  - 2020.02.29 : socketAgent-0.1.jar   > JDK 1.8 기반으로 컴파일
```
 

 
- 일반적인 전문 수신 및 파일 수신 시 
 
```bash
 ]$ java -jar socketAgent-0.3-JDK-1.6.jar -p "KIS_FTAgent" -d ""
   ** application.properties 의 socket.server.SA.recvFileList## 에 정의된 파일명에 기록된 파일을 읽어서 송신 (일자는 전일 기준)
   ** -p 파라미터 추가 (임화혁TJ 요청 / 2020.03.13) env 에 저장된 값을 경로로 사용하게 수정요청 
```

- 특정파일의 전문 수신 및 파일 수신 시 

```bash
 ]$ java -jar socketAgent-0.4-JDK-1.6.jar -p "KIS_FTAgent" -d "MAGNET0001-REPLY.200205"
                                                               ^^^^^^^^^^^^^^^^^^^^^^^
                                                                                                                                               수신파일명

                       
                       
```
 
- 일반적인 전문 송신 및 파일 송신 시 
 
```bash
 $]java -jar socketAgent-0.3-JDK-1.6.jar -p "KIS_FTAgent" -u ""
 ** application.properties 의 socket.client.SA.sendConfigName 에 정의된 파일명에 기록된 파일을 읽어서 송신(일자는 현재일자 기준)
 
```

- 특정일자의 전문 송신 및 파일 송신 시 

```bash
 $]java -jar socketAgent-0.4-JDK-1.6.jar -p "KIS_FTAgent" -u "MAGNET0001-REPLY.200205"
                                                               ^^^^^^^^^^^^^^^^^^^^^^^
                                                                                                                                              송신파일명
  
                       
```

- 로그 저장 경로 (환경변수에 설정된 경로 밑에 아래 경로를 찾게 됨) 
 
```bash
	resource/log4j.properties 파일에 경로가 설정되어 있음. 	
```

- application.properties 내용

```bash
# socket server mode 
# mode value : 
#    T : Test , R : Real 
## 테스트모드/real 모드 구분 , test 모드인 경우는 수신모드에만 적용되며, 수신리스트에 정의된 파일의 특정일자만 가져오게 설정되어 있음.
## 특정일자가 아닌 정해진 일자의 파일을 가져오고자 하는 경우 R로 설정 변경할 것
socket.server.mode=T

# socket server info
socket.server.relayType=SA

# local test 
#socket server ip / port
socket.server.SA.ip=127.0.0.1  ## 파일 송/수신 서버 ip
socket.server.SA.port=30030    ## 파일 송/수신 서버 port

# KIS :: TEST
#socket server ip / port
#socket.server.SA.ip=210.112.100.97
#socket.server.SA.port=5102
# KIS :: PROD
#socket.server.SA.ip=210.112.100.63
#socket.server.SA.port=5102

#org code
## 기관코드
socket.server.SA.orgCode=SCOURT0001

# receive file type code 
# E(EDI) : REPLY , D(DDC) : DDCRE, T(TRNS) : TRANS 
# A(ALL)
## 기술된 파일을 전부 가져오기, 특정 파일만 가져오려 하면, 아래 recvFileList# 에서 항목을 하나만 남기면 된다.
socket.server.SA.recvCode=A

##################################################################
# Receive info  :: 수신 파일 리스트 기술, 기본값은 나열된 파일명을 prefix로 하여 .yyyyMMdd (어제일자) 의 파일을 가져오게 된다. 
## receive file list 
socket.server.SA.recvFileList1=SCOURT0001_TLF
socket.server.SA.recvFileList2=SCOURT0001_REP
socket.server.SA.recvFileList3=SCOURT0001_ZPP
socket.server.SA.recvFileList4=SCOURT0001_KAKAO_REP


# file receive info
## 수신된 파일 저장 경로, fullpath로 적어야만 됨
#socket.client.SA.recvPath=/home/kis/socketAgent/recvfiles
socket.client.SA.recvPath=D:/eGovDev_3.8/socketAgentTest/recvfiles
##################################################################


##################################################################
# Send info :: 파일 송신 모드인 경우 사용되는 값
# send file type code 
# ‘E’ : EDI, ‘I’ : ISP, ‘B’ : BATCH
# A(ALL)
socket.server.SA.sendCode=A

# send file test yn type code 
# ‘1’ : Real Data, ‘0’ : Test Data  :: 실제, 테스트 여부, 현재는 테스트로 되어 있으며, 운영에 반영시 1로 값을 변경하여 real data임을 표시하여야 한다
socket.server.SA.sendTestYn=0

# file send info
#:: 전송할 파일이 위치하는 경로, Full Path로 기술하여야만 됨. 
#socket.client.SA.sendPath=/home/kis/socketAgent/recvfiles
socket.client.SA.sendPath=D:/eGovDev_3.8/socketAgentTest/sendfiles

## 전송할 파일명을 기술하고 있는 파일이 존재하는 경로, Full path로 기술해야만 함
socket.client.SA.sendConfigPath=D:/eGovDev_3.8/socketAgentTest/sendfiles

# file list 저장 파일명
## 전송할 파일명을 기술하고 있는 파일
socket.client.SA.sendConfigName=fileInfo.cfg 
# 적용 일자 타입 
## 일자타입을 설정. 6자리인경우 yyMMdd, 8자리인 경우 yyyyMMdd 로 설정. 현재는 8자리 타입으로 설정됨.
socket.client.SA.sendConfigDateType=yyyyMMdd

##################################################################

# encoding code
## 수신/송신에서 사용되는 encoding 타입
socket.encode=UTF-8

##################################################################
# relay info :: 수신전용

# relayserver use yn 
# 릴레이서버 사용여부 , Y: 사용, N:사용안함
socket.server.relayYn=Y

# relay server info :: 릴레이 시작점 서버 정보
socket.server.ST.ip=127.0.0.1
socket.server.ST.port=23510

##################################################################


# cliet socket timeout
#:: 타임아웃 시간 정보 (단위 : 초)
socket.server.timeout=3
```





