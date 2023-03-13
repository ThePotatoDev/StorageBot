package gg.tater.uncbot;

import gg.tater.uncbot.model.DiscordBotModule;
import gg.tater.uncbot.module.clip.VideoClipModule;
import gg.tater.uncbot.module.jda.JdaBotModule;
import gg.tater.uncbot.module.dropbox.DropboxService;
import gg.tater.uncbot.module.dropbox.DropboxModule;
import lombok.SneakyThrows;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

public final class UncBotApplication {

    public static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
    public static final Logger LOGGER = Logger.getLogger(UncBotApplication.class.getName());

    private static final Set<DiscordBotModule> DISCORD_BOT_MODULES = new HashSet<>();

    @SneakyThrows
    public static void main(String[] args) {
        DropboxService service = registerModule(new DropboxModule());
        registerModule(new JdaBotModule(service));
//        registerModule(new VideoClipModule(service));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> DISCORD_BOT_MODULES.forEach(UncBotApplication::unregisterModule)));
    }

    private static <T extends DiscordBotModule> T registerModule(T module) {
        module.setup();
        DISCORD_BOT_MODULES.add(module);
        LOGGER.info("Registered discord bot module: " + module.getId());
        return module;
    }

    private static <T extends DiscordBotModule> T unregisterModule(T module) {
        module.shutdown();
        DISCORD_BOT_MODULES.remove(module);
        LOGGER.info("Unregistered discord bot module: " + module.getId());
        return module;
    }
}
