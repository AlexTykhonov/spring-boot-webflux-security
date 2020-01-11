package com.l2d.tuto.springbootwebfluxsecurity.controller;

import com.l2d.tuto.springbootwebfluxsecurity.user.domain.Moto;
import com.l2d.tuto.springbootwebfluxsecurity.user.domain.User;
import com.l2d.tuto.springbootwebfluxsecurity.user.repository.MotoRepository;
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


    @Autowired
    MotoRepository motoRepository;

    @GetMapping
    public Flux<Moto> getAllMoto(Mono<Principal> principal) {

        Mono<String> username = principal.map(p -> p.getName());
        Mono<User> user = username.flatMap(userRepository::findByLogin);
        Mono<List<Moto>> listMono = user.map(user1 -> user1.getMotos());
        Flux<Moto> motoFlux = listMono.flatMapMany(Flux::fromIterable);
        return motoFlux;
    }

    @GetMapping ("/getall" )
    public Flux<Moto> getAllMotos() {

        return motoRepository.findAll();
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

    @DeleteMapping
    public Mono<Void> delAllMotos () {
        return motoRepository.deleteAll();
    }

    @DeleteMapping ("/user/{userId}" )
    public Mono<Void> delUserMoto (Mono<Principal> principal, @PathVariable String userId) {
        System.out.println(userId);
        Mono<String> username = principal.map(p -> p.getName());
        Mono<User> user = username.flatMap(userRepository::findByLogin);
        user.map(u-> {
            u.deleteAll();
            return u;
       });
        return Mono.empty();
    }


    @PostMapping
    public Mono<User> postMoto (@RequestBody Moto moto, Mono<Principal> principalMono) {
        Mono<String> username = principalMono.map(p -> p.getName());
        Mono<User> user = username.flatMap(userRepository::findByLogin);
        return user.flatMap(user1 -> {
            moto.setUserId(user1.getId());
            Mono<Moto> motoMono = motoRepository.save(moto);
            motoMono.map(m->{
                System.out.println("==============MOTO CONTROLLER===" + m);
                return user1.addMoto(m);

            });
         return userRepository.save(user1);
        });
        }

        @PostMapping ("/addmoto" )
    public Mono<Moto> addMoto (@RequestBody Moto moto, Principal principal) {
            Mono<User> user = userRepository.findByLogin(principal.getName());
            Mono<Moto> mmoto = motoRepository.save(moto);
            user.zipWith(Mono.just(moto)).map(tuple->{
               tuple.getT2().setUserId(tuple.getT1().getId());
                tuple.getT1().addMoto(tuple.getT2());
              return userRepository.save(tuple.getT1());
            });
           return mmoto;
        }
}
