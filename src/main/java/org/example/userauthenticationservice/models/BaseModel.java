package org.example.userauthenticationservice.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // this value will be stored in tinyint datatype as 0,1,...
    //using below annotation we can store as varchar in table
    @Enumerated(EnumType.STRING)
    private State state;
}
