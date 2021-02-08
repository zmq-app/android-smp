package com.itheima.app.jpushserver;

import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.audience.AudienceTarget;
import cn.jpush.api.push.model.notification.Notification;

public class JPushUtils {
    /**
     * 快捷地构建推送对象：所有平台,所有设备的通知
     */
    public static PushPayload buildPushObject_all_all_alert(String message) {
        return PushPayload.alertAll(message);
    }

    /**
     * 构建推送对象: 所有平台,推送目标是别名为 "alias1"的通知
     */
    public static PushPayload buildPushObject_all_alias_alert(String alias, String message) {
        return PushPayload.newBuilder().setPlatform(Platform.all())
                .setAudience(Audience.alias(alias))
                .setNotification(Notification.alert(message)).build();
    }

    /**
     * 构建推送对象：平台是 iOS，推送目标是 "tag1", "tag_all" 的交集 推送内容同时包括通知与消息 - 通知信息是
     * ALERT，角标数字为 5，通知声音为 "happy"，并且附加字段 from = "JPush"； 消息内容是 MSG_CONTENT。通知是
     * APNs 推送通道的 消息是 JPush 应用内消息通道的。APNs 的推送环境是“生产”（如果不显式设置的话，Library 会默认指定为开发）
     */
    public static PushPayload buildPushObject_android_tag_alertWithTitle() {
        return PushPayload.newBuilder().setPlatform(Platform.android())
                .setAudience(Audience.tag("tag1"))
                .setNotification(Notification.android("ALERT", "TITLE", null))
                .build();
    }

    /**
     * 构建推送对象：平台是 Andorid 与 iOS，推送目标是 （"tag1" 与 "tag2" 的并集）且（"alias1" 与 "alias2"
     * 的并集） 推送内容是 - 内容为 MSG_CONTENT 的消息，并且附加字段 from = JPush。
     */
    public static PushPayload buildPushObject_ios_audienceMore_messageWithExtras() {
        return PushPayload
                .newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.newBuilder()
                        .addAudienceTarget(AudienceTarget.tag("tag1", "tag2"))
                        .addAudienceTarget(AudienceTarget.alias("alias1", "alias2"))
                        .build())
                .setMessage(Message.newBuilder().setMsgContent("MSG_CONTENT")
                        .addExtra("from", "JPush").build()).build();
    }
}
