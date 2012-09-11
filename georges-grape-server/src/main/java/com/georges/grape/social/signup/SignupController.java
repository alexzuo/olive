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
package com.georges.grape.social.signup;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;

import com.georges.grape.social.connection.UsernameAlreadyInUseException;
import com.georges.grape.social.message.Message;
import com.georges.grape.social.message.MessageType;
import com.georges.grape.social.signin.SignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

import com.georges.grape.data.GrapeUser;
import com.georges.grape.repository.GrapeUserRepository;

@Controller
public class SignupController {

	@Autowired
	private GrapeUserRepository acountRepository;

	public SignupController() {
	}

	@RequestMapping(value="/signup", method=RequestMethod.GET)
	public SignupForm signupForm(WebRequest request) {
		Connection<?> connection = ProviderSignInUtils.getConnection(request);
		if (connection != null) {
			request.setAttribute("message", new Message(MessageType.INFO, "Your " + StringUtils.capitalize(connection.getKey().getProviderId()) + " account is not associated with a Spring Social Showcase account. If you're new, please sign up."), WebRequest.SCOPE_REQUEST);
			return SignupForm.fromProviderUser(connection.fetchUserProfile());
		} else {
			return new SignupForm();
		}
	}

	@RequestMapping(value="/signup", method=RequestMethod.POST)
	public String signup(@Valid SignupForm form, BindingResult formBinding, WebRequest request) {
		if (formBinding.hasErrors()) {
			return null;
		}
		GrapeUser user = createAccount(form, formBinding);
		if (user != null) {
			SignInUtils.signin(user.getId());
			ProviderSignInUtils.handlePostSignUp(user.getId(), request);
			return "redirect:/";
		}
		return null;
	}

	// internal helpers
	
	private GrapeUser createAccount(SignupForm form, BindingResult formBinding) {
		GrapeUser account = new GrapeUser(form.getUsername(), form.getFirstName()+" "+form.getLastName());
		acountRepository.save(account);
		return account;
	}

}
