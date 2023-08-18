package com.suken27.humanfactorsjava.slack;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.model.Action;
import com.slack.api.model.Attachment;

@Configuration
public class SlackApp {
    
    @Bean
    public App initSlackApp() {
        AppConfig config = new AppConfig();
        //TODO: Move this to a config file
        config.setSigningSecret("4fafae3ae6d61272b03bc576d395f3b8");
        config.setSingleTeamBotToken("xoxb-2452501990086-5742975164999-XFXfToNlDKGi9RSpHA56vskR");
        App app = new App(config);
        // write the code for a message with a button
        List<Attachment> attachments = new ArrayList<>();
        Attachment attachment = new Attachment();
        attachment.setText("Would you like to play a game?");
        attachment.setCallbackId("game_selection");
        Action action = new Action();
        action.setName("games_list");
        action.setText("Pick a game...");
        action.setType(Action.Type.SELECT);
        List<Action.Option> options = new ArrayList<>();
        Action.Option option = new Action.Option();
        option.setText("Hearts");
        option.setValue("hearts");
        options.add(option);
        option = new Action.Option();
        option.setText("Bridge");
        option.setValue("bridge");
        options.add(option);
        option = new Action.Option();
        option.setText("Checkers");
        option.setValue("checkers");
        options.add(option);
        option = new Action.Option();
        option.setText("Chess");
        option.setValue("chess");
        options.add(option);
        action.setOptions(options);
        List<Action> actions = new ArrayList<>();
        actions.add(action);
        attachment.setActions(actions);
        attachments.add(attachment);
        app.command("/hello", (req, ctx) -> ctx.ack(r -> r.text("What's up?").attachments(attachments)));
        return app;
    }
    
}
