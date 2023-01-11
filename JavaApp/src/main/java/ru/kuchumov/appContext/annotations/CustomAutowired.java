package ru.kuchumov.appContext.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
Аннотация для AutowiredContext
Ограничения:
1. Только явно указанные типы объектов (не интерфейсы и родительские классы)
2. Нельзя циклический инжект
3. Если нужен контекст - инжектить AutowiredContext
 */

@Target({ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomAutowired {
}
