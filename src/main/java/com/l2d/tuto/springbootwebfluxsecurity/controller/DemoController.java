package com.l2d.tuto.springbootwebfluxsecurity.controller;

import com.l2d.tuto.springbootwebfluxsecurity.security.ReactiveUserDetailsServiceImpl;
import com.l2d.tuto.springbootwebfluxsecurity.security.SecurityUtils;
import com.l2d.tuto.springbootwebfluxsecurity.user.domain.Moto;
import com.l2d.tuto.springbootwebfluxsecurity.user.domain.User;
import com.l2d.tuto.springbootwebfluxsecurity.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.file.attribute.UserPrincipal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * created by duc-d on 8/5/2018
 */
@RestController
@RequestMapping(value = "/api")
public class DemoController {

@Autowired
UserRepository userRepository;

    @Autowired
    ReactiveUserDetailsServiceImpl reactiveUserDetailsService;

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public Mono<String> hello(ServerWebExchange serverWebExchange,  @RequestParam String name) {

//        reactiveUserDetailsService.findByUsername(SecurityUtils.getUserFromRequest(serverWebExchange).block())
//                .subscribe(user->);

        return SecurityUtils.getUserFromRequest(serverWebExchange);
    }

    @GetMapping
    public List<Moto> greet(Mono<Principal> principal) {
     //  Mono<UserDetails> userDetailsMono = reactiveUserDetailsService.findByUsername(principal.block().getName());
       //  Mono<User> user = principal.map(Principal::getName).flatMap(name->reactiveUserDetailsService.findByUsername(name));
         // полчучить из принципала имя, по имени достаь юзера из базы данных
       // System.out.println("userDetailsMono -------->>>" +userDetailsMono);

//        Mono<SecurityContext> context = ReactiveSecurityContextHolder.getContext();
//
//       Optional<User> user = ReactiveSecurityContextHolder.getContext()
//                .map(SecurityContext::getAuthentication)
//                .map(Authentication::getPrincipal)
//                .map(User::currentUser);

       // User user = (User)userDetailsMono.map(name->name);

        Mono<User> user = principal.map(Principal::getName).flatMap(userRepository::findByLogin);
        List<Moto> moto = new ArrayList<>();
                user.map(user1 -> user1.getMotos()).subscribe(list->moto.addAll(list));
       // return principal.map(Principal::getName).map(name -> String.format("Hello, name = %s", name)); }

        return moto;
    }


}
