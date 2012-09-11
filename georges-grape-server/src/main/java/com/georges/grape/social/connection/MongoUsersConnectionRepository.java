package com.georges.grape.social.connection;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;
import static org.springframework.data.mongodb.core.query.Criteria.where;

public class MongoUsersConnectionRepository implements UsersConnectionRepository {
    private ConnectionSignUp connectionSignUp;
    private final ConnectionFactoryLocator connectionFactoryLocator;
    private final MongoTemplate mongoTemplate;
    private final TextEncryptor textEncryptor;

    public MongoUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator, MongoTemplate mongoTemplate, TextEncryptor textEncryptor) {
        this.connectionFactoryLocator = connectionFactoryLocator;
        this.mongoTemplate = mongoTemplate;
        this.textEncryptor = textEncryptor;
    }

    public void setConnectionSignUp(ConnectionSignUp connectionSignUp) {
        this.connectionSignUp = connectionSignUp;
    }

    /**
     * Find the ids for local application users that have the given
     * {@link Connection}. Used to support the ProviderSignIn scenario where the
     * user id returned is used to sign a local application user in using his or
     * her provider account. No entries indicates no application users are
     * associated with the connection; SignInThirdPartyController will
     * implicitly register with the app. A single entry indicates that exactly
     * one application user is associated with the connection and is used to
     * sign in that user via SignInThirdPartyController. Multiple entries
     * indicate that multiple application users are associated with the
     * connection and handled as an error by SignInThirdPartyController.
     * 
     * @param connection
     *            the service provider connection resulting from the provider
     *            sign-in attempt
     * @return the user ids associated with the connection.
     */
    @Override
    public List<String> findUserIdsWithConnection(Connection<?> connection) {
        ConnectionKey key = connection.getKey();

        Query q = new Query(where("providerId").is(key.getProviderId()).and("providerUserId").is(key.getProviderUserId()));
        List<UserConnection> conns = mongoTemplate.find(q, UserConnection.class);

        if (conns.size() == 0 && connectionSignUp != null)
        {
            String newUserId = connectionSignUp.execute(connection);
            if (newUserId != null)
            {
                createConnectionRepository(newUserId).addConnection(connection);
                return Arrays.asList(newUserId);
            }
        }
        List<String> localUserIds = new ArrayList<String>();
        for (UserConnection cn : conns)
            localUserIds.add(cn.getUserId());

        return localUserIds;
    }

    /**
     * Find the ids of the users who are connected to the specific provider user
     * accounts.
     * 
     * @param providerId
     *            the provider id, e.g. "qq","weibo"
     * @param providerUserIds
     *            the set of provider user ids. For example, QQ uses openId as
     *            providerUserId
     * @return the set of user ids connected to those service provider users, or
     *         empty if none.
     */
    @Override
    public Set<String> findUserIdsConnectedTo(String providerId, Set<String> providerUserIds) {

        Query q = new Query(where("providerId").is(providerId).and("providerUserId").in(providerUserIds));
        List<UserConnection> conns = mongoTemplate.find(q, UserConnection.class);

        Set<String> localUserIds = new HashSet<String>();
        for (UserConnection cn : conns)
            localUserIds.add(cn.getUserId());

        return localUserIds;
    }

    /**
     * Create a single-user {@link ConnectionRepository} instance for the user
     * assigned the given id. All operations on the returned repository instance
     * are relative to the user.
     * 
     * @param userId
     *            the id of the local user account.
     * @return the ConnectionRepository, exposing a number of operations for
     *         accessing and updating the given user's provider connections.
     */
    @Override
    public ConnectionRepository createConnectionRepository(String userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }
        return new MongoConnectionRepository(userId, connectionFactoryLocator, mongoTemplate);

    }

}
