package gg.tater.uncbot.module.clip;

import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import gg.tater.uncbot.UncBotApplication;
import gg.tater.uncbot.model.DiscordBotModule;
import gg.tater.uncbot.module.dropbox.DropboxService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class VideoClipModule extends DiscordBotModule {

    private static final String DROP_BOX_CLIP_PATH = "/clips";
    private static final String USER_DIRECTORY = System.getProperty("user.dir");

    private final List<Metadata> videoMetaData = new ArrayList<>();

    private final DropboxService service;

    @Override
    public String getId() {
        return "video_module";
    }

    @Override
    public void setup() {
        File targetDirectory = new File(USER_DIRECTORY + File.separator + "downloads");
        boolean created = targetDirectory.mkdirs();
        UncBotApplication.LOGGER.info("Target video download path created: " + created);

        fetchVideoMetaData();
        UncBotApplication.EXECUTOR_SERVICE.scheduleAtFixedRate(() -> {
            fetchVideoMetaData();
            UncBotApplication.LOGGER.info("Re-fetched all video metadata from Dropbox.");
        }, 0L, 5L, TimeUnit.MINUTES);
    }

    @Override
    public void shutdown() {}

    @SneakyThrows
    public Optional<File> getRandomGeneratedVideoFile() {
        Metadata randomMetaData = videoMetaData.get(ThreadLocalRandom.current().nextInt(videoMetaData.size()));
        String originalPath = randomMetaData.getPathLower();

        String alteredRandomMetaDataPath = randomMetaData.getPathLower()
                .replace(DROP_BOX_CLIP_PATH, "")
                .replace(" ", "_");

        File file = new File(USER_DIRECTORY, "downloads/" + alteredRandomMetaDataPath);

        try (OutputStream stream = new FileOutputStream(file)) {
            service.getClient()
                    .files()
                    .downloadBuilder(originalPath)
                    .download(stream);
        }

        return Optional.empty();
    }


    @SneakyThrows
    private void fetchVideoMetaData() {
        ListFolderResult result = service.getClient()
                .files()
                .listFolder(DROP_BOX_CLIP_PATH);
        result.getEntries().forEach(metadata -> {
            videoMetaData.add(metadata);
            UncBotApplication.LOGGER.info("Fetched video metadata: " + metadata.getName());
        });
    }
}
