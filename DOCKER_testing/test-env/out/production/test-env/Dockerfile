FROM eclipse-temurin
#ARG name
#ENV name=$name
WORKDIR /usr/src/myapp

RUN mkdir data
COPY . .
RUN javac Pr.java
CMD ["java","Pr"]
