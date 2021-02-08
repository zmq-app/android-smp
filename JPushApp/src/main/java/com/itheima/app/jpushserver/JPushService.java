package com.itheima.app.jpushserver;

import java.util.Timer;
import java.util.TimerTask;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.PushPayload;

/**
 * @Subject JPushServer发送通知消息
 * @Author  zhangming
 * @Date    2020-09-03 17:32
 */
public class JPushService {
    //集成Demo参数
    private static final String masterSecret = "811e0b558f8d65d1dcb02c0f";
    private static final String appKey = "e319b0124305411fc3087bb9";

    //功能需求参数
    private static int nIndexNum = 0;
    private static int nMaxCount = 1;

    public static void sendMessage(final JPushClient jpushClient, int nIntervalMs){
        final Timer timer = new Timer();
        final TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (nIndexNum < nMaxCount) {
                    long sendTime = System.currentTimeMillis();
                    String msg = "nIndexNum:"+nIndexNum+"\n"+"sendTimeMs:"+sendTime;
                    PushPayload payload = JPushUtils.buildPushObject_all_all_alert(msg);
                    PushResult result = null;
                    try {
                        result = jpushClient.sendPush(payload);
                    } catch (APIConnectionException e) {
                        e.printStackTrace();
                    } catch (APIRequestException e) {
                        e.printStackTrace();
                    }
                    if (result != null) {
                        System.out.println("nIndexNum: "+nIndexNum+" sendTimeMs: "+sendTime+" responseCode: "+result.getResponseCode());
                    }
                    nIndexNum++;
                }else if(nIndexNum == nMaxCount){
                    timer.cancel();
                }
            }
        };
        timer.schedule(task, 0, nIntervalMs);
    }

    public static void main(String[] args) {
        JPushClient jpushClient = new JPushClient(masterSecret, appKey, null, ClientConfig.getInstance());
        sendMessage(jpushClient,10*1000);
    }
}
