package org.safescan.interceptors;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.safescan.utils.JwtUtil;
import org.safescan.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * @param request Request from front-end
     * @param response Responsive code for response
     * @param handler handler
     * @return true Successfully validation, give pass permission
     *         false Failed validation, do not give pass permission
     * @throws Exception Throw the exception if there exists
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        // Obtain the token
        String token = request.getHeader("Authorization");

        // Validate teh token
        try{
            // Obtain the same token from the redis
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            String redisToken = operations.get(token);

            // Estimate weather these two tokens are equal, which means there is a same token in redis
            if (redisToken == null){
                throw new RuntimeException("Token is " + token);
            }

            // Store token from the request header into a new ThreadLocal
            Map<String, Object> stringObjectMap = JwtUtil.parseToken(token);
            ThreadLocalUtil.set(stringObjectMap);

            // Give pass permission
            return true;
        } catch (Exception e) {
            // http responsive code is 401
            response.setStatus(401);
            System.out.println(token);
            // Do not give pass permission
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) throws Exception {
        // Clear data in TreadLocal to prevent memory leaks
        ThreadLocalUtil.remove();
    }
}
