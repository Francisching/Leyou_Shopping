package cn.itcast.mbp.service;

import cn.itcast.mbp.pojo.User;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void testAdd(){

        User user = new User();
        user.setId(8L);
        user.setUserName("刘德华");
        user.setAge(28);
        user.setEmail("dehua@qq.com");

        this.userService.save(user);
    }

    @Test
    public void testDynamicParamQuery(){

        String userName = null;
        Integer age = 20;
        String email = null;

        //链式查询,动态sql
        List<User> users = this.userService
                .query()
                .eq(!StringUtils.isEmpty(userName),"user_name", userName)
                .gt(age!=null,"age", age)
                .like(!StringUtils.isEmpty(email),"email", email)
                .list();

        users.forEach(System.out::println);
    }

    @Test
    public void testPageQuery(){
        IPage<User> iPage = new Page<>(1,3);

        Integer age = null;

        this.userService.page(iPage,new QueryWrapper<User>().gt(age!=null,"age",age));

        long pages = iPage.getPages();
        List<User> records = iPage.getRecords();
        long total = iPage.getTotal();

        System.out.println("总页数:"+pages+" 总共有元素："+total);
        System.out.println("当前页数据：");
        records.forEach(System.out::println);
    }

}