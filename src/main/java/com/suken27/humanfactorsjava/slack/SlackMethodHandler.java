package com.suken27.humanfactorsjava.slack;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.users.UsersListResponse;
import com.slack.api.model.User;
import com.suken27.humanfactorsjava.model.Team;
import com.suken27.humanfactorsjava.model.controller.ModelController;
import com.suken27.humanfactorsjava.model.exception.MemberAlreadyInTeamException;
import com.suken27.humanfactorsjava.model.exception.TeamManagerNotFoundException;
import com.suken27.humanfactorsjava.rest.exception.MemberInAnotherTeamException;
import com.suken27.humanfactorsjava.slack.exception.UserNotFoundInWorkspaceException;

@Component
public class SlackMethodHandler {

    @Autowired
    private ModelController modelController;

    private final ConcurrentMap<String, User> usersStoreById = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, User> usersStoreByEmail = new ConcurrentHashMap<>();

    public String getUserId(String email, String botToken)
            throws UserNotFoundInWorkspaceException, SlackApiException, IOException {
        return getUserByEmail(email, botToken).getId();
    }

    public String getUserEmail(String id, String botToken)
            throws UserNotFoundInWorkspaceException, SlackApiException, IOException {
        return getUserById(id, botToken).getProfile().getEmail();
    }

    public User getUserById(String id, String botToken)
            throws SlackApiException, IOException, UserNotFoundInWorkspaceException {
        User user = usersStoreById.get(id);
        if (user == null) {
            fetchUsers(botToken);
            user = usersStoreById.get(id);
        }
        if (user == null) {
            throw new UserNotFoundInWorkspaceException(id);
        }
        return user;
    }

    public User getUserByEmail(String email, String botToken)
            throws SlackApiException, IOException, UserNotFoundInWorkspaceException {
        User user = usersStoreByEmail.get(email);
        if (user == null) {
            fetchUsers(botToken);
            user = usersStoreByEmail.get(email);
        }
        if (user == null) {
            throw new UserNotFoundInWorkspaceException(email);
        }
        return user;
    }

    public Team checkTeamManager(String id, String slackBotToken)
            throws UserNotFoundInWorkspaceException, SlackApiException, IOException, TeamManagerNotFoundException {
        String email = getUserEmail(id, slackBotToken);
        Team team = modelController.getTeam(email);
        if (team == null) {
            throw new TeamManagerNotFoundException(email);
        }
        team.getManager().setSlackId(id);
        team.setSlackBotToken(slackBotToken);
        return team;
    }

    public Team addTeamMember(String teamManagerId, String userId, String slackBotToken)
            throws UserNotFoundInWorkspaceException, SlackApiException, IOException, TeamManagerNotFoundException,
            MemberAlreadyInTeamException, MemberInAnotherTeamException {
        return modelController.addTeamMember(getUserEmail(teamManagerId, slackBotToken),
                getUserEmail(userId, slackBotToken), userId);
    }

    private void fetchUsers(String botToken) throws SlackApiException, IOException {
        MethodsClient client = Slack.getInstance().methods();
        // Call the users.list method using the built-in WebClient
        UsersListResponse result = client.usersList(r -> r
                .token(botToken));
        for (User user : result.getMembers()) {
            if (!user.isDeleted() && !user.isBot() && user.isEmailConfirmed()) {
                usersStoreById.put(user.getId(), user);
                usersStoreByEmail.put(user.getProfile().getEmail(), user);
            }
        }
    }

}
