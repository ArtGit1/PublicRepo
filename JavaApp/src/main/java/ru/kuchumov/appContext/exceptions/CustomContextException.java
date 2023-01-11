package ru.kuchumov.appContext.exceptions;

/*
    Ошибка при неверном создании контекста (неправильные геттеры, отсутствие наследования и пр.)
 */

public class CustomContextException extends RuntimeException {
    public CustomContextException(String message) {
        super(message);
    }
}