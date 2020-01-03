package com.l2d.tuto.springbootwebfluxsecurity.security.controller;

import com.l2d.tuto.springbootwebfluxsecurity.security.ReactiveUserDetailsServiceImpl;
import com.l2d.tuto.springbootwebfluxsecurity.security.dto.LoginVM;
import com.l2d.tuto.springbootwebfluxsecurity.security.jwt.JWTReactiveAuthenticationManager;
import com.l2d.tuto.springbootwebfluxsecurity.security.jwt.JWTToken;
import com.l2d.tuto.springbootwebfluxsecurity.security.jwt.TokenProvider;
import com.l2d.tuto.springbootwebfluxsecurity.user.domain.Authority;
import com.l2d.tuto.springbootwebfluxsecurity.user.domain.User;
import com.l2d.tuto.springbootwebfluxsecurity.user.repository.AuthorityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;


/**
 *
 * @author duc-d
 *
 */
@RestController
@RequestMapping("/authorize")
@Slf4j
public class UserJWTController {
    private final TokenProvider tokenProvider;
    private final JWTReactiveAuthenticationManager authenticationManager;
    private final Validator validation;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ReactiveUserDetailsServiceImpl reactiveUserDetailsService;

    @Autowired
    private AuthorityRepository authorityRepository;

    Set<Authority> authorityHashSet = new HashSet<Authority>();

    public UserJWTController(TokenProvider tokenProvider,
                             JWTReactiveAuthenticationManager authenticationManager,
                             Validator validation) {
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
        this.validation = validation;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Mono<JWTToken> authorize(@Valid @RequestBody LoginVM loginVM) {
        System.out.println("------------------> JWT Controller :"+loginVM);
        //{
        //    "username":"Andrew",
        //    "password":"password"
        //}
        if (!this.validation.validate(loginVM).isEmpty()) {
            return Mono.error(new RuntimeException("Bad request"));
        }

        Authentication authenticationToken =
                new UsernamePasswordAuthenticationToken(loginVM.getUsername(), loginVM.getPassword());
// метод authenticate проверяет есть ли в базе данных пользователь с таким логином и паролем.
        Mono<Authentication> authentication = this.authenticationManager.authenticate(authenticationToken);
//если нет - выбрасывает исключение
        authentication.doOnError(throwable -> {
            throw new BadCredentialsException("Bad credentials");
        });
        //  если есть кладем authenticationToken кладем в Секьюрити контекст холдер который содержит всю информацию о текущих пользователях приложения
        ReactiveSecurityContextHolder.withAuthentication(authenticationToken);
        return authentication.map(auth -> {
            // на основе информации полученной из объекта authenticationToken и создаем токен
            String jwt = tokenProvider.createToken(auth);
            //eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBbmRyZXciLCJhdXRoIjoiUk9MRV9BRE1JTiIsImV4cCI6MTU3NDYyNjEwNH0.REaxA7Bl4IssgjlsT_PbOPhf4EyubpwZkEPt_onTLaeq1E5d3RGPAW9Np-9V5TSq3lV_ffmt4RiFG8X2pS-_YQ
            //  помещаем наш токен в объект jwttoken
           // {
            //    "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBbmRyZXciLCJhdXRoIjoiUk9MRV9BRE1JTiIsImV4cCI6MTU3NDYyNjEwNH0.REaxA7Bl4IssgjlsT_PbOPhf4EyubpwZkEPt_onTLaeq1E5d3RGPAW9Np-9V5TSq3lV_ffmt4RiFG8X2pS-_YQ"
            //}
            return new JWTToken(jwt);
// вот этот токен - временный пропуск
        });
    }

    @PostMapping("/registr")
    public Mono<String> registrator (@Valid @RequestBody User user) {

      //  ArrayList<Authority> authorityArrayList = new ArrayList<>();
       // authorityRepository.findAll().subscribe(authorityArrayList::add);
       // authorityHashSet.add(authorityRepository.findById("ROLE_ADMIN"));

        String password = passwordEncoder.encode(user.getPassword());
        user.setPassword(password);

        Mono<Authority> authority =  authorityRepository.findById("ROLE_ADMIN");
        authority.subscribe(a->authorityHashSet.add(a));

        user.setAuthorities(authorityHashSet);
        reactiveUserDetailsService.createSpringSecurityUser(user);
        return reactiveUserDetailsService.findByUsername(user.getLogin()).flatMap(login->Mono.just(login.getUsername()));
    }

}
