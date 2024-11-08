# MovieMate: Watch Together

**MovieMate: Watch Together** - приложение, созданное для генерации релевантных
подборок фильмов по жанрам для двоих и поиска первого обоюдно понравившегося фильма.
**MovieMate: Watch Together** позволяет двум людям, находящимся рядом,
выбрать кино для совместного досуга.

[Видео-презентация](https://disk.yandex.ru/d/2hgxUALmLd4fIQ)<br>
[Презентация](https://docs.google.com/presentation/d/1BF1rZZe5X0MjMT2s9wwvjVFb_E5W_osR/edit?usp=sharing&ouid=109217328439205395137&rtpof=true&sd=true)

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
