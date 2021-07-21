package cn.itcast.flux.web;

import cn.itcast.flux.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Random;

@RestController
@RequestMapping("/heima")
@Slf4j
public class WebController {

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
                .take(10) // 取前3个元素
                // 将元素转为一个User对象
                .map(i -> User.of("user_" + i, 20 + i.intValue()));
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<User> getUserStream() {
        log.info("stream 开始执行");
        Flux<User> flux = getUserWithFlux();
        log.info("stream 执行完毕");
        return flux;
    }

    @GetMapping("/flux/str")
    public Flux<Integer> printStr() {

        // 定义一个随机生成器
        Random random = new Random();
        // 把集合的构造函数作为初始化方法，形成的初始化变量，会传递给第二个函数作为参数
        Flux<Integer> flux = Flux.generate(ArrayList::new, (list, sink) -> {
            // 生成随机数
            int value = random.nextInt(100);
            // 加入集合
            list.add(value);
            // 加入Flux
            sink.next(value);

            if (list.size() == 10) {
                // 当集合元素数量等于10，则停止,flux在封装弹射元素时，不触碰complete则会一直添加
                sink.complete();
            }
            return list;
        });

        return flux;

    }


    @GetMapping("/flux/num")
    public Flux<Integer> printInteger(){
        Flux<Integer> flux = Flux.create(sink -> {
//            for (int i = 0; i < 10; i++) {
//                // 将循环遍历得到的数字，添加到flux
//                sink.next(i);
//            }

            //向flux中弹射2个元素
            sink.next(10);
            sink.next(50);
            // 循环结束，任务完成
            sink.complete();
        });

        return flux;
    }


}
