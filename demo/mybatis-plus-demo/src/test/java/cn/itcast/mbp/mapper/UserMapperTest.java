package cn.itcast.mbp.mapper;


import cn.itcast.mbp.pojo.User;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testSelect() {
        System.out.println(("----- selectAll method test ------"));
        // 使用BaseMapper提供的selectList方法
        List<User> userList = userMapper.selectList(null);
        Assert.assertEquals(5, userList.size());
        userList.forEach(System.out::println);
    }

    @Test
    public void testAdd(){
        User user = new User();
        user.setId(7L);
        user.setUserName("魏泽lin");
        user.setAge(18);
        user.setEmail("zelin@qq.com");

        int insert = this.userMapper.insert(user);

        Assert.assertEquals(1,insert);
    }

    @Test
    public void testUpdate(){
        User user = new User();
        user.setEmail("weize@heima.com");

        //update user set user_name = '魏泽',age = null,email=null where id = 7
        //update user set user_name = '魏泽' where id = 7
       // int count = this.userMapper.updateById(user);

        //update user set email = 'weize@heima.com' where user_name = '魏泽lin'
        this.userMapper.update(user,new QueryWrapper<User>()
                .eq("user_name","魏泽lin"));
    }

    @Test
    public void testQueryOne(){
//        User user = this.userMapper.selectById(3L);
//        System.out.println("user = " + user);

        User user = this.userMapper.selectOne(new QueryWrapper<User>().eq("user_name", "Tom"));

        System.out.println("user = " + user);
    }

    @Test
    public void testList(){

        List<User> users = this.userMapper.selectList(new QueryWrapper<User>().like("email", "com"));

        users.forEach(System.out::println);
    }
}