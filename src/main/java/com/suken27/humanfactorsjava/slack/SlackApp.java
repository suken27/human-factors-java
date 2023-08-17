package com.suken27.humanfactorsjava.slack;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;

@Configuration
public class SlackApp {
    
    @Bean
    public App initSlackApp() {
        AppConfig config = new AppConfig();
        //TODO: Move this to a config file
        config.setSigningSecret("c37275215a1b02f8950d2d6951356771");
        config.setSingleTeamBotToken("xoxb-2452501990086-5742975164999-gBHFadxvv3y2q2o5KQ4GZMIo");
        App app = new App(config);
        app.command("/hello", (req, ctx) -> ctx.ack("What's up?"));
        return app;
    }
    
}
