package com.forteach.server.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Auther: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 19-1-9 16:14
 * @Version: 1.0
 * @Description:
 */
@Configuration
@EnableConfigurationProperties(WechatProperties.class)
public class WeChatMiniAppConfig {

    private WechatProperties properties;

    private static String appId;

    private static Map<String, WxMaService> maServices = Maps.newHashMap();

    @Autowired
    public WeChatMiniAppConfig(WechatProperties properties) {
        this.properties = properties;
    }


    public static WxMaService getMaService() {
        WxMaService wxService = maServices.get(appId);
        if (wxService == null) {
            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appId));
        }
        return wxService;
    }

    @Bean
    public Object services() {
        maServices = this.properties.getConfigs()
                .stream()
                .map(this::apply)
                .collect(Collectors.toMap(s -> s.getWxMaConfig().getAppid(), a -> a));

        return Boolean.TRUE;
    }


    private WxMaService apply(WechatProperties.Config a) {
        WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
        config.setAppid(a.getAppid());
        config.setSecret(a.getSecret());
        config.setToken(a.getToken());
        config.setAesKey(a.getAesKey());
        config.setMsgDataFormat(a.getMsgDataFormat());
        //设置 appId
        appId = a.getAppid();
        WxMaService service = new WxMaServiceImpl();
        service.setWxMaConfig(config);
        return service;
    }
}