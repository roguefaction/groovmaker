package com.example.groovmaker.controller;

import com.example.groovmaker.model.User;
import com.example.groovmaker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    //default site view and login view
    @GetMapping(value = "/login")
    public ModelAndView login() {
        //create a view object and sets this view to login

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @GetMapping(value = "/register")
    public ModelAndView register() {

        ModelAndView modelAndView = new ModelAndView();
        User user = new User();

        // user object added to displaying page (?)
        modelAndView.addObject("user", user);
        modelAndView.setViewName("register");
        return modelAndView;
    }

    @PostMapping(value = "/register")
    public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult) {

        ModelAndView modelAndView = new ModelAndView();
        User userExists = userService.findUserByEmail(user.getEmail());

        // check if email is already taken
        if (userExists != null) {
            bindingResult
                    .rejectValue("email", "error.user", "This email is already taken");
        }

        // check if input has errors
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("register");
        } else {

            // if everything is ok, create new user and redirect to register view
            userService.saveUser(user);
            modelAndView.addObject("successMessage", "Registration successful");
            modelAndView.addObject("user", new User());
            modelAndView.setViewName("register");

        }
        return modelAndView;
    }

    @GetMapping(value = "admin/home")
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView();

        // get current users name from auth object
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());

        // welcome objects added to displaying page
        modelAndView.addObject("WelcomeUser", "Welcome, " + user.getName());
        modelAndView.addObject("username", user.getName());
        modelAndView.addObject("adminMessage","Content Available Only for Users with Admin Role");
        modelAndView.setViewName("admin/home");

        return modelAndView;
    }

    @GetMapping(value = {"/","/welcome"})
    public ModelAndView welcome(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("welcome");

//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        User user = userService.findUserByEmail(authentication.getName());
//        modelAndView.addObject("WelcomeUser", "Welcome, " + user.getName());
//


        return modelAndView;
    }


}
