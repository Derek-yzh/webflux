package com.example.webflux.controller;

import com.example.webflux.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.channels.FileChannel;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @Author: Derek
 * @DateTime: 2021/1/30 18:06
 * @Description: 响应式编程Demo
 */
@RestController
@RequestMapping("person")
public class PersonController {

    @Autowired
    PersonService personService;

    @GetMapping("get")
    public Mono<Object> get(){
        System.out.println("-----start-----");
        System.out.println("controller: "+Thread.currentThread().getName());

        Mono<Object> mono = Mono.create(sink -> {
            System.out.println("do: "+Thread.currentThread().getName());
            sink.success(personService.getPerson());
            //sink.success(personService.waiting());//阻塞
        })
        .doOnSubscribe(sub -> System.out.println("sub: "+sub)) //订阅数据
        .doOnNext(data -> System.out.println("data: "+data)) //得到数据
        .doOnSuccess(success -> System.out.println("sucess")); //整体完成

        System.out.println("-----end-----");
        return mono;
    }

    /**
     * test Mono.just
     * @param name name
     * @param request ReactorServerHttpRequest webFlux 特有
     * @param session session
     * @return string
     */
    @GetMapping("just")
    public Mono<Object> just(@RequestParam String name,
                             ServerHttpRequest request,
                             WebSession session){
        System.out.println("request: " + request);

        System.out.println(request.getQueryParams().get("name"));
        System.out.println("name: " + name);

        if (StringUtils.isEmpty(session.getAttribute("code"))) {
            System.out.println("set something ...");
            session.getAttributes().put("code", 250);
        }
        System.out.println("code: " + session.getAttribute("code"));

        return Mono.just("么么哒，" + name);
    }

    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> sse(String name, String str){
        //1.封装对象
        Flux<String> flux = Flux.fromStream(IntStream.range(1, 10000).mapToObj(i -> {
            try { TimeUnit.MILLISECONDS.sleep(new Random().nextInt(3000));} catch (InterruptedException e) {e.printStackTrace();}
            return "" + name + ":" + str + i + "\n";
        }))
        .doOnSubscribe(sub -> {
            System.out.println(sub);
        })
        .doOnComplete(() -> System.out.println("complete .."))
        .doOnNext(data -> {
            System.out.println("data: " + data);
        });
        //2.对象 连带里面的方法给了容器
        return flux;
    }

    public void test(){



    }

}
