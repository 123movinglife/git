package com.example.market.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.market.dto.LoginDTO;
import com.example.market.dto.RegisterDTO;
import com.example.market.entity.User;
import com.example.market.mapper.UserMapper;
import com.example.market.util.JwtUtil;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final JwtUtil jwtutil;
    public UserService(UserMapper userMapper, JwtUtil jwtutil){
        this.userMapper = userMapper;
        this.jwtutil = jwtutil;
    }
    public void register(RegisterDTO dto){
        //检查用户名是否已存在
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername,dto.getUsername()));
        if(count > 0){
            throw new RuntimeException("用户名已被占用");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        //关键:密码加密后再存,绝不存明文
        user.setPassword(BCrypt.hashpw(dto.getPassword(),BCrypt.gensalt()));
        user.setNickname(dto.getNickname());
        user.setPhone(dto.getPhone());
        userMapper.insert(user);
    }
    public String login(LoginDTO dto){
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername,dto.getUsername()));
        if(user == null || !BCrypt.checkpw(dto.getPassword(),user.getPassword())){
            throw new RuntimeException("用户名或密码错误");
        }
        return jwtutil.createToken(user.getId());//返回token
    }
    public User getById(Long id ){
        User user = userMapper.selectById(id);
        if(user != null) user.setPassword(null); //b别把密码返回前端
        return user;
    }
}
