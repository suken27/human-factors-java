package com.suken27.humanfactorsjava.slack;

import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.*;
import static com.slack.api.model.block.element.BlockElements.*;

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
        config.setSigningSecret("4fafae3ae6d61272b03bc576d395f3b8");
        config.setSingleTeamBotToken("xoxb-2452501990086-5742975164999-XFXfToNlDKGi9RSpHA56vskR");
        App app = new App(config);
        app.command("/hello", (req, ctx) -> ctx.ack(r -> r.text("What's up?")
            .blocks(asBlocks(
                section(section -> section.text(markdownText("*Please select a restaurant:*"))),
                divider(),
                actions(actions -> actions
                    .elements(asElements(
                        button(b -> b.text(plainText(pt -> pt.emoji(true).text("Farmhouse"))).value("v1")),
                        button(b -> b.text(plainText(pt -> pt.emoji(true).text("Kin Khao"))).value("v2"))
                    ))
                )
            ))
        ));
        return app;
    }

}
