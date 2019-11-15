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
        Flux<Moto> motoFlux = listMono.flatMapMany(Flux::fromIterable);
        return motoFlux;
    }

    @GetMapping ("/{motoid}" )
    public Flux<Moto> getMotoById(Mono<Principal> principal, @PathVariable String motoid) {
        System.out.println(motoid);
        Mono<String> username = principal.map(p -> p.getName());
        Mono<User> user = username.flatMap(userRepository::findByLogin);
        Mono<List<Moto>> listMono = user.map(user1 -> user1.getMotos());
        Flux<Moto> motoFlux = listMono.flatMapMany(Flux::fromIterable);
        return motoFlux.filter(moto->moto.getId().equals(motoid));
    }


    @DeleteMapping ("/{motoid}" )
    public Mono<List<Moto>> delMoto (Mono<Principal> principal, @PathVariable String motoid) {
        System.out.println(motoid);
        Mono<String> username = principal.map(p -> p.getName());
        Mono<User> user = username.flatMap(userRepository::findByLogin);
        Mono<List<Moto>> listMono = user.map(user1 -> user1.getMotos());
        Flux<Moto> motoFlux = listMono.flatMapMany(Flux::fromIterable);
        Flux<Moto> motoFlux2 = motoFlux.filter(moto->moto.getId().equals(motoid));
        motoFlux2.map(moto -> listMono.map(list-> list.remove(moto)));
              //  .map(motoFlux1->user.map(user1->user1.setMotos(motoFlux1))));
        return listMono;
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
        });
        }
}

// создать андроид приложение которое будет клиентом для этого.
// Комментарий к 49-52