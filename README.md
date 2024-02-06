<h3>Сервис Авторизации</h3>
<hr>

<p>Данный сервис представляет собой приложение для аутентификации и авторизации пользователей, разработанное с использованием Spring Framework и языка программирования Java 17. </p>

<p>Этот сервис вырван из контекста полной системы - дипломной работы, из-за чего некоторые функции были также обрезаны, уменьшены, либо в не рабочем состоянии. Но на общей работоспособности это естественно не скажется</p>
<p>Использую этот проект, чтобы заполнить пустоту моего гитхаба</p>
<p>Весь бэк дипломной работы написан лично мной, включая весь девопс</p>
<p>Порт написанный в script в ci.yml: <b>-rP 15888</b> динамический(благодаря нгроку) так что можете не пытаться подключиться на наш сервер(компьютер)))</p>
<hr>


<h4>Использованные технологии:</h4>

- Swagger
- CI/CD
- Docker
- REST API
- Postgres
- Unit tests

<h3>Документация</h3>
<hr>
<p>Swagger генерирует автоматическую документацию и интерактивный интерфейс для вашего REST API. После запуска приложения, вы можете получить доступ к Swagger UI по адресу: <h4>http://localhost:9000/auth/swagger-ui</h4></p>


<h3>CI/CD</h3>
<hr>

<p>У нас (разработчиков) дипломного проекта нет лишних денег для покупки сервера, поэтому мы использовали ngrock</p>

<p>Поэтому .gitlab-ci.yml имеет такой вид и такие команды</p>

<p>docker-compose.yml с нашего сервера</p>


```yaml
version: '3.1'
services:
  auth:
    #image: d1mir/auth:v1.02
    build: 'auth'
    container_name: auth
    ports:
      - "9000:9000"
    environment:
      SPRING_PROFILE: prod
      EUREKA_HOST: discovery
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/postgres
    networks:
      - database
      - spring
      
  discovery:
    build: 'discovery'
    container_name: discovery
    ports:
      - "8761:8761"
    environment:
      eureka.instance.hostname: discovery
    networks:
      - spring

  gateway:
    build: 'gateway'
    container_name: gateway
    ports:
      - "8085:8085"
    environment:
      EUREKA_HOST: discovery
    networks:
      - spring

  blog:
    build: 'blog'
    container_name: blog
    ports:
      - "8086:8086"
    environment:
      EUREKA_HOST: discovery
      SPRING_PROFILE: prod
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/blog
      spring.jpa.hibernate.ddl-auto: update
    networks:
      - spring
      - database
    

networks:
  database:
    name: database_network
    external: true
  spring:
    name: spring
    driver: bridge
```

<p>docker compose of Postgres:</p>

```yaml
version: '3.3'

services:
  db:
    image: bitnami/postgresql
    container_name: db
    restart: always
    ports:
      - "5432:5432"
    environment:
      POSTGRES_USER: root
      POSTGRES_PASSWORD: root
      POSTGRES_DB: postgres
    volumes:
      - data:/opt/docker/postgres/postgres
    networks:
      - database


networks:
  database:
    driver: bridge
    name: database_network
    
volumes:
  data:
```
