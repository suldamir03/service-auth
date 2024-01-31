<h3>Сервис Авторизации</h3>
<p>Присутствует swagger по пути: <span>/auth/swagger-ui</span></p>
<h3>Запуск одного приклада</h3>

```text
docker-compose up -d --build [название образа]   --- в папке проекта, для запуска только сервиса авторизации
```



<h3>DevOps commands</h3>

```text
docker logs d1mir/gateway --- чек логов бэка 
mvn clean package spring-boot:repackage --- формирование джарки
docker build . -t ngateway  ---билдим image 
docker tag back:latest d1mir/gateway ---билдим image for dockerhub
docker push d1mir/gateway ---push to dockerhub 
 scp -rP 16994 ./target/auth.jar  public@0.tcp.eu.ngrok.io:/opt/docker/auth/auth.jar
```