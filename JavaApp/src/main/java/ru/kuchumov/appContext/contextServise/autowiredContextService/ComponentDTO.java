package ru.kuchumov.appContext.contextServise.autowiredContextService;

import java.lang.reflect.Constructor;

/*
ДТОшка для хранения информации о прокси компонента
 */

public class ComponentDTO {
    private Object proxyComponent;
    private Object originalInstance;
    private Class<?> originalInstanceClass;
    private Constructor<?> originalCustomAutowiredConstructor;
    private ComponentStatus componentStatus;

    public ComponentDTO(Object proxyComponent, Object originalInstance, Class<?> originalInstanceClass,
                        Constructor<?> originalCustomAutowiredConstructor, ComponentStatus componentStatus) {
        this.proxyComponent = proxyComponent;
        this.originalInstance = originalInstance;
        this.originalInstanceClass = originalInstanceClass;
        this.originalCustomAutowiredConstructor = originalCustomAutowiredConstructor;
        this.componentStatus = componentStatus;
    }

    public Object getProxyComponent() {
        return proxyComponent;
    }

    public ComponentDTO setProxyComponent(Object proxyComponent) {
        this.proxyComponent = proxyComponent;
        return this;
    }

    public Object getOriginalInstance() {
        return originalInstance;
    }

    public ComponentDTO setOriginalInstance(Object originalInstance) {
        this.originalInstance = originalInstance;
        return this;
    }

    public Class<?> getOriginalInstanceClass() {
        return originalInstanceClass;
    }

    public ComponentDTO setOriginalInstanceClass(Class<?> originalInstanceClass) {
        this.originalInstanceClass = originalInstanceClass;
        return this;
    }

    public Constructor<?> getOriginalCustomAutowiredConstructor() {
        return originalCustomAutowiredConstructor;
    }

    public ComponentDTO setOriginalCustomAutowiredConstructor(Constructor<?> originalCustomAutowiredConstructor) {
        this.originalCustomAutowiredConstructor = originalCustomAutowiredConstructor;
        return this;
    }

    public ComponentStatus getComponentStatus() {
        return componentStatus;
    }

    public void setComponentStatus(ComponentStatus componentStatus) {
        this.componentStatus = componentStatus;
    }
}

enum ComponentStatus {
    CONTEXT, COMPONENT
}
