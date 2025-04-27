package de.personal.taskmanager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.AntPathMatcher;

@ExtendWith(MockitoExtension.class)
public class RandomTests {
    @Test
    void testAntPathMatcher() {
        AntPathMatcher matcher = new AntPathMatcher();

        System.out.println(matcher.match("/api/**", "/api/task"));         // true
        System.out.println(matcher.match("/api/**", "/api/task/details/1"));         // true
        System.out.println(matcher.match("/api/user/", "/api"));         // false
        System.out.println(matcher.matchStart("/api/user/", "/api"));    // true
        System.out.println(matcher.matchStart("/api/user/", "/api/user/details/"));    // false
    }

    @Test
    void testIntegerToString() {
        Object a = Integer.valueOf(1);
        System.out.println(a.toString());
    }
}
