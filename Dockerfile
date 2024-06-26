FROM openjdk:11-jdk-stretch
RUN mkdir -p /root/technicalCodingChalange
COPY ./target/technicalCodingChalange-0.0.1-SNAPSHOT.jar /root/technicalCodingChalange

WORKDIR /root/technicalCodingChalange
EXPOSE 8080
ENV SPRING_OPTS "--spring.profiles.active=default"
ENV JAVA_OPTS "-Djava.security.egd=file:/dev/./urandom"
CMD exec java $JAVA_OPTS -jar technicalCodingChalange-0.0.1-SNAPSHOT.jar $SPRING_OPTS