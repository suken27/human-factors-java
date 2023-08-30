package com.suken27.humanfactorsjava.slack;

import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.*;
import static com.slack.api.model.block.element.BlockElements.*;
import static com.slack.api.model.view.Views.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.context.Context;
import com.slack.api.methods.SlackApiException;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.event.AppHomeOpenedEvent;
import com.slack.api.model.view.View;
import com.slack.api.model.view.ViewState.Value;
import com.suken27.humanfactorsjava.model.dto.QuestionDto;
import com.suken27.humanfactorsjava.model.dto.TeamDto;
import com.suken27.humanfactorsjava.model.dto.TeamMemberDto;
import com.suken27.humanfactorsjava.model.dto.UserDto;
import com.suken27.humanfactorsjava.model.exception.MemberAlreadyInTeamException;
import com.suken27.humanfactorsjava.model.exception.TeamManagerNotFoundException;
import com.suken27.humanfactorsjava.rest.exception.MemberInAnotherTeamException;
import com.suken27.humanfactorsjava.slack.exception.UserNotFoundInWorkspaceException;

@Configuration
public class SlackApp {

        @Autowired
        private SlackMethodHandler slackMethodHandler;

        public static final Logger logger = LoggerFactory.getLogger(SlackApp.class);

        private static final String USER_SELECT_ACTION_ID = "team_member_select_action";
        private static final String ADD_MEMBER_BUTTON_ACTION_ID = "team_member_add_action";

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
        public App initSlackApp(AppConfig config) {
                App app = new App(config).asOAuthApp(true);
                app.event(AppHomeOpenedEvent.class, (payload, ctx) -> {
                        AppHomeOpenedEvent event = payload.getEvent();
                        List<LayoutBlock> blocks = addTeamBlocks(event.getUser(), ctx.getBotToken());
                        // Build a Home tab view
                        View appHomeView = view(view -> view
                                        .type("home")
                                        .blocks(blocks));
                        // Update the App Home for the given user
                        if (event.getView() == null) {
                                updateView(appHomeView, event.getUser(), null, ctx);
                        } else {
                                updateView(appHomeView, event.getUser(), null, ctx);
                        }
                        return ctx.ack();
                });
                app.command("/questions", (req, ctx) -> ctx.ack(r -> {
                        try {
                                launchQuestions(ctx);
                        } catch (IOException | SlackApiException e) {
                                logger.error("Error ocurred when using the SlackApi", e);
                        }
                        return null;
                }));
                return addActionHandlers(app);
        }

        private List<LayoutBlock> addTeamBlocks(String user, String botToken) {
                TeamDto team = null;
                List<LayoutBlock> blocks = new ArrayList<>();
                try {
                        team = slackMethodHandler.checkTeamManager(user, botToken);
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
                        blocks.add(header(h -> h.text(plainText("You are not a team manager"))));
                        return blocks;
                }
                blocks.add(header(h -> h.text(plainText("You are a team manager"))));
                blocks.add(divider());
                listTeamMembers(team, blocks);
                blocks.add(divider());
                addTeamMemberBlock(blocks);
                return blocks;
        }

        private App addActionHandlers(App app) {
                app.blockAction(USER_SELECT_ACTION_ID, (req, ctx) -> {
                        logger.debug("Team member select action received. Payload: {}", req.getPayload());
                        return ctx.ack();
                });
                return addTeamMemberHandler(app);
        }

        private void listTeamMembers(TeamDto team, List<LayoutBlock> blocks) {
                for (TeamMemberDto member : team.getMembers()) {
                        if(member.getSlackId() != null) {
                                blocks.add(section(section -> section.text(markdownText(mt -> mt.text(
                                        "<@" + member.getSlackId() + "> is a team member.")))));
                        } 
                }
        }

        private void addTeamMemberBlock(List<LayoutBlock> blocks) {
                blocks.add(actions(action -> action
                                .blockId("team_member_add_block")
                                .elements(asElements(
                                                usersSelect(us -> us
                                                                .actionId(USER_SELECT_ACTION_ID)
                                                                .placeholder(plainText(
                                                                                "Pick a user from the dropdown list"))),
                                                button(b -> b
                                                                .actionId(ADD_MEMBER_BUTTON_ACTION_ID)
                                                                .text(plainText("Add selection")))))));
        }

        private App addTeamMemberHandler(App app) {
                return app.blockAction(ADD_MEMBER_BUTTON_ACTION_ID, (req, ctx) -> {
                        logger.debug("Team member add action received. Payload: {}", req.getPayload());
                        Map<String, Value> values = req.getPayload().getView().getState().getValues().get("team_member_add_block");
                        if (values == null) {
                                logger.error("Team member add action received but no values were found in the actions block. Payload: {}",
                                                req.getPayload());
                                return ctx.ack();
                        }
                        Value value = values.get(USER_SELECT_ACTION_ID);
                        if(value == null) {
                                logger.error("Team member add action received but the user select block was not among the values included. Payload: {}",
                                                req.getPayload());
                                return ctx.ack();
                        }
                        String selectedUserId = value.getSelectedUser();
                        if(selectedUserId == null) {
                                logger.error("Team member add action received but no user was selected. Payload: {}",
                                                req.getPayload());
                                return ctx.ack();
                        }
                        String teamManagerId = req.getPayload().getUser().getId();
                        TeamDto team = null;
                        try {
                                team = slackMethodHandler.addTeamMember(teamManagerId, selectedUserId,
                                                ctx.getBotToken());
                                value.setSelectedUser(null);
                                View appHomeView = view(view -> view
                                        .type("home")
                                        .blocks(addTeamBlocks(teamManagerId, ctx.getBotToken())));
                                updateView(appHomeView, teamManagerId, null, ctx);
                                logger.debug("Team member [{}] added to the team managed by [{}]", selectedUserId,
                                        teamManagerId);
                        } catch (MemberAlreadyInTeamException e) {
                                logger.debug("Slack user with id [{}] tried to add a team member that is already in the team",
                                                teamManagerId);
                        } catch (MemberInAnotherTeamException e) {
                                logger.debug("Slack user with id [{}] tried to add a team member that is already in another team",
                                                teamManagerId);
                        } catch (SlackApiException | IOException | UserNotFoundInWorkspaceException
                                        | TeamManagerNotFoundException e) {
                                logger.error("Error ocurred when trying to add the team member [{}] to the team managed by [{}]",
                                                selectedUserId, teamManagerId, e);
                        }
                        return ctx.ack();
                });
        }

        private List<LayoutBlock> questionBlocks(List<QuestionDto> questions) {
                List<LayoutBlock> blocks = new ArrayList<>();
                for(QuestionDto question : questions) {
                        blocks.addAll(questionBlock(question));
                        blocks.add(divider());
                }
                return blocks;
        }

        private List<LayoutBlock> questionBlock(QuestionDto question) {
                List<LayoutBlock> blocks = new ArrayList<>();
                blocks.add(section(section -> section
                        .text(markdownText(mt -> mt.text(question.getQuestionText())))));
                for (String option : question.getOptions()) {
                        blocks.add(actions(action -> action
                                .elements(asElements(
                                        button(b -> b
                                                .text(plainText(option))
                                                .value(option)
                                                .actionId("question_response_action"))
                                ))
                        ));
                }
                return blocks;
        }

        private void updateView(View view, String userId, String hash, Context context) throws IOException, SlackApiException {
                context.client().viewsPublish(r -> {
                        r.view(view);
                        r.userId(userId);
                        r.hash(hash);
                        return r;
                });
        }

        private void launchQuestions(Context context) throws IOException, SlackApiException {
                // TODO: Check if the user is a team manager
                Map<UserDto, List<QuestionDto>> questions = slackMethodHandler.launchQuestions(context.getRequestUserId(), context.getBotToken());
                for(Entry<UserDto, List<QuestionDto>> entry : questions.entrySet()) {
                        context.client().chatPostMessage(r -> r
                                .channel(entry.getKey().getSlackId())
                                .blocks(questionBlocks(entry.getValue()))
                                .token(context.getBotToken()));
                }
        }

}