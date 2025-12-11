FROM amazoncorretto:17

WORKDIR /app

# JAR 파일을 컨테이너로 복사
# build/libs/*.jar 패턴으로 복사하되, plain.jar는 제외하는 것이 좋음.
# 보통 bootJar 태스크가 실행되면 -SNAPSHOT.jar 가 생성됨.
COPY build/libs/*.jar app.jar

# 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
