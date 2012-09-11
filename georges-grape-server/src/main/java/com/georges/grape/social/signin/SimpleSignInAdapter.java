/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.georges.grape.social.signin;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.social.connect.Connection;
import org.springframework.web.context.request.NativeWebRequest;

import com.georges.grape.social.connection.UserConnection;
import com.georges.grape.social.connection.UserSignature;


public class SimpleSignInAdapter {

    private MongoTemplate mongoTemplate;
    private SecureRandom random = new SecureRandom();
    
    public SimpleSignInAdapter(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    
    /**
     * @param localUserId Grape user Id, unique, and will be kept as authentication->principle
     */
    public String signIn(String localUserId, String terminalId, long expireTime, Connection<?> connection, NativeWebRequest request) {
    	SignInUtils.signin(localUserId);
    	
    	   Query q = new Query(where("userId").is(localUserId).and("terminalId").is(terminalId));
           List<UserSignature> users = mongoTemplate.find(q, UserSignature.class);
           String sig=generateSigature();
           
           if(users.size()==0)
           {
               System.out.println("new Signin");
               UserSignature newu=new UserSignature(localUserId,terminalId, sig, expireTime);
               System.out.println("new Signin "+newu.toString());
               mongoTemplate.save(newu);
 
           }
           else
           {
               UserSignature user=users.get(0);
               user.setSignature(sig);
               System.out.println("renew Signin "+user.toString());
               mongoTemplate.save(user);
               
           }
           return sig;
    }
    private String generateSigature() {
    return new BigInteger(130, random).toString(32);
    }
    
}
