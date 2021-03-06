package com.example.cornerstore.springcornerstoreapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Controller
@RequestMapping
public class PingController {

    class Ping{
        private String test ;
        public Ping(String msg){ this.test = msg; }
        public String getTest() { return this.test; }
    }
    @GetMapping("/")
    public Ping ping() {
        return new Ping( "CornerStore API version 1.0 alive!");
    }

    @GetMapping("/home")
    public String base(){
        return "base";
    }
    @GetMapping("/login")
    public String log(){
        return "login";
    }
    @GetMapping("/account")
    public String account(){
        return "profile";
    }
    @GetMapping("/tester")
    public String tester(){
        return "home";
    }
    @GetMapping("/editprofile")
    public String editor(){
        return "editprofile";
    }
    @GetMapping("/signup")
    public String signup(){
        return "signup";
    }
}
