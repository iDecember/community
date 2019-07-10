package life.december.community.controller;

import life.december.community.dto.AccessTokenDTO;
import life.december.community.dto.GithubUser;
import life.december.community.mapper.UserMapper;
import life.december.community.model.User;
import life.december.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class AuthorizeController {
    @Autowired
    private GithubProvider githubProvider;

    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.redirect.uri}")
    private  String redirectUri;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/callback")
    public  String callback(@RequestParam(name="code") String code,
                            @RequestParam(name="state") String state,
                            HttpServletResponse response
    ){
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setState(state);
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser = githubProvider.getUser(accessToken);//使用github登陆成功以后
        if (githubUser !=null){
            User user = new User();                //获取用户信息
            String token = UUID.randomUUID().toString();//获取用户信息时生成一个token
            user.setToken(token);//代替sessio
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));//强制转换
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());//把token放到user对象里，以上存储到数据库中
            userMapper.insert(user);
            //写入cookie
            response.addCookie(new Cookie("token",token));//把token放到cookie里

            //request.getSession().setAttribute("user",githubUser);
            return "redirect:/";
            //登录成功，写cookie和session
        }else{
            //登陆失败，重新登录
            return "redirect:/";
        }
    }
}
