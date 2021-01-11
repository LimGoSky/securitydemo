package com.example.demo.bean;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class Permission {
    @Id
    @GeneratedValue
    private long id;//主键.
    private String url;//授权链
}