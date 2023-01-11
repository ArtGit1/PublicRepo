package ru.kuchumov.appContext;

import ru.kuchumov.appContext.components.CustomComponent;

import java.util.HashMap;
import java.util.Map;

/*
ManualContext, но только легко заменяемый в коде на AutoContext и обратно. По скорости выполнения равен обычному ManualContext
(На 10 процентов быстрее AutoContext)
1. Определить геттеры в AutoContext
2. Заоверрайдить их тут в виде:
public static ClassName getClassName() {
    return (ClassName) ManualContexHandler.getInstance(ClassName.class);
}
3. В начале программы проинициализировать:
ContextContainer.ContextInitializer.initReplaceableManualContext(args);
4. Дальше использовать так же как и AutoContext:
ContextContainer.getContext().getClassName();
5. Если нужно обратно поменять на AutoContext, то просто изменить инициализацию
 */

public class ReplaceableManualContext extends ReplaceableManualContextParent {
//public class ReplaceableManualContext extends ReplaceableManualContextParent implements AutoContext {
// Если этот контекст не используется, то убрать implements AutoContext, чтобы дало скомпилироваться

    private ReplaceableManualContext() { // Не изменять
    }


    protected static class ManualContexHandler { // Не изменять
        private final static Map<Class<? extends CustomComponent>, CustomComponent> existingComponents = new HashMap<>();
        protected static String[] args;
        private static ReplaceableManualContext replaceableManualContext = null;
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
        protected static void setArgs(String[] arguments) {
            args = arguments;
        }

        protected static ReplaceableManualContext getContextInstance() {
            if (replaceableManualContext == null) {
                replaceableManualContext = new ReplaceableManualContext();
            }
            return replaceableManualContext;
        }
    }
}
