package life.december.community.controller;

import life.december.community.mapper.UserMapper;
import life.december.community.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController {
//  把cookie中key为token的信息 获取到 去数据库中查询是否存在 来验证是否登录成功
    @Autowired
    private UserMapper userMapper;//usermapper里才能访问数据库的user

    @GetMapping("/")
    public  String index(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {         //遍历cookie 找到key为token的这条记录
            if(cookie.getName().equals("token")) {
                String token = cookie.getValue();
                User user=userMapper.findByToken(token); //去数据库中查，如果token相等就去数据库中查
                if (user !=null){
                     request.getSession().setAttribute("user",user);    //如果有就把user放到session里
                }
                break;
            }
        }

        return "index";
    }
}
