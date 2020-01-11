package com.l2d.tuto.springbootwebfluxsecurity.user.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@NoArgsConstructor
@AllArgsConstructor
public class Moto {

    @Id
private String id;
private String userId;
private String model;
private Long volume;
}
