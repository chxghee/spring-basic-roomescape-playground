#!/bin/bash

# 명령 에러 발생 시 종료
set -e

PROJECT_ROOT="/home/ubuntu/app"
APP_NAME="spring-basic-roomescape-playground"

APP_LOG="$PROJECT_ROOT/application.log"
APP_ERROR_LOG="$PROJECT_ROOT/error.log"
DEPLOY_LOG="$PROJECT_ROOT/deploy.log"

# 배포 중 에러 발생으로 중단 시 로그 기록 함수
on_error() {
  echo "********** [배포 중 에러 발생] : $(date +%Y-%m-%d\ %H:%M:%S) **********" >> $DEPLOY_LOG
  echo "   -> 실패한 명령어: '$BASH_COMMAND'" >> $DEPLOY_LOG
  echo "   -> 위치: ${BASH_SOURCE[1]}:${LINENO[1]}" >> $DEPLOY_LOG
  exit 1
}
trap on_error ERR

echo "=========== [배포 시작] : $(date +%Y-%m-%d\ %H:%M:%S) ===========" >> $DEPLOY_LOG

cd $PROJECT_ROOT

# 1. 최신 코드 pull
echo "> Git pull" >> $DEPLOY_LOG
git pull >> $DEPLOY_LOG 2>&1

# 2. 프로젝트 빌드
echo "> 프로젝트 빌드 시작" >> $DEPLOY_LOG
chmod +x ./gradlew
./gradlew build >> $DEPLOY_LOG 2>&1

# 3. jar 파일 시간 순 정렬 후 가장 상단에 있는(가장 최신) plain 이 아닌 jar 파일을 선택
JAR_FILE=$(ls -t $PROJECT_ROOT/build/libs/*.jar | grep -v "\-plain.jar$" | head -n 1)

# 4. 실행 중인 애플리케이션의 PID
CURRENT_PID=$(pgrep -f "$APP_NAME")

# 5. 실행 중인 애플리케이션이 있으면 종료
echo "> 실행 중인 애플리케이션이 있다면 종료" >> $DEPLOY_LOG
if [ -z "$CURRENT_PID" ]; then
  echo "  -> 현재 실행 중인 애플리케이션이 없습니다." >> $DEPLOY_LOG
else
  echo "  -> 실행 중인 애플리케이션 종료 (PID: $CURRENT_PID)" >> $DEPLOY_LOG
  kill -15 $CURRENT_PID
  sleep 5
fi

# 6. 새 애플리케이션 백그라운드 실행
echo "> 새 애플리케이션 실행" >> $DEPLOY_LOG
nohup java -jar $JAR_FILE > $APP_LOG 2> $APP_ERROR_LOG &    # 정상 출력 로그-> > APP_LOG / 비정상 출력 로그 -> 2> ERROR_LOG

# 7. 애플리케이션 실행 여부 체크
NEW_PID=$(pgrep -f "$APP_NAME")
if [ -n "$NEW_PID" ]; then
  echo "  -> 애플리케이션 실행 성공 (PID: $NEW_PID)" >> $DEPLOY_LOG
else
  echo "  -> 애플리케이션 실행 실패" >> $DEPLOY_LOG
  exit 1
fi

echo "=========== [배포 완료] : $(date +%Y-%m-%d\ %H:%M:%S) ===========" >> $DEPLOY_LOG
