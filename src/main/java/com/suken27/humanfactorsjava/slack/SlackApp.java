package com.suken27.humanfactorsjava.slack;

import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.*;
import static com.slack.api.model.block.element.BlockElements.*;
import static com.slack.api.model.view.Views.*;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.views.ViewsPublishResponse;
import com.slack.api.model.event.AppHomeOpenedEvent;
import com.slack.api.model.view.View;

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
        // TODO: Command that adds a new member to the team checking the email of the user
        // to assess it is a team manager or not
        app.event(AppHomeOpenedEvent.class, (payload, ctx) -> {
                // Build a Home tab view
                View appHomeView = view(view -> view
                        .type("home")
                        .blocks(asBlocks(
                                section(section -> section.text(markdownText(mt -> mt.text("*Welcome to your _App's Home_* :tada:")))),
                                section(section -> section.text(markdownText(mt -> {
                                        try {
                                                return mt.text("Your email is: " + ctx.client().usersInfo(r -> r.token(ctx.getBotToken()).user(payload.getEvent().getUser())));
                                        } catch (IOException | SlackApiException e) {
                                                e.printStackTrace();
                                                return mt.text("Error retrieving user info from slack.");
                                        }
                                })))
                        ))
                );
                // Update the App Home for the given user
                if(payload.getEvent().getView() == null) {
                        ViewsPublishResponse res = ctx.client().viewsPublish(r -> r
                                .userId(payload.getEvent().getUser())
                                .view(appHomeView)        
                        );
                } else {
                        ViewsPublishResponse res = ctx.client().viewsPublish(r -> r
                                .userId(payload.getEvent().getUser())
                                .hash(payload.getEvent().getView().getHash())
                                .view(appHomeView)        
                        );
                }
                return ctx.ack();
        });
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
