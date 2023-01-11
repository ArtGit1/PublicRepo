package ru.kuchumov.appContext.contextServise;

/*
    Чтобы не ломать логику интерфейса AutoContext, геттер на стартовые аргументы вынесен сюда
 */

public interface GlobalCustomContext {
    String[] getArgs();
}
