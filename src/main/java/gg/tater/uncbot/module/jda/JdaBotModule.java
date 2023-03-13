package gg.tater.uncbot.module.jda;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import gg.tater.uncbot.UncBotApplication;
import gg.tater.uncbot.model.DiscordBotModule;
import gg.tater.uncbot.module.dropbox.DropboxService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import java.io.*;
import java.util.Optional;

@RequiredArgsConstructor
public class JdaBotModule extends DiscordBotModule {

    private final DropboxService service;

    private static final String USER_DIRECTORY = System.getProperty("user.dir");
    private static final String JDA_CONFIG_FILE_NAME = "jda_config.yaml";
    private static final String DROP_BOX_CONFIGURATION_PATH = "/configuration";
    private static final String DISCORD_JDA_API_TOKEN_FIELD = "botApiToken";
    private static final String DISCORD_BOT_ACTIVITY_TYPE_FIELD = "botActivityType";
    private static final String DISCORD_BOT_ACTIVITY_DESCRIPTION_FIELD = "botActivityDescription";

    @Override
    public String getId() {
        return "jda_module";
    }

    @Override
    @SneakyThrows
    public void setup() {
        ListFolderResult result = service.getClient()
                .files()
                .listFolder(DROP_BOX_CONFIGURATION_PATH);

        Optional<Metadata> optional = result.getEntries()
                .stream()
                .filter(metadata -> metadata.getName().equalsIgnoreCase(JDA_CONFIG_FILE_NAME))
                .findFirst();

        if (optional.isEmpty()) {
            UncBotApplication.LOGGER.info("Could not load JDA instance. Config is not on Dropbox.");
            return;
        }

        Metadata metadata = optional.get();
        String originalPath = metadata.getPathLower();
        String replacedPath = originalPath.replace("/configuration/", "");

        File file = new File(USER_DIRECTORY, replacedPath);

        try (OutputStream stream = new FileOutputStream(file)) {
            service.getClient()
                    .files()
                    .downloadBuilder(originalPath)
                    .download(stream);
        } catch (IOException | DbxException e) {
            e.printStackTrace();
        }

        getDataMap().putAll(getYaml().load(new FileInputStream(file)));
        boolean deleted = file.delete();
        UncBotApplication.LOGGER.info("Successfully loaded all JDA information from Dropbox config. Deletion status: " + deleted);

        JDABuilder builder = JDABuilder.createDefault(getYamlValue(DISCORD_JDA_API_TOKEN_FIELD, String.class));
        builder.setAutoReconnect(true);
        builder.setBulkDeleteSplittingEnabled(false);
        builder.setAutoReconnect(true);

        Activity.ActivityType activityType = Activity.ActivityType.valueOf(getYamlValue(DISCORD_BOT_ACTIVITY_TYPE_FIELD, String.class));
        String activityDescription = getYamlValue(DISCORD_BOT_ACTIVITY_DESCRIPTION_FIELD, String.class);
        builder.setActivity(Activity.of(activityType, activityDescription));

        JDA jda = builder.build();
    }

    @Override
    public void shutdown() {

    }
}
