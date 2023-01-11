package ru.kuchumov.appContext.contextServise.autowiredContextService;

import ru.kuchumov.appContext.AutowiredContext;
import ru.kuchumov.appContext.annotations.CustomAutowired;
import ru.kuchumov.appContext.exceptions.CustomContextException;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
Сервис с логикой работы AutowiredContext (создание, инициализация компонентов, работа методов самого контекста)
 */

public class AutowiredContextService {
    private static MethodHandlerImplementation methodHandlerImplementation;
    private static Map<Class<?>, ComponentDTO> existingComponents;
    private static Map<Class<?>, Class<?>> conformity;
    private static String[] arguments;

    public static AutowiredContext getContextInstance(String[] args) {
        arguments = args;
        methodHandlerImplementation = new MethodHandlerImplementation();
        existingComponents = new HashMap<>();
        conformity = new HashMap<>();
        return (AutowiredContext) createProxyInstance(AutowiredContext.class, ComponentStatus.CONTEXT, true);
    }

    public static Object createProxyInstance(Class<?> clazz, ComponentStatus componentStatus, boolean isContext) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setSuperclass(clazz);
        Object instance;
        List<Constructor<?>> twoConstructors = null;
        try {
            if (isContext) {
                instance = proxyFactory.createClass().getConstructor().newInstance();
            } else {
                Constructor<?>[] constructors = proxyFactory.createClass().getDeclaredConstructors();
                twoConstructors = getCustomAutowiredConstructors(clazz, constructors);
                List<Object> parameters = new ArrayList<>();
                for (Class<?> parClass:
                        twoConstructors.get(1).getParameterTypes()) {
                    parameters.add(getOrCreateProxyInstance(parClass));
                }
                if (parameters.isEmpty()) {
                    instance = twoConstructors.get(1).newInstance();
                } else {
                    instance = twoConstructors.get(1).newInstance(parameters.toArray());
                }
            }


        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        ((ProxyObject) instance).setHandler(methodHandlerImplementation);
        ComponentDTO componentDTO;
        if (isContext) {
            componentDTO = new ComponentDTO(instance, null, clazz, null, componentStatus);
        } else {
            componentDTO = new ComponentDTO(instance, null, clazz, twoConstructors.get(0), componentStatus);
        }
        existingComponents.put(instance.getClass(), componentDTO);
        conformity.put(clazz, instance.getClass());
        return instance;
    }

    private static Object getOrCreateProxyInstance(Class<?> clazz) {
        if (conformity.containsKey(clazz)) {
            return existingComponents.get(conformity.get(clazz)).getProxyComponent();
        } else {
            return createProxyInstance(clazz, ComponentStatus.COMPONENT, false);
        }
    }

    public static Object runOriginalMethod(Method method, Object object, Object[] objects) {
        if (existingComponents.get(object.getClass()).getComponentStatus().equals(ComponentStatus.CONTEXT)) {
            return runImplementedMethod(method, objects);
        }
        try {
            ComponentDTO componentDTO = existingComponents.get(object.getClass());
            Object originalInstance = componentDTO.getOriginalInstance();
            if (originalInstance == null) {
                originalInstance = createNewOriginalInstance(componentDTO);
                existingComponents.put(object.getClass(), componentDTO.setOriginalInstance(originalInstance));
            }
            return method.invoke(originalInstance, objects);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static Object runImplementedMethod(Method method, Object[] objects) {
        String methodName = method.getName();
        if (methodName.equals("getComponent")) {
            if (!conformity.containsKey((Class<?>) objects[0])) {
                createProxyInstance((Class<?>) objects[0], ComponentStatus.COMPONENT, false);
            }
            return existingComponents.get(conformity.get((Class<?>) objects[0])).getProxyComponent();
        } else {
            if (methodName.equals("getArgs")) {
                return arguments;
            }
            if (methodName.startsWith("get")) {
                Class<?> returnType = method.getReturnType();
                if (!conformity.containsKey(returnType)) {
                    Object clazz;
                    if (objects.length == 0) {
                        clazz = method.getReturnType();
                    } else {
                        clazz = objects[0];
                    }
                    createProxyInstance((Class<?>) clazz, ComponentStatus.COMPONENT, false);
                }
                return existingComponents.get(conformity.get(returnType)).getProxyComponent();
            } else {
                return null;
            }
        }
    }

    private static Object createNewOriginalInstance(ComponentDTO componentDTO) {
        Constructor<?> constructor = componentDTO.getOriginalCustomAutowiredConstructor();
        return createNewOriginalInstanceWithInjection(constructor);
    }

    private static Object createNewOriginalInstanceWithInjection(Constructor<?> constructor) {
        try {
            Class<?>[] parameters = constructor.getParameterTypes();
            List<Object> parInstances = new ArrayList<>();
            if (parameters.length == 0) {
                return constructor.newInstance();
            }
            for (Class<?> parameter :
                    parameters) {
                   parInstances.add(getOrCreateProxyInstance(parameter));
            }
                return constructor.newInstance(parInstances.toArray());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Constructor<?>> getCustomAutowiredConstructors(Class<?> clazz, Constructor<?>[] proxyConstructors) {
        List<Constructor<?>> constructors = new ArrayList<>();
        Constructor<?>[] originalConstructors = clazz.getDeclaredConstructors();
        if (originalConstructors.length == 1) {
            constructors.add(originalConstructors[0]);
            constructors.add(proxyConstructors[0]);
            return constructors;
        } else {
            for (int i = 0; i < originalConstructors.length; i++) {
                if (originalConstructors[i].isAnnotationPresent(CustomAutowired.class)) {
                    constructors.add(originalConstructors[i]);
                    constructors.add(proxyConstructors[i]);
                    break;
                }
            }
                }
        if (constructors.isEmpty()) {
            throw new CustomContextException("Не найдено конструктора с CustomAutowired у " + clazz);
        }
        return constructors;
    }

}
