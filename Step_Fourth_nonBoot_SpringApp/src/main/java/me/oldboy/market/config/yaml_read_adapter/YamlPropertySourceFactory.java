package me.oldboy.market.config.yaml_read_adapter;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.util.Properties;

/**
 * Фабрика для загрузки свойств из YAML файлов в Spring Environment.
 *
 * @see PropertySourceFactory
 */
public class YamlPropertySourceFactory implements PropertySourceFactory {

    /**
     * Загружает свойства из YAML файла и создает PropertySource.
     *
     * @param name            имя источника свойств
     * @param encodedResource ресурс с YAML файлом
     * @return PropertySource с свойствами из YAML
     */
    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource encodedResource) {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(encodedResource.getResource());
        Properties props = factory.getObject();
        return new PropertiesPropertySource(encodedResource.getResource().getFilename(), props);
    }
}
