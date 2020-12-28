package com.udacity.jwdnd.course1.cloudstorage.controllers;

import com.udacity.jwdnd.course1.cloudstorage.misc.ResourceStore;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static com.udacity.jwdnd.course1.cloudstorage.config.UrlFactory.SIGNUP_URL;

/**
 * Signup controller
 */
@Controller()
@RequestMapping(SIGNUP_URL)
public class SignupController extends AbstractController {

    private static final List<String> MODEL_ATTRIBUTES = List.of(
            "enterFirstName", "enterLastName", "enterUsername", "enterPassword", "signUp", "backToLogin",
            "signupSuccess0", "signupSuccess1", "signupSuccess2",
            "usernameMaxLen", "passwordMaxLen", "firstnameMaxLen", "lastnameMaxLen"
    );

    private final UserService userService;

    public SignupController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String signupView(Model model) {
        addModelAttributes(model, MODEL_ATTRIBUTES);
        return "signup";
    }

    /**
     * Sign up the specified user
     * @param user - user details
     * @param model - model
     * @return
     */
    @PostMapping()
    public String signupUser(@ModelAttribute User user, Model model) {
        String signupError = null;

        if (!userService.isUsernameAvailable(user.getUsername())) {
            signupError = "usernameExists";
        } else {
            int rowsAdded = userService.createUser(user);
            if (rowsAdded < 0) {
                signupError = "signupError";
            }
        }

        if (signupError == null) {
            model.addAttribute("signupSuccess", true);
        } else {
            signupError = ResourceStore.getBundle().getString(signupError);
            model.addAttribute("signupError", signupError);
        }

        return signupView(model);
    }
}