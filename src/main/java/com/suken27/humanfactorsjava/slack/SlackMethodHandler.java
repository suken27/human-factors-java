package com.suken27.humanfactorsjava.slack;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.users.UsersListResponse;
import com.slack.api.model.User;
import com.suken27.humanfactorsjava.slack.exception.UserNotFoundInWorkspaceException;

@Component
public class SlackMethodHandler {

    private final ConcurrentMap<String, User> usersStoreById = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, User> usersStoreByEmail = new ConcurrentHashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(SlackMethodHandler.class);

    public String retrieveUserId(String email, String botToken) throws UserNotFoundInWorkspaceException, SlackApiException, IOException {
        User user = usersStoreByEmail.get(email);
        if (user == null) {
            fetchUsers(botToken);
            user = usersStoreByEmail.get(email);
        }
        if (user == null) {
            throw new UserNotFoundInWorkspaceException(email);
        }
        return user.getId();
    }

    public String retrieveUserEmail(String id, String botToken)
            throws UserNotFoundInWorkspaceException, SlackApiException, IOException {
        User user = usersStoreById.get(id);
        if (user == null) {
            fetchUsers(botToken);
            user = usersStoreById.get(id);
        }
        if (user == null) {
            throw new UserNotFoundInWorkspaceException(id);
        }
        return user.getProfile().getEmail();
    }

    private void fetchUsers(String botToken) throws SlackApiException, IOException {
        MethodsClient client = Slack.getInstance().methods();
        // Call the users.list method using the built-in WebClient
        UsersListResponse result = client.usersList(r -> r
                .token(botToken));
        for (User user : result.getMembers()) {
            usersStoreById.put(user.getId(), user);
            usersStoreByEmail.put(user.getProfile().getEmail(), user);
        }

    }

}
