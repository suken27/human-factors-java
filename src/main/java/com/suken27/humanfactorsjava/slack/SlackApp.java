package com.suken27.humanfactorsjava.slack;

import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.*;
import static com.slack.api.model.block.element.BlockElements.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.jakarta_servlet.SlackOAuthAppServlet;

import jakarta.servlet.annotation.WebServlet;

@Configuration
public class SlackApp {

    @Bean
    public AppConfig loadOAuthConfig(Environment environment) {
        return AppConfig.builder()
                .signingSecret(environment.getProperty("com.suken.humanfactors.slack.signingSecret"))
                .clientId(environment.getProperty("com.suken.humanfactors.slack.clientID"))
                .clientSecret(environment.getProperty("com.suken.humanfactors.slack.clientSecret"))
                .scope(environment.getProperty("com.suken.humanfactors.slack.scope"))
                .userScope(environment.getProperty("com.suken.humanfactors.slack.userScope"))
                .redirectUri(environment.getProperty("com.suken.humanfactors.slack.redirectURI"))
                .oauthCompletionUrl(environment.getProperty("com.suken.humanfactors.slack.oauthCompletionURL"))
                .oauthCancellationUrl(environment.getProperty("com.suken.humanfactors.slack.oauthCancellationURL"))
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

    @WebServlet({ "/slack/install", "/slack/oauth" })
    public class SlackOAuthRedirectController extends SlackOAuthAppServlet {
        public SlackOAuthRedirectController(App app) {
            super(app);
        }
    }

}
