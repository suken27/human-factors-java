package com.suken27.humanfactorsjava.slack;

import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.*;
import static com.slack.api.model.block.element.BlockElements.*;
import static com.slack.api.model.view.Views.*;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.methods.response.views.ViewsPublishResponse;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.event.AppHomeOpenedEvent;
import com.slack.api.model.view.View;
import com.suken27.humanfactorsjava.model.Team;
import com.suken27.humanfactorsjava.model.TeamMember;
import com.suken27.humanfactorsjava.model.controller.ModelController;
import com.suken27.humanfactorsjava.model.exception.TeamManagerNotFoundException;
import com.suken27.humanfactorsjava.slack.exception.UserNotFoundInWorkspaceException;

@Configuration
public class SlackApp {

        public static final Logger logger = LoggerFactory.getLogger(SlackApp.class);

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
                                .oauthRedirectUriPath(environment
                                                .getProperty("com.suken27.humanfactors.slack.redirectURIPath"))
                                .build();
        }

        @Bean
        public App initSlackApp(AppConfig config, ModelController modelController) {
                App app = new App(config).asOAuthApp(true);
                app.event(AppHomeOpenedEvent.class, (payload, ctx) -> {
                        AppHomeOpenedEvent event = payload.getEvent();
                        Team team;
                        try {
                                team = modelController.checkTeamManager(event.getUser(), ctx.getBotToken());
                        } catch (TeamManagerNotFoundException e) {
                                logger.debug("Slack user with id {} tried to access team without being a TeamManager", event.getUser());
                                team = null;
                        } catch (UserNotFoundInWorkspaceException e) {
                                // This is really improbable, but we should handle it anyway
                                logger.error("Slack user with id {} not found in workspace", event.getUser());
                                team = null;
                        }
                        List<LayoutBlock> blocks = new ArrayList<>();
                        if(team != null) {
                                blocks.add(section(section -> section.text(markdownText(mt -> mt.text(
                                                "*You are a team manager* :tada:")))));
                                for(TeamMember member : team.getMembers()) {
                                        blocks.add(section(section -> section.text(markdownText(mt -> mt.text(
                                                        member.getSlackId() + " is a team member.")))));
                                }
                        } else {
                                blocks.add(section(section -> section.text(markdownText(mt -> mt.text(
                                                "*You are not a team manager* :skull_and_crossbones:")))));
                        }

                        // Build a Home tab view
                        View appHomeView = view(view -> view
                                        .type("home")
                                        .blocks(blocks));
                        // Update the App Home for the given user
                        if (event.getView() == null) {
                                ViewsPublishResponse res = ctx.client().viewsPublish(r -> r
                                                .userId(event.getUser())
                                                .view(appHomeView));
                        } else {
                                ViewsPublishResponse res = ctx.client().viewsPublish(r -> r
                                                .userId(event.getUser())
                                                .hash(event.getView().getHash())
                                                .view(appHomeView));
                        }
                        return ctx.ack();
                });
                app.command("/hello", (req, ctx) -> ctx.ack(r -> r.text("What's up?")
                                .blocks(asBlocks(
                                                section(section -> section
                                                                .text(markdownText("*Please select a restaurant:*"))),
                                                divider(),
                                                actions(actions -> actions
                                                                .elements(asElements(
                                                                                button(b -> b.text(plainText(pt -> pt
                                                                                                .emoji(true)
                                                                                                .text("Farmhouse")))
                                                                                                .value("v1")),
                                                                                button(b -> b.text(plainText(pt -> pt
                                                                                                .emoji(true)
                                                                                                .text("Kin Khao")))
                                                                                                .value("v2")))))))));
                return app;
        }

}
