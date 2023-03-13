package gg.tater.uncbot.model;

public abstract class DiscordBotModule extends YamlConfigurate {

    public abstract String getId();

    public abstract void setup();

    public abstract void shutdown();

}
