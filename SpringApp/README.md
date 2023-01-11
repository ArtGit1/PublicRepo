Spring RESTApi:

- Сохранение, удаление, скачивание файлов. Изменение комментариев к загруженным файлам
- Валидация по типу и размеру с помощью HibernateValidator
- Файлы сохраняются на диск, информация о них (Название, Тип, Размер, Дата загрузки, Изменения, Комментарий, Путь) - PostgreSQL
- Реализован поиск с фильтрацией с помощью CriteriaAPI (фильтры по имени, типу, дате изменения)
- Реализована возможность скачивания нескольких файлов в зип-архиве
- Обработка Exception
- Модульные тесты (JUnit, Mockito)
- Документация - Swagger
- БД развернута в Docker
- Подключен Liquibase