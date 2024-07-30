FROM openjdk:17
EXPOSE 8080
ADD target/tech_hub-ecommerce.jar tech_hub-ecommerce.jar
ENTRYPOINT ["java","-jar","/tech_hub-ecommerce.jar"]