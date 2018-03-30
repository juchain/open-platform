FROM gitlab.blockshine.net:4567/library/java:8-openjdk

ADD build/libs/open-platform.jar /usr/src/open-platform.jar

WORKDIR /usr/src

CMD ["java", "-Duser.timezone=Asia/Shanghai", "-jar", "open-platform.jar"]
