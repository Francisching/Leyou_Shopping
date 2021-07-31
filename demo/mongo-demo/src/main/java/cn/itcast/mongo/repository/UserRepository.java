package cn.itcast.mongo.repository;

import cn.itcast.mongo.pojo.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<User,Long> {

    //db.users.find({name:"${name}"})
    List<User> findByName(String name);

    List<User> findByAgeGreaterThan(int age);

}
