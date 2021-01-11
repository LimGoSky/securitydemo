package com.example.demo.bean;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class UserInfo {

    @Id
    @GeneratedValue
    private long uid;//主键.
    private String username;//用户名.
    private String password;//密码.

    //用户－－角色：多对多的关系．
    @ManyToMany(fetch= FetchType.EAGER)//立即从数据库中进行加载数据;
    @JoinTable(name = "UserRole", joinColumns = { @JoinColumn(name = "uid") }, inverseJoinColumns ={@JoinColumn(name = "role_id") })
    private List<Role> roles;
}
