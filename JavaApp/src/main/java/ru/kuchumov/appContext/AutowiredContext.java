package ru.kuchumov.appContext;

/*
Контекст аля-Спринг. Медленнее ручного и автоматического в 2 раза. Все компоненты - прокси с лэйзи инициализацией
Использование:

ContextContainer.ContextInitializer.initAutowiredContext(args);
AutowiredContext autowiredContext = (AutowiredContext) ContextContainer.getContext();
autowiredContext.getComponent(<ClassName>.class)

Или:
Определить геттеры в AutoContext и использовать их:
ContextContainer.getContext().get<ClassName>();
В этом случае становится полностью взаимозаменяемым с другими контекстами

Основная суть:
Можно инжектить зависимости в конструктор с помощью @CustomAutowired над ним
Ограничения на инжект ч/з конструктор:
1. Нельзя циклический инжект (Инжектить контекст)
2. Класс компонентов нельзя определять как интерфейс или как родительский класс (@Qualifier не предусмотрено)) )
3. Контекст инжекстить через AutowiredContext, а не через AutoContext
4. Инжектить только в конструктор, конструкторов можно несколько

 */

public abstract class AutowiredContext implements AutoContext {
    public <T> T getComponent(Class<T> clazz) {
        return null;
    }
}

