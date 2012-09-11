package com.georges.grape.social.signin;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.connect.web.ProviderSignInAttempt;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.view.RedirectView;

import com.georges.grape.controller.BaseGrapeController;
import com.georges.grape.data.GrapeException;
import com.georges.grape.data.GrapeException.ErrorStatus;
import com.georges.grape.data.GrapeUser;
import com.georges.grape.repository.GrapeUserRepository;

@Controller
@RequestMapping("/signin")
public class SignInThirdPartyController extends BaseGrapeController {

    private final static Log logger = LogFactory.getLog(SignInThirdPartyController.class);

    private final ConnectionFactoryLocator connectionFactoryLocator;
    private final UsersConnectionRepository usersConnectionRepository;
    private boolean useAuthenticateUrl;
    @Autowired
    private SimpleSignInAdapter signInAdapter;
    @Autowired
    private GrapeUserRepository userRepository;

    private SecureRandom random = new SecureRandom();

    /**
     * Creates a new provider sign-in controller.
     * 
     * @param connectionFactoryLocator
     *            the locator of {@link ConnectionFactory connection factories}
     *            used to support provider sign-in. Note: this reference should
     *            be a serializable proxy to a singleton-scoped target instance.
     *            This is because {@link ProviderSignInAttempt} are
     *            session-scoped objects that hold ConnectionFactoryLocator
     *            references. If these references cannot be serialized,
     *            NotSerializableExceptions can occur at runtime.
     * @param usersConnectionRepository
     *            the global store for service provider connections across all
     *            users. Note: this reference should be a serializable proxy to
     *            a singleton-scoped target instance.
     * @param signInAdapter
     *            handles user sign-in
     */
    @Inject
    public SignInThirdPartyController(ConnectionFactoryLocator connectionFactoryLocator,
            UsersConnectionRepository usersConnectionRepository) {
        this.connectionFactoryLocator = connectionFactoryLocator;
        this.usersConnectionRepository = usersConnectionRepository;
        this.useAuthenticateUrl = true;
    }

    @RequestMapping(value = "/{providerId}", method = RequestMethod.GET)
    public RedirectView signIn(@PathVariable String providerId, @RequestParam("terminal") String terminalId, NativeWebRequest request) {
        OAuth2ConnectionFactory<?> connectionFactory = (OAuth2ConnectionFactory<?>) connectionFactoryLocator.getConnectionFactory(providerId);

        OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();
        OAuth2Parameters parameters = getOAuth2Parameters(providerId, terminalId, request);

        String oauth2Url;

        if (useAuthenticateUrl) {
            oauth2Url = oauthOperations.buildAuthenticateUrl(GrantType.AUTHORIZATION_CODE, parameters);
        } else {
            oauth2Url = oauthOperations.buildAuthorizeUrl(GrantType.AUTHORIZATION_CODE, parameters);
        }
        System.out.println("going to redirect to " + oauth2Url);
        return new RedirectView(oauth2Url);

    }

    /**
     * Process the authentication callback from an OAuth 2 service provider.
     * Called after the user authorizes the authentication, generally done once
     * by having he or she click "Allow" in their web browser at the provider's
     * site. Handles the provider sign-in callback by first determining if a
     * local user account is associated with the connected provider account. If
     * so, signs the local user in by delegating to
     * {@link SignInAdapter#signIn(String, Connection, NativeWebRequest)}. If
     * not, redirects the user to a signup page to create a new account with
     * {@link ProviderSignInAttempt} context exposed in the HttpSession.
     * 
     * @see ProviderSignInAttempt
     * @see ProviderSignInUtils
     */
    @RequestMapping(value = "/{providerId}", method = RequestMethod.GET, params = "code")
    public RedirectView oauth2Callback(@PathVariable String providerId,
            @RequestParam("code") String code,
            @RequestParam("terminal") String terminalId,
            NativeWebRequest request) {
        try {
            OAuth2ConnectionFactory<?> connectionFactory = (OAuth2ConnectionFactory<?>) connectionFactoryLocator.getConnectionFactory(providerId);
            Connection<?> connection = null;

            HttpServletRequest nativeRequest = request.getNativeRequest(HttpServletRequest.class);
            String redirect = nativeRequest.getRequestURL().toString();
            System.out.println("redirect: " + redirect);
            
            AccessGrant accessGrant = null;
            try {
                accessGrant = connectionFactory.getOAuthOperations().exchangeForAccess(code, redirect, null);
                connection = connectionFactory.createConnection(accessGrant);
            } catch (HttpClientErrorException e) {
                logger.warn("HttpClientErrorException while completing connection: " + e.getMessage());
                logger.warn("      Response body: " + e.getResponseBodyAsString());
                throw e;
            }
            return handleSignIn(connection, providerId, terminalId, accessGrant.getExpireTime(), request);
        } catch (Exception e) {

            e.printStackTrace();
            logger.warn("Exception while handling OAuth2 callback (" + e.getMessage() + "). Redirecting to ");
            return sendResult(GrapeException.ErrorStatus.INTERANL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/callback", method = RequestMethod.GET, params = "status")
    public String sendLoginResult(@RequestParam int status,  ModelMap model) {
         return "signin_failure";
     }

    @RequestMapping(value = "/callback", method = RequestMethod.GET, params = "uid")
    public String sendLoginResult(@RequestParam String uid, @RequestParam String sig, ModelMap model) {

        GrapeUser user = userRepository.findById(uid);
        model.addAttribute("user", user);
        return "signin_success";
    }

    // internal helpers
    private RedirectView handleSignIn(Connection<?> connection, String providerId, String terminalId, long expireTime, NativeWebRequest request) {
        // try to find local user Ids, usersConnectionRepository can auto create
        // the account and return the user Id
        List<String> userIds = usersConnectionRepository.findUserIdsWithConnection(connection);
        if (userIds.size() == 0) {
            logger.error("failed to create local User");
            return sendResult(GrapeException.ErrorStatus.INTERANL_SERVER_ERROR);

        } else if (userIds.size() == 1) {
            usersConnectionRepository.createConnectionRepository(userIds.get(0)).updateConnection(connection);

            String signature = signInAdapter.signIn(userIds.get(0), terminalId, expireTime, connection, request);
            GrapeUser user = userRepository.findById(userIds.get(0));
            if (user == null)
            {
                logger.error("can not find such user in the user repository");
                return sendResult(GrapeException.ErrorStatus.INTERANL_SERVER_ERROR);
            }
            return sendResult(user.getId(), signature, expireTime);

        } else {
            logger.error("there is multiple local users for the same connection");
            return sendResult(GrapeException.ErrorStatus.INTERANL_SERVER_ERROR);
        }
    }

    private String generateSigature() {
        return new BigInteger(130, random).toString(32);
    }

    private OAuth2Parameters getOAuth2Parameters(String providerId, String terminalId, NativeWebRequest request) {
        OAuth2Parameters params = new OAuth2Parameters();

        HttpServletRequest nativeRequest = request.getNativeRequest(HttpServletRequest.class);
        String redirect = nativeRequest.getRequestURL().toString()+"?terminal="+terminalId;
        params.setRedirectUri(redirect);

        if (providerId.equals("qq")) {
            params.setScope("get_user_info");
            params.set("display", "mobile");
        } else if (providerId.equals("weibo")) {
            params.set("display", "mobile");
        } else {
            throw new GrapeException(GrapeException.ErrorStatus.INTERANL_SERVER_ERROR, "unsupported providerId " + providerId);
        }

        return params;
    }

    private RedirectView sendResult(String userId, String sig, long expire) {
        return new RedirectView("callback?uid=" + userId + "&sig=" + sig+"&expire="+expire, true);
    }

    private RedirectView sendResult(ErrorStatus status) {
        return new RedirectView("callback?status=" + status.value(), true);
    }

}
