package cn.itcast.mbp.service.impl;

import cn.itcast.mbp.mapper.UserMapper;
import cn.itcast.mbp.pojo.User;
import cn.itcast.mbp.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {

}
