package com.example.webflux.service;

import com.example.webflux.entity.Person;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Derek
 * @DateTime: 2021/1/30 18:11
 * @Description: TODO
 */
@Service
public class PersonService {

    static ConcurrentHashMap<Integer,Person> map = new ConcurrentHashMap<>();

    static {
        for (int i = 0; i < 100; i++) {
            Person person = new Person();
            person.setId(i);
            person.setName("person"+i);
            map.put(i,person);
        }
    }

    public Person getPerson(){
        System.out.println("service: "+Thread.currentThread().getName());
        try {TimeUnit.MILLISECONDS.sleep(3000);} catch (InterruptedException e) {e.printStackTrace();}
        return map.get(1);
    }

    public Person waiting(){
        try {TimeUnit.MILLISECONDS.sleep(Integer.MAX_VALUE);} catch (InterruptedException e) {e.printStackTrace();}
        return map.get(1);
    }

    public Flux<Person> getPersons() {
        return Flux.fromIterable(map.values());
    }

}
