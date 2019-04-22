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

        // user object added to displaying page
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


    @GetMapping(value = {"/", "/welcome"})
    public String welcome() {

        User user = getAuth();

        if (user != null) {
            return "redirect:/user/home";
        }

        return "redirect:/login";
    }


    private User getAuth(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userService.findUserByEmail(authentication.getName());
    }


}
