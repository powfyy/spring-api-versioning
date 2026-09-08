# spring-api-versioning

Автоконфигурация Spring MVC для версионирования REST API через аннотации — с поддержкой внешних и внутренних (internal) префиксов пути.

## Что делает

Вместо ручного прописывания версии в `@RequestMapping`, версия задаётся аннотацией на контроллере или методе. Библиотека сама собирает итоговый путь, подставляя нужный префикс:

```java
@RestController
@RequestMapping("/users")
@ApiVersion(1)
public class UserController {

    @GetMapping
    public List<User> getUsers() {
        // ...
    }
}
```

Реальный путь эндпоинта: `/api/v1/users`.

## Быстрый старт

**1. Включить автоконфигурацию** на классе конфигурации приложения:

```java
@EnableApiVersioning
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

**2. Настрой префиксы** в `application.yaml`:

```yaml
api:
  versioning:
    external-prefix: /api
    internal-prefix: /internal/api
    path-suffix: /service-name # опционально
```

**3. Расставь `@ApiVersion`** на контроллерах или отдельных методах:

```java
@RestController
@RequestMapping("/orders")
@ApiVersion(1)
public class OrderController {

    @GetMapping
    public List<Order> getOrders() {
        // -> GET /api/v1/orders
    }

    @GetMapping("/{id}")
    @ApiVersion(2) // переопределяет версию класса для конкретного метода
    public OrderDetails getOrderDetails(@PathVariable String id) {
        // -> GET /api/v2/orders/{id}
    }
}
```

## Внутренние (internal) эндпоинты

Флаг `internal = true` переключает базовый префикс на `internal-prefix` — удобно для межсервисного общения, когда внутренние и внешние эндпоинты должны быть чётко разделены по пути (например, для разных правил Spring Security).

```java
@RestController
@RequestMapping("/orders")
@ApiVersion(value = 1, internal = true)
public class InternalOrderController {

    @GetMapping
    public List<Order> getOrders() {
        // -> GET /internal/api/v1/orders
    }
}
```

Флаг наследуется по правилу **ИЛИ**: если `internal = true` стоит хотя бы на классе или хотя бы на методе — итоговый эндпоинт будет internal. Метод не может «снять» internal, унаследованный от класса.

```java
@ApiVersion(value = 1, internal = true)
@RestController
public class MixedController {

    @GetMapping("/a")
    public void a() {
        // internal = true (унаследовано от класса)
    }

    @GetMapping("/b")
    @ApiVersion(1) // internal не указан на методе -> берётся false с метода,
                   // но класс уже дал true -> итог true
    public void b() {
        // internal = true
    }
}
```

## Конфигурация

| Свойство                        | Обязательно | По умолчанию | Описание                                                  |
|----------------------------------|:-----------:|---------------|-------------------------------------------------------------|
| `api.versioning.external-prefix` | нет         | `/api`        | Префикс для обычных (внешних) эндпоинтов                    |
| `api.versioning.internal-prefix` | нет         | `/internal/api` | Префикс для эндпоинтов с `@ApiVersion(internal = true)`   |
| `api.versioning.path-suffix`     | нет         | —             | Дополнительный сегмент, добавляемый сразу после версии       |

> `server.servlet.context-path` в конфиге лучше не задавать одновременно с `external-prefix` — он приклеивается контейнером ко **всем** путям приложения ещё до диспетчеризации, и префиксы будут дублироваться/конфликтовать. Весь префикс пути (`/api`, `/internal/api`) должен управляться через `api.versioning.*`.

## Примеры итоговых путей

| Аннотации                                              | `@RequestMapping`     | Итоговый путь                  |
|---------------------------------------------------------|------------------------|----------------------------------|
| `@ApiVersion(1)` на классе                               | `/users`               | `/api/v1/users`                 |
| `@ApiVersion(2)` на методе, `@ApiVersion(1)` на классе   | `/users/{id}`          | `/api/v2/users/{id}`             |
| `@ApiVersion(value = 1, internal = true)` на классе       | `/orders`               | `/internal/api/v1/orders`       |
| Без `@ApiVersion`                                        | `/health`               | `/health` (версия не подставляется) |


## Определение текущей версии внутри контроллера

Если внутри метода нужно узнать, на какой версии он был вызван (например, для условной логики):

```java
Integer version = apiVersioningHelper.currentVersion();
```

Возвращает null, если версия не определена в пути запроса.

## Настройка Spring Security под версионированные пути

Так как версия — это просто число в сегменте пути, используй wildcard, чтобы не переписывать правила при добавлении новой версии:

```java
.requestMatchers("/api/v*/admin/**").hasAuthority(ADMIN)
.requestMatchers("/internal/api/v*/**").permitAll()
```

## Ограничения

- Версия — целое положительное число (`@ApiVersion(1)`). Семантическое версионирование (`1.2`, `2-beta`) не поддерживается.
- Библиотека рассчитана на сервлетный стек (Spring MVC), для WebFlux не адаптирована.