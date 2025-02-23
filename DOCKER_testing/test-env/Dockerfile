FROM eclipse-temurin
ARG pat
ENV pat=$pat
WORKDIR /usr/src/myapp
RUN mkdir data
COPY . .
RUN javac Ec.java
CMD ["java","Ec"]

