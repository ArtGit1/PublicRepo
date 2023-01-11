package ru.kuchumov.appComponents.utilites.osInitializer;

import ru.kuchumov.appContext.components.CustomComponent;

public class OSInitializer implements CustomComponent {
    private OSContext osContext;

    public OSInitializer() {
        switch (getOSName()) {
            case LINUX, WINDOWS_PS -> this.osContext = new OSContextImplemenatation(new LinuxOrPSStrategy());
            case WINDOWS_CMD -> this.osContext = new OSContextImplemenatation(new WindowsCMDStrategy());
        }
    }

    public OSContext getOSContext() {
        return osContext;
    }



    private AvailableOS getOSName() {
        String os = System.getProperty("os.name");
        String sessionName = System.getenv().get("SESSIONNAME");
        if (os.toLowerCase().startsWith("lin")) {
            return AvailableOS.LINUX;
        } else if (os.toLowerCase().startsWith("win")) {
            if (sessionName == null) {
                return AvailableOS.WINDOWS_PS;
            } else {
                return AvailableOS.WINDOWS_CMD;
            }
        } else {
            System.out.println("Ошибка инициализации операционной системы");
            System.exit(-1);
            return null;
        }
    }
}

enum AvailableOS {WINDOWS_CMD, WINDOWS_PS, LINUX}
