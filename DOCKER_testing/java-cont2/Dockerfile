FROM eclipse-temurin

#mozhe i bez WORKDIR ama ne e organizirano ka so treba zatoa pravime folder myapp i stavame se tamu
WORKDIR /usr/src/myapp
COPY . .
RUN mkdir data
RUN javac ExecutionCounter.java
CMD ["java","ExecutionCounter"]