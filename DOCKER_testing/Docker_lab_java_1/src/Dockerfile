FROM eclipse-temurin
COPY . /usr/src/myapp/
#COPY html/products.txt /var/www/html/
WORKDIR /usr/src/myapp
RUN javac ProductsManagement.java
CMD ["java", "ProductsManagement"]