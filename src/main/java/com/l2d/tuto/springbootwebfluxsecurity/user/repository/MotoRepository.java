package com.l2d.tuto.springbootwebfluxsecurity.user.repository;

import com.l2d.tuto.springbootwebfluxsecurity.user.domain.Moto;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface MotoRepository extends ReactiveMongoRepository <Moto, String> {
}
