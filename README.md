# MovieMate: Watch Together

**MovieMate: Watch Together** - приложение, созданное для генерации релевантных
подборок фильмов по жанрам для двоих и поиска первого обоюдно понравившегося фильма.
**MovieMate: Watch Together** позволяет двум людям, находящимся рядом,
выбрать кино для совместного досуга.

## Стек

- Spring:
    - Boot
    - Web
    - Data JPA
- Lombok
- PostgreSQL
- OpenAPI
- Java 17

## Сборка

В переменных окружения, либо в resource/application.yaml прописываем корректные данные для подключения к базе данных
PostgreSQL, которую необходимо заранее развернуть.

Сборка на Java 17

```shell
mvn clean package
```

В папке target появится файл [moviemate-0.0.1-SNAPSHOT.jar](target/moviemate-0.0.1-SNAPSHOT.jar), который на сервере
запускаем с помощью

```shell
java -jar moviemate-0.0.1-SNAPSHOT.jar
```
