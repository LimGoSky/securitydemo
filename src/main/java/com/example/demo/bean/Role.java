package com.example.demo.bean;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class Role {
    @Id
    @GeneratedValue
    private long rid;//主键.
    private String name;//角色名称.
    private String description;//角色描述.

    // 角色 - 权限是多对多的关系
    @ManyToMany(fetch= FetchType.EAGER)
    @JoinTable(name="RolePermission",joinColumns= {@JoinColumn(name="role_id")} , inverseJoinColumns= {@JoinColumn(name="permission_id")})
    private List<Permission> permissions;
}
