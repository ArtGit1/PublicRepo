package ru.kuchumov.appContext.contextServise.autowiredContextService;

import javassist.util.proxy.MethodHandler;

import java.lang.reflect.Method;

/*
Перехватчик вызовов прокси-компонентов
 */

public class MethodHandlerImplementation implements MethodHandler {
    @Override
    public Object invoke(Object o, Method method, Method method1, Object[] objects) {
        return AutowiredContextService.runOriginalMethod(method, o, objects);
    }
}
