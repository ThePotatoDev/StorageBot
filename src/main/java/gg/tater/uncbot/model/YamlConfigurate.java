package gg.tater.uncbot.model;

import lombok.Getter;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class YamlConfigurate {

    public static final String BASE_CONFIG_PATH = System.getProperty("user.dir") + File.separator + "configs" + File.separator;

    private final Map<String, Object> dataMap = new HashMap<>();

    private final Yaml yaml = new Yaml();

    protected final <T> T getYamlValue(String field, Class<T> clazz) {
        return clazz.cast(dataMap.get(field));
    }

}
