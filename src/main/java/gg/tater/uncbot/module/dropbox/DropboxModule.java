package gg.tater.uncbot.module.dropbox;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import gg.tater.uncbot.model.DiscordBotModule;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileInputStream;

public class DropboxModule extends DiscordBotModule implements DropboxService {

    private static final String DROP_BOX_CLIENT_IDENTIFIER = "UncBot";
    private static final String DROP_BOX_API_TOKEN_FIELD = "dropBoxApiToken";

    private DbxClientV2 client;

    @Override
    public String getId() {
        return "dropbox_module";
    }

    @Override
    @SneakyThrows
    public void setup() {
        File configFile = new File(BASE_CONFIG_PATH + "dropbox_config.yaml");
        getDataMap().putAll(getYaml().load(new FileInputStream(configFile)));
        DbxRequestConfig config = DbxRequestConfig.newBuilder(DROP_BOX_CLIENT_IDENTIFIER).build();
        this.client = new DbxClientV2(config, getYamlValue(DROP_BOX_API_TOKEN_FIELD, String.class));
    }

    @Override
    public void shutdown() {}

    @Override
    public DbxClientV2 getClient() {
        return client;
    }
}
