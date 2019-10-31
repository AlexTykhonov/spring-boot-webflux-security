package com.l2d.tuto.springbootwebfluxsecurity.controller;

import com.l2d.tuto.springbootwebfluxsecurity.security.ReactiveUserDetailsServiceImpl;
import com.l2d.tuto.springbootwebfluxsecurity.security.SecurityUtils;
import com.l2d.tuto.springbootwebfluxsecurity.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Principal;

/**
 * created by duc-d on 8/5/2018
 */
@RestController
@RequestMapping(value = "/api")
public class DemoController {


    @Autowired
    ReactiveUserDetailsServiceImpl reactiveUserDetailsService;

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public Mono<String> hello(ServerWebExchange serverWebExchange,  @RequestParam String name) {

//        reactiveUserDetailsService.findByUsername(SecurityUtils.getUserFromRequest(serverWebExchange).block())
//                .subscribe(user->);

        return SecurityUtils.getUserFromRequest(serverWebExchange);
    }

    @GetMapping("/")
    public Mono<String> greet(Mono<Principal> principal) {
       // User user = reactiveUserDetailsService.findByUsername(principal.block().getName());
       //  Mono<User> user = principal.map(Principal::getName).flatMap(name->reactiveUserDetailsService.findByUsername(name));
         // полчучить из принципала имя, по имени достаь юзера из базы данных
        return principal.map(Principal::getName).map(name -> String.format("Hello, name = %s", name)); }
}
