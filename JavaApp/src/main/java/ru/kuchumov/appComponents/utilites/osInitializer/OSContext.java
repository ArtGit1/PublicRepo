package ru.kuchumov.appComponents.utilites.osInitializer;

public interface OSContext {
    OSStrategy getOSStrategy();
}

class OSContextImplemenatation implements OSContext {
    private final OSStrategy osStrategy;

    public OSContextImplemenatation(OSStrategy osStrategy) {
        this.osStrategy = osStrategy;
    }

    @Override
    public OSStrategy getOSStrategy() {
        return osStrategy;
    }
}
