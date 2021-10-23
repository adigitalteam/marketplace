package com.example.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@Table(name = "languages")
public class Language {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;
    @Column(name = "locale")
    String locale;
    @Column(name = "messagekey")
    String key;
    @Column(name = "messagecontent")
    String content;
}