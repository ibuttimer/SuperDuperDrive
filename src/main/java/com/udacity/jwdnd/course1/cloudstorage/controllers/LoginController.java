package com.udacity.jwdnd.course1.cloudstorage.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static com.udacity.jwdnd.course1.cloudstorage.config.UrlFactory.LOGIN_URL;

/**
 * Login controller
 */
@Controller
@RequestMapping(LOGIN_URL)
public class LoginController extends AbstractController {

    private static final List<String> MODEL_ATTRIBUTES = List.of(
        "logIn", "enterUsername", "enterPassword", "usernameMaxLen", "passwordMaxLen", "invalidCredentials",
        "loggedOut", "signUpHere", "signupSuccess"
    );

    private static final String LOGIN_VIEW = "login";

    @GetMapping()
    public String loginView(Model model) {
        return loginViewSetup(model);
    }

    public String loginViewSetup(Model model) {
        addModelAttributes(model, MODEL_ATTRIBUTES);
        return LOGIN_VIEW;
    }

    public static String loginViewSetup(AbstractController controller, ModelMap model) {
        controller.addModelAttributes(model, MODEL_ATTRIBUTES);
        return LOGIN_VIEW;
    }
}