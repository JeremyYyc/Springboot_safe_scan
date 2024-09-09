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
     * @param request 前端传递的请求
     * @param response 返回的的状态响应码
     * @param handler handler
     * @return true 认证成功，放行
     *         false 认证失败，不放行
     * @throws Exception 如果产生异常，则抛出
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //令牌验证
        String token = request.getHeader("Authorization");

        //验证token
        try{
            //从redis中获取相同的token
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            String redisToken = operations.get(token);

            //判断两个token是否相同，即在redis缓存中存在一个与此次登录所被验证的token相同的一个令牌
            if (redisToken == null){
                throw new RuntimeException("请重新登录");
            }
            Map<String, Object> stringObjectMap = JwtUtil.parseToken(token);
            ThreadLocalUtil.set(stringObjectMap);
            //放行
            return true;
        } catch (Exception e) {
            //http的响应状态码为401
            response.setStatus(401);
            //不放行
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //清除TreadLocal中的数据以防止内存泄漏
        ThreadLocalUtil.remove();
    }
}
