FROM openjdk:8
RUN mkdir /home/cora
ENV CW_MAIN_CLASS=schedule.ScheduleMaker
ENV CW_CLASS_PATH=/home/cora/RingetteSchedule-0.0.1-SNAPSHOT-jar-with-dependencies.jar
ENV CW_JAVA_PATH=/usr/local/openjdk-8/bin/java
ENV CW_AUTH_PATH=/mnt/host/auth.json

COPY ./target/corawebif-1.0-SNAPSHOT.jar /home/cora/corawebif-1.0-SNAPSHOT.jar
COPY ./rs_jar/RingetteSchedule-0.0.1-SNAPSHOT-jar-with-dependencies.jar /home/cora/RingetteSchedule-0.0.1-SNAPSHOT-jar-with-dependencies.jar
CMD ["java", "-cp", "/home/cora/corawebif-1.0-SNAPSHOT.jar", "cora.main.CwApplication", "server"]
