package com.suken27.humanfactorsjava.slack;

import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.*;
import static com.slack.api.model.block.element.BlockElements.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.jakarta_servlet.SlackAppServlet;
import com.slack.api.bolt.jakarta_servlet.SlackOAuthAppServlet;

import jakarta.servlet.annotation.WebServlet;

@Configuration
public class SlackApp {

    @Autowired
    private Environment environment;

    @Bean
    public App initSlackApp() {
        AppConfig config = new AppConfig();
        config.setSigningSecret(environment.getProperty("com.suken.humanfactors.slack.signingSecret"));
        config.setClientId(environment.getProperty("com.suken.humanfactors.slack.clientID"));
        config.setClientSecret(environment.getProperty("com.suken.humanfactors.slack.clientSecret"));
        config.setScope(environment.getProperty("com.suken.humanfactors.slack.scope"));
        config.setUserScope(environment.getProperty("com.suken.humanfactors.slack.userScope"));
        config.setRedirectUri(environment.getProperty("com.suken.humanfactors.slack.redirectURI"));
        config.setOauthCompletionUrl(environment.getProperty("com.suken.humanfactors.slack.oauthCompletionURL"));
        config.setOauthCancellationUrl(environment.getProperty("com.suken.humanfactors.slack.oauthCancellationURL"));
        App app = new App(config).asOAuthApp(true).enableTokenRevocationHandlers();
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

    @WebServlet("/slack/events")
    public class SlackEventsController extends SlackAppServlet {
        public SlackEventsController(App app) {
            super(app);
        }
    }

    @WebServlet("/slack/install")
    public class SlackOAuthInstallController extends SlackOAuthAppServlet {
        public SlackOAuthInstallController(App app) {
            super(app);
        }
    }

    @WebServlet("/slack/oauth_redirect")
    public class SlackOAuthRedirectController extends SlackOAuthAppServlet {
        public SlackOAuthRedirectController(App app) {
            super(app);
        }
    }

}
