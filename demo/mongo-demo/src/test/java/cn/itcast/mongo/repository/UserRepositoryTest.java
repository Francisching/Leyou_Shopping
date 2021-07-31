package cn.itcast.mongo.repository;

import cn.itcast.mongo.pojo.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testAdd(){
        User user = new User(10010L,"ldh",30);

        User userResult = mongoTemplate.save(user);
        userRepository.save(user);

        System.out.println("userResult = " + userResult);
    }

    @Test
    public void testAddBatch(){

        this.mongoTemplate.insertAll(null);

        this.userRepository.saveAll(null);

    }

    @Test
    public void saveAll() {
        List<User> list = new ArrayList<>();
        list.add(new User(1L, "Amy", 16));
        list.add(new User(2L, "Lucy", 21));
        list.add(new User(3L, "Jack", 20));
        list.add(new User(4L, "Tom", 25));
        list.add(new User(5L, "John", 18));

        userRepository.saveAll(list);
    }

    @Test
    public void testUpdate(){
        // id存在，则修改
        User user = new User(1L, "Amy", 15);

        //saveOrUpdate
        userRepository.save(user);

    }

    @Test
    public void testFindById(){
        Optional<User> result = userRepository.findById(1L);

        result.ifPresent(System.out::println);
    }

    @Test
    public void testFindByPage(){
        // 查询，PageRequest就是分页条件对象，
        Page<User> info = userRepository.findAll(PageRequest.of(0, 3, Sort.Direction.DESC, "age"));
        // 总页数
        int totalPages = info.getTotalPages();
        System.out.println("totalPages = " + totalPages);
        // 总条数
        long totalElements = info.getTotalElements();
        System.out.println("totalElements = " + totalElements);
        // 当前页结果集合
        List<User> list = info.getContent();
        list.forEach(System.out::println);
    }

    @Test
    public void testFindByName(){


        List<User> users =  this.userRepository.findByName("Amy");

        users.forEach(System.out::println);
    }

    @Test
    public void testFindByRange(){

        List<User> users = this.userRepository.findByAgeGreaterThan(15);

        users.forEach(System.out::println);
    }



}