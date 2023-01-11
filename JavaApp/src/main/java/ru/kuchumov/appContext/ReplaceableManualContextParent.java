package ru.kuchumov.appContext;

/*
Нужно для работы ReplaceableManualContext
 */

public abstract class ReplaceableManualContextParent {
    public String[] getArgs() {
        return ReplaceableManualContext.ManualContexHandler.args;
    }

    public ReplaceableManualContext setArgs(String[] args) {
        ReplaceableManualContext.ManualContexHandler.setArgs(args);
        return (ReplaceableManualContext) this;
    }

    public static ReplaceableManualContext getContextInstance() {
        return ReplaceableManualContext.ManualContexHandler.getContextInstance();
    }
}
