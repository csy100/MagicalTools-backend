package com.mt.service.impl;

import cn.hutool.crypto.digest.MD5;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mt.common.Result;
import com.mt.entity.PayRequest;
import com.mt.entity.UserOrder;
import com.mt.mapper.UserOrderMapper;
import com.mt.service.UserOrderService;
import com.mt.service.UserService;
import com.mt.utils.UserHolder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.text.NumberFormat;

/**
 * Author: csy100
 * Date: 2023/7/26
 */
@Slf4j
@Service
public class UserOrderServiceImpl extends ServiceImpl<UserOrderMapper, UserOrder> implements UserOrderService {

    @Resource
    public UserService userService;

    public Result getCode(String price) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(0);
        double parsedDouble = Double.parseDouble(price) * 35000;
        String parseDouble = numberFormat.format(parsedDouble);

        // 拿到此时的用户数据
        String mail = UserHolder.getUser().getEmail();
        // 设置请求参数
        String name = "4gai";
        String payType = "native";
        String orderId = mail + "=" + System.currentTimeMillis() / 1000;
        // 设置回调函数的地址
        String notifyUrl = "http://38.47.121.199:9999/order/getPayRes";
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
        Mono<String> responseMono = client.post().contentType(MediaType.APPLICATION_FORM_URLENCODED).body(BodyInserters.fromFormData(data)).retrieve().bodyToMono(String.class);
        try {
            String res = responseMono.block();
            JSONObject jsonObject = JSONUtil.parseObj(res);
            String qr = jsonObject.getJSONObject("info").getStr("qr");
            return Result.ok(qr);
        } catch (Exception error) {
            return Result.fail(error.getMessage());
        }
    }

    public ResponseEntity<String> getResByXorPay(PayRequest request) {
        String aoid = request.getAoid();
        String order_id = request.getOrder_id();
        String pay_price = request.getPay_price();
        String pay_time = request.getPay_time();
        String more = request.getMore();
        String detail = request.getDetail();
        String sign = request.getSign();

        System.out.println("aoid = " + aoid);
        System.out.println("order_id = " + order_id);
        System.out.println("pay_price = " + pay_price);
        System.out.println("pay_time = " + pay_time);
        System.out.println("more = " + more);
        System.out.println("detail = " + detail);
        System.out.println("sign = " + sign);
        double payPrice = Double.parseDouble(pay_price); // 实际支付的rmb
        Long addTimes = (long) (payPrice + 0.5) * 35000; // 支付后新增的金币数量

        String[] split = order_id.split("=");
        String mail = split[0];
        QueryWrapper<UserOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", mail);
        UserOrder userOrder = this.getOne(queryWrapper);
        if (userOrder == null) {
            UserOrder userOrderTemp = new UserOrder();
            userOrderTemp.setEmail(UserHolder.getUser().getEmail());
            userOrderTemp.setPrice(0.0);
            userOrderTemp.setToken(0L);
            this.save(userOrderTemp);
            userOrder = userOrderTemp;
        }
        // 拿到原先的数据
        Double price = userOrder.getPrice();
        Long token = userOrder.getToken();
        // 修改充值后的数据
        userOrder.setPrice(price + payPrice);
        userOrder.setToken(token + addTimes);
        boolean update = this.updateById(userOrder);
        if (update) {
            // 返回HTTP 200表示通知成功
            return ResponseEntity.ok("ok");
        }
        return ResponseEntity.badRequest().build();
    }
}
