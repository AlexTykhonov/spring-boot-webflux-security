package com.l2d.tuto.springbootwebfluxsecurity.controller;

import com.l2d.tuto.springbootwebfluxsecurity.user.domain.Moto;
import com.l2d.tuto.springbootwebfluxsecurity.user.domain.User;
import com.l2d.tuto.springbootwebfluxsecurity.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/moto")

public class MotoController {

    @Autowired
    UserRepository userRepository;

    @GetMapping
    public Flux<Moto> getAllMoto(Mono<Principal> principal) {

        Mono<String> username = principal.map(p -> p.getName());
        Mono<User> user = username.flatMap(userRepository::findByLogin);
        Mono<List<Moto>> listMono = user.map(user1 -> user1.getMotos());
        return listMono.flatMapMany(Flux::fromIterable);
    }


    @PostMapping
    public Mono<User> postMoto (@RequestBody Moto moto, Mono<Principal> principalMono) {
        Principal principal;
        Mono<String> username = principalMono.map(p -> p.getName());
        Mono<User> user = username.flatMap(userRepository::findByLogin);
        return user.flatMap(user1 -> {
          List<Moto> motoList = new ArrayList<>();

          if (user1.getMotos()!=null){
                 motoList.addAll(user1.getMotos());}

          motoList.add(moto);
          user1.setMotos(motoList);
         return userRepository.save(user1);
        //  return user1;
        });
        }




}
