package cc.ibooker.sprovider_api;

import java.util.ServiceLoader;

import cc.ibooker.sprovider_annotation.SProvider;

public class ServiceProvider {
    private static final ServiceLoader<ISProvider> MAP = ServiceLoader.load(ISProvider.class);

    public static ISProvider load(String alias) {
        for (ISProvider iSProvider : MAP) {
            if (iSProvider != null) {
                SProvider annotation = iSProvider.getClass().getAnnotation(SProvider.class);
                if (annotation != null && alias.equals(annotation.alias())) {
                    return iSProvider;
                }
            }
        }
        return null;
    }
}
