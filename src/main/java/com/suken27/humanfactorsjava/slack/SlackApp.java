package com.suken27.humanfactorsjava.slack;

import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.*;
import static com.slack.api.model.block.element.BlockElements.*;
import static com.slack.api.model.view.Views.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.methods.SlackApiException;
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
                        List<LayoutBlock> blocks = new ArrayList<>();
                        addTeamBlocks(event.getUser(), ctx.getBotToken(), blocks, app, modelController);

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

        private void addTeamBlocks(String user, String botToken, List<LayoutBlock> blocks, App app,
                        ModelController modelController) {
                Team team = null;
                try {
                        team = modelController.checkTeamManager(user, botToken);
                } catch (TeamManagerNotFoundException e) {
                        logger.debug("Slack user with id {} tried to access team without being a TeamManager", user);
                        blocks.add(section(section -> section.text(markdownText(mt -> mt.text(
                                        "*You are not a team manager* :skull_and_crossbones:")))));
                } catch (UserNotFoundInWorkspaceException e) {
                        // This is really improbable, but we should handle it anyway
                        logger.error("Slack user with id [{}] not found in workspace. This exception should never be thrown.",
                                        user, e);
                        blocks.add(section(section -> section.text(markdownText(mt -> mt.text(
                                        "*Unexpected error: Couldn't find the current user in the workspace* :warning:")))));
                } catch (SlackApiException e) {
                        if (e.getError() != null && e.getError().getError().equals("ratelimited")) {
                                logger.info("Too many requests to the Slack Api, the maximum amount of requests has been exceeded",
                                                e);
                                blocks.add(section(section -> section.text(markdownText(mt -> mt.text(
                                                "*Too many requests: Please try again in "
                                                                + e.getResponse().header("Retry-After")
                                                                + " seconds * :warning:")))));
                        }
                        logger.error("Error ocurred when using the SlackApi", e);
                        blocks.add(section(section -> section.text(markdownText(mt -> mt.text(
                                        "*Unexpected error: Error ocurred when using the SlackApi* :warning:")))));
                } catch (IOException e) {
                        logger.error("Error ocurred when using the SlackApi", e);
                        blocks.add(section(section -> section.text(markdownText(mt -> mt.text(
                                        "*Unexpected error: Error ocurred when using the SlackApi* :warning:")))));
                }
                if (team == null) {
                        return;
                }
                blocks.add(section(section -> section.text(markdownText(mt -> mt.text(
                                "*You are a team manager* :tada:")))));
                blocks.add(divider());
                listTeamMembers(team, blocks);
                blocks.add(divider());
                addTeamMemberAddBlock(blocks, app);
        }

        private void listTeamMembers(Team team, List<LayoutBlock> blocks) {
                for (TeamMember member : team.getMembers()) {
                        blocks.add(section(section -> section.text(markdownText(mt -> mt.text(
                                        member.getSlackId() + " is a team member.")))));
                }
        }

        private void addTeamMemberAddBlock(List<LayoutBlock> blocks, App app) {
                blocks.add(input(input -> input
                                .blockId("team_member_select_block")
                                .element(usersSelect(us -> us
                                                .actionId("team_member_select_action")
                                                .placeholder(plainText("Pick a user from the dropdown list"))))
                                .label(plainText("Add team members"))));
                blocks.add(actions(action -> action
                                .blockId("team_member_button_block")
                                .elements(asElements(button(b -> b
                                                .actionId("team_member_add_action")
                                                .text(plainText("Add selection")))))));

                app.blockAction("team_member_select_action", (req, ctx) -> {
                        return ctx.ack();
                });
                app.blockAction("team_member_add_action", (req, ctx) -> {
                        logger.debug("Team member add action received");
                        logger.debug("Request payload: {}", ctx.getAdditionalValues().get("team_member_select_block"));
                        return ctx.ack();
                });
        }

}