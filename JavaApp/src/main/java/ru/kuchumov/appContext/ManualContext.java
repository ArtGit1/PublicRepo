package ru.kuchumov.appContext;

import ru.kuchumov.appContext.components.CustomComponent;

import java.util.HashMap;
import java.util.Map;

/*
Еще один вариант контекста, полностью независимый от AutoContext, следовательно, сложно заменяемый в коде
Быстрее AutoContext на 10 процентов
Требования:
1. Унаследовать компоненты от CustomComponent
2. Определить тут геттеры:
public static ClassName getClassName() {
    return (ClassName) ManualContexHandler.getInstance(ClassName.class);
}
3. Инициализировать ничего не надо, в любом месте кода ManualContext.getClassName();
 */

public class ManualContext {

    public static void setArgs(String[] args) {  // Не изменять
        ManualContexHandler.setArgs(args);
    }
    public static String[] getArgs() {  // Не изменять
        return ManualContexHandler.args;
    }



    private static class ManualContexHandler {  // Не изменять
        private final static Map<Class<? extends CustomComponent>, CustomComponent> existingComponents = new HashMap<>();
        private static String[] args;
        private static CustomComponent getInstance(Class<? extends CustomComponent> someClass) {
            if (existingComponents.containsKey(someClass)) {
                return existingComponents.get(someClass);
            }
            try {
                CustomComponent customComponent = someClass.getDeclaredConstructor().newInstance();
                existingComponents.put(someClass, customComponent);
                return customComponent;
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
        private static void setArgs(String[] arguments) {
            args = arguments;
        }
    }
}
