package com.mt.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.extra.mail.MailUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mt.common.Result;
import com.mt.controller.UserOrderController;
import com.mt.dto.LoginFormDto;
import com.mt.dto.UserDto;
import com.mt.entity.User;
import com.mt.mapper.UserMapper;
import com.mt.service.UserService;
import com.mt.utils.UserHolder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.mt.utils.RedisConstants.*;
import static com.mt.utils.SystemConstants.USER_NICK_NAME_PREFIX;

/**
 * Author: csy100
 * Date: 2023/7/16
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    
    /**
     * 发送验证码
     *
     * @param loginFormDto
     * @return
     */
    @Override
    public Result sendCode(LoginFormDto loginFormDto) {
        if (StrUtil.isBlank(loginFormDto.getEmail()) || StrUtil.isBlank(loginFormDto.getPassword())) {
            return Result.fail("邮箱和密码不能为空！");
        }
        
        String code = RandomUtil.randomNumbers(6);
        MailUtil.send(loginFormDto.getEmail(), "验证码", "您的验证码为:<h2> " + code + "</h2>", true);
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + loginFormDto.getEmail(), code,
                LOGIN_CODE_TTL, TimeUnit.MINUTES);
        
        return Result.ok();
    }
    
    
    /**
     * 登录功能
     *
     * @param loginFormDto
     * @return
     */
    @Override
    public Result userLogin(LoginFormDto loginFormDto) {
        String cacheCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + loginFormDto.getEmail());
        if (cacheCode == null || !cacheCode.equals(loginFormDto.getCode())) {
            return Result.fail("验证码错误，请稍后重试！");
        }
        
        User user = this.query().eq("email", loginFormDto.getEmail()).one();
        if (user == null) {
            user = createUserWithEmail(loginFormDto);
        }
        
        String loginPassword = DigestUtils.md5DigestAsHex(loginFormDto.getPassword().getBytes());
        if (!user.getPassword().equals(loginPassword)) {
            return Result.fail("密码错误");
        }
        // 生成token
        String token = UUID.randomUUID().toString(true);
        
        UserDto userDto = BeanUtil.copyProperties(user, UserDto.class);
        
        // 将userDTO数据储存到Map集合下,并在赋值的时候将每一个字段的值转为字符串形式
        Map<String, Object> userMap = BeanUtil.beanToMap(userDto, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((filedName, filedValue) -> filedValue.toString()));
        
        stringRedisTemplate.opsForHash().putAll(LOGIN_USER_KEY + token, userMap);
        stringRedisTemplate.expire(LOGIN_USER_KEY + token, LOGIN_USER_TTL, TimeUnit.DAYS);
        
        return Result.ok(token);
    }
    
    /**
     * 退出功能
     * @param token
     * @return
     */
    @Override
    public Result userLogout(String token) {
        if (StrUtil.isBlank(token)) {
            return Result.fail("登录信息已过期！");
        }
        stringRedisTemplate.delete(LOGIN_USER_KEY + token);
        return Result.ok("已安全退出！");
    }
    
    /**
     * 找回密码
     * @param loginFormDto
     * @return
     */
    @Override
    public Result changePassword(LoginFormDto loginFormDto) {
        User user = this.query().eq("email", loginFormDto.getEmail()).one();
        if (StrUtil.isBlank(loginFormDto.getEmail())) {
            return Result.fail("请先填写邮箱和输入新的密码");
        }
        if (user == null) {
            return Result.fail("您未注册该账号，请先进行登录注册");
        }
        
        String code = RandomUtil.randomNumbers(6);
        String content = "您的新密码为:<h2> " + loginFormDto.getPassword() + "</h2>" +
                "您的验证码为:<h2> " + code + "</h2>(60秒内有效)";
        MailUtil.send(loginFormDto.getEmail(), "重置密码", content, true);
        
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + loginFormDto.getEmail(), code,
                LOGIN_CODE_TTL, TimeUnit.MINUTES);
        
        String newPassword = DigestUtils.md5DigestAsHex(loginFormDto.getPassword().getBytes());
        user.setPassword(newPassword);
        this.updateById(user);
        return Result.ok("请注意查收邮箱");
    }
    
    private User createUserWithEmail(LoginFormDto loginFormDto) {
        User user = new User();
        user.setEmail(loginFormDto.getEmail());
        // 生成随机 nickName
        user.setNickName(USER_NICK_NAME_PREFIX + RandomUtil.randomNumbers(10));
        
        // 加密密码
        String password = loginFormDto.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        user.setPassword(password);
        
        // 保存用户到数据库中
        this.save(user);
        return user;
    }

    public Result getCode(String price){
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(0);
        double parsedDouble = Double.parseDouble(price) * 35000;
        String parseDouble = numberFormat.format(parsedDouble);

        // 拿到此时的用户数据
        System.out.println(UserHolder.getUser().getEmail());
        User user = this.query().eq("id", UserHolder.getUser().getEmail()).one();
        String mail = user.getEmail();
        // 设置请求参数
        String name = "4gai";
        String payType = "native";
        String orderId = mail + "=" + System.currentTimeMillis() / 1000;
        // 设置回调函数的地址
        String notifyUrl = "http://" + "" + ":8081/order/getPayRes";
        // 根据接口文档,需要md5加密信息
        String s = name + payType + price + orderId + notifyUrl + "74a1194ce9a14bb2a24b0444d571a81e";
        String sign = MD5.create().digestHex(s);

        // 创建一个WebClient实例
        WebClient client = WebClient.create("https://xorpay.com/api/pay/" + "25154");

        // 创建一个MultiValueMap，用于存放请求参数
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("name", name); // 支付名称，可变，建议使用网站名
        data.add("pay_type", payType); // 支付类型，固定
        data.add("price", price); // 支付金额，必填
        data.add("order_id", orderId); // 订单id--后台生成的id，必填
        data.add("order_uid", mail); // 邮件，必填
        data.add("notify_url", notifyUrl); // 回调地址，必填
        data.add("more", parseDouble); // 备注，选填
        data.add("sign", sign); // 生成的标记号，必填

        // 使用WebClient执行POST请求
        Mono<String> responseMono = client.post()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(data))
                .retrieve()
                .bodyToMono(String.class);
        // TODO 保存未支付的订单
//        Order order = new Order();
//        order.setId(java.util.UUID.randomUUID().toString());
//        order.setUserId(user.getId());
//        order.setBalance(Double.parseDouble(price));
//        order.setBillId(orderId);
//        order.setContent("未支付");
//        order.setCreateTime(new Date());
        try {
            String res = responseMono.block();
            JSONObject jsonObject = JSONUtil.parseObj(res);
            String aoid = jsonObject.getStr("aoid");
//            order.setAoid(aoid);
//            this.save(order);
            String qr = jsonObject.getJSONObject("info").getStr("qr");
            log.info(aoid);
            return Result.ok(qr);
        } catch (Exception error) {
            return Result.fail(error.getMessage());
        }
    }

    public void getResByXorPay(UserOrderController.PayRequest request){
        String aoid = request.aoid();
        String orderOd = request.order_id();
        String payPrice = request.pay_price();
        String payTime = request.pay_time();
        String more = request.more();
        String detail = request.detail();
        String sign = request.sign();

        log.info("aoid = " + aoid);
        log.info("order_id = " + orderOd);
        log.info("pay_price = " + payPrice);
        log.info("pay_time = " + payTime);
        log.info("more = " + more);
        log.info("detail = " + detail);
        log.info("sign = " + sign);
        // TODO 修改 order 表, 修改 user 表
//        Order order = this.query().eq("aoid", aoid).one();
//        order.setContent("支付成功");
//        order.setChannel("微信");
//        order.setPayTime(new Date());
//        this.updateById(order);
//
//        // 修改用户表
//        double balance = order.getBalance();
//        User user = userService.query().eq("id", BaseContext.getCurrent()).one();
//        int token = user.getToken();
//        user.setToken((int) (token + balance * 35000));
//        userService.updateById(user);
    }
}
