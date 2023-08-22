package com.suken27.humanfactorsjava.slack;

import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.*;
import static com.slack.api.model.block.element.BlockElements.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;

@Configuration
public class SlackApp {

    @Bean
    public AppConfig loadOAuthConfig(Environment environment) {
        return AppConfig.builder()
                .singleTeamBotToken(null)
                .signingSecret(environment.getProperty("com.suken27.humanfactors.slack.signingSecret"))
                .clientId(environment.getProperty("com.suken27.humanfactors.slack.clientID"))
                .clientSecret(environment.getProperty("com.suken27.humanfactors.slack.clientSecret"))
                .scope(environment.getProperty("com.suken27.humanfactors.slack.scope"))
                .userScope(environment.getProperty("com.suken27.humanfactors.slack.userScope"))
                .oauthInstallPath(environment.getProperty("com.suken27.humanfactors.slack.installPath"))
                .oauthRedirectUriPath(environment.getProperty("com.suken27.humanfactors.slack.redirectURIPath"))
                .build();
    }

    @Bean
    public App initSlackApp(AppConfig config) {
        App app = new App(config).asOAuthApp(true);
        app.command("/hello", (req, ctx) -> ctx.ack(r -> r.text("What's up?")
                .blocks(asBlocks(
                        section(section -> section.text(markdownText("*Please select a restaurant:*"))),
                        divider(),
                        actions(actions -> actions
                                .elements(asElements(
                                        button(b -> b.text(plainText(pt -> pt.emoji(true).text("Farmhouse")))
                                                .value("v1")),
                                        button(b -> b.text(plainText(pt -> pt.emoji(true).text("Kin Khao")))
                                                .value("v2")))))))));
        return app;
    }

}
