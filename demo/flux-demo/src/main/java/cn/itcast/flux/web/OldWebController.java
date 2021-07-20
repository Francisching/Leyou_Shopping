package cn.itcast.flux.web;

import cn.itcast.flux.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/heima")
@Slf4j
public class OldWebController {

    @GetMapping("/hello")
    public String sayHello(@RequestParam("info") String info) {

        return "hello heima129:" + info;
    }

    @GetMapping("/sync")
    public User getUserSync() {
        log.info("sync 开始执行");
        User user = getUser();
        log.info("sync 执行完毕");
        return user;
    }

    @GetMapping("/async")
    public Mono<User> getUserAsync() {
        log.info("sync 开始执行");
        //mono封装，
        Mono<User> mono = Mono.fromSupplier(this::getUser);
        log.info("sync 执行完毕");

        return mono;

    }


    // 生成user的方法
    private User getUser() {
        try {
            // 模拟业务耗时1秒
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return User.of("Rose", 18);
    }


    @GetMapping("/flux")
    public Flux<User> getUserFlux() {
        log.info("flux 开始执行");
        Flux<User> flux = getUserWithFlux();
        log.info("flux 执行完毕");
        return flux;
    }

    private Flux<User> getUserWithFlux() {
        return Flux.interval(Duration.ofSeconds(1))// 每隔1秒发射一个元素
                .take(3) // 取前3个元素
                // 将元素转为一个User对象
                .map(i -> User.of("user_" + i, 20 + i.intValue()));
    }

}
