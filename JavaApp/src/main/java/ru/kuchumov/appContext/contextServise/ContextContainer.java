package ru.kuchumov.appContext.contextServise;

import ru.kuchumov.appContext.AutoContext;
import ru.kuchumov.appContext.ReplaceableManualContext;
import ru.kuchumov.appContext.components.CustomComponent;
import ru.kuchumov.appContext.contextServise.autowiredContextService.AutowiredContextService;
import ru.kuchumov.appContext.exceptions.CustomContextException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

/*
    Основной класс для использования контекста
    Содержит:
    - контекст
    - инициализатор контекста
    - перехватчик для методов, определенных в интерфейсе Context
 */

public class ContextContainer {
    private static AutoContext context;

    public static AutoContext getContext() {
        if (context == null) {
            throw new CustomContextException("Сначала инициализируйте контекст - initContex()");
        }
        return context;
    }



    /*
    Инициализатор контекста
     */
    public static class ContextInitializer {
        private static AutoContext appContext;


        /*
        Инициализация контекста с определением класса компонента по названию геттера
         */
        public static void initContextWithStaticComponentsPackage(String componentsPath, String[] args) {
            initialization(componentsPath, args, ContextType.WITH_STATIC_COMPONENTS_PACKAGE);
        }


        /*
        Инициализация контекста с определением класса компонента по типу возвращаемого значения в геттере
         */
        public static void initContextWithDeclaringComponentsByReturnTypeOfGetter(String[] args) {
            initialization(null, args, ContextType.WITH_DECLARING_COMPONENTS_BY_RETURN_TYPE_OF_GETTER);
        }

        /*
        Инициализация ручного контекста, взаимозаменяемого с AutoContext
         */
        public static void initReplaceableManualContext(String[] args) {
            initialization(null, args, ContextType.REPLACEABLE_MANUAL_CONTEXT);
        }

        /*
        Инициализация контекста с возможностью инжектить в конструктор
         */
        public static void initAutowiredContext(String[] args) {
            initialization(null, args, ContextType.AUTOWIRED_CONTEXT);
        }

        private static void initialization(String componentsPath, String[] arguments, ContextType contextType) {
            if (appContext == null) {
                if (contextType.equals(ContextType.WITH_DECLARING_COMPONENTS_BY_RETURN_TYPE_OF_GETTER) || contextType.equals(ContextType.WITH_STATIC_COMPONENTS_PACKAGE)) {
                    CustomInvocationHandler customInvocationHandler = new CustomInvocationHandler(componentsPath, arguments, contextType);
                    ClassLoader someInterfaceClassLoader = ProxyContext.class.getClassLoader();
                    Class<?>[] interfaces = ProxyContext.class.getInterfaces();
                    appContext = (AutoContext) Proxy.newProxyInstance(someInterfaceClassLoader, interfaces, customInvocationHandler);
                    context = appContext;
                } else if (contextType.equals(ContextType.REPLACEABLE_MANUAL_CONTEXT)) {
                    appContext = (AutoContext) ReplaceableManualContext.getContextInstance().setArgs(arguments);
                    context = appContext;
                } else {
                    appContext = AutowiredContextService.getContextInstance(arguments);
                    context = appContext;
                }

            } else {
                System.out.println("Контекст уже создан. Используйте getContex()");
            }
        }


        /*
        Перехватчик вызова методов интерфейса Context
         */
        private static class CustomInvocationHandler implements InvocationHandler {
            private String COMPONENTS_PATH;
            private final ContextType contextType;
            private final String[] args;

            private final HashMap<String, CustomComponent> existingComponents;

            public CustomInvocationHandler(String COMPONENTS_PATH, String[] arguments, ContextType contextType) {
                this.contextType = contextType;
                if (contextType == ContextType.WITH_STATIC_COMPONENTS_PACKAGE) {
                    this.COMPONENTS_PATH = COMPONENTS_PATH + ".";
                }
                existingComponents = new HashMap<>();
                args = arguments;
            }


            /*
            Перехват вызова методов, определенных в интерфейсе Context
            - сначала ищет среди уже созданных экземпляров компонентов по имени метода
            - если не найдено, то возвращает новый и записывает в Map
             */
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                String methodName = method.getName();

                if (!methodName.startsWith("get")) { // Проверка на правильность именования метода
                    throw new CustomContextException("Неверное именование метода: " + methodName);
                }

                if (existingComponents.containsKey(methodName)) { // Проверка на существование компонента
                    return existingComponents.get(methodName);
                }

                if (methodName.equals("getArgs")) { // Если геттер на стартовые аргументы (прописан в GlobalCustomContex)
                    return this.args;
                }


                try {
                    switch (contextType) { // Если не существует, создание и возврат нового
                        case WITH_STATIC_COMPONENTS_PACKAGE -> {
                            return invokeWithStaticComponentsPackage(methodName);
                        }
                        case WITH_DECLARING_COMPONENTS_BY_RETURN_TYPE_OF_GETTER -> {
                            return invokeWithDeclaringComponentsByReturnTypeOfGetter(methodName, method);
                        }
                    }
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                    throw new CustomContextException("Неизвестная ошибка контекста: \n" + e.getMessage());
                }
                return null;
            }


            /*
            Создание экземпляра компонента по названию геттера
             */
            private Object invokeWithStaticComponentsPackage(String methodName) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
                String className = COMPONENTS_PATH + methodName.substring(3);

                try {

                    CustomComponent customComponent = (CustomComponent) Class.forName(className).getDeclaredConstructor().newInstance();
                    existingComponents.put(methodName, customComponent);
                    return customComponent;

                } catch (ClassNotFoundException e) { // Некоторые ошибки неверного использования контекста

                    throw new CustomContextException("Неверное именование метода: " + methodName + "\nНе найден класс: " +
                            COMPONENTS_PATH + methodName.substring(3) + "\n" + e.getMessage());

                } catch (ClassCastException e) {

                    throw new CustomContextException(COMPONENTS_PATH + methodName.substring(3) +
                            " не является компонентом.\n" +
                            "Укажите это явно - implements CustomComponent");
                }
            }


            /*
            Создание экземпляра компонента по типу возвращаемого значения геттера
             */
            private Object invokeWithDeclaringComponentsByReturnTypeOfGetter(String methodName, Method method) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
                Class<?> someClass = method.getReturnType();

                try {
                    CustomComponent customComponent = (CustomComponent) someClass.getDeclaredConstructor().newInstance();
                    existingComponents.put(methodName, customComponent);
                    return customComponent;
                } catch (ClassCastException e) { // Некоторые ошибки неверного использования контекста

                    throw new CustomContextException(someClass.getName() +
                            " не является компонентом.\n" +
                            "Укажите это явно - implements CustomComponent");
                }
            }
        }

        private enum ContextType {
            WITH_STATIC_COMPONENTS_PACKAGE,
            WITH_DECLARING_COMPONENTS_BY_RETURN_TYPE_OF_GETTER,
            REPLACEABLE_MANUAL_CONTEXT,
            AUTOWIRED_CONTEXT
        }
    }
}
