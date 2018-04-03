package com.mapchat.chat;

import android.app.Application;
import android.os.StrictMode;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.mapchat.chat.friends.AddRequest;
import com.mapchat.chat.model.LCIMRedPacketMessage;
import com.mapchat.chat.model.LCIMRedPacketAckMessage;
import com.mapchat.chat.model.LCIMTransferMessage;
import com.mapchat.chat.model.LeanchatUser;
import com.mapchat.chat.model.UpdateInfo;
import com.mapchat.chat.redpacket.GetSignInfoCallback;
import com.mapchat.chat.redpacket.RedPacketUtils;
import com.mapchat.chat.service.PushManager;
import com.mapchat.chat.util.LeanchatUserProvider;
import com.mapchat.chat.util.Utils;
import com.yunzhanghu.redpacketsdk.RPInitRedPacketCallback;
import com.yunzhanghu.redpacketsdk.RPValueCallback;
import com.yunzhanghu.redpacketsdk.RedPacket;
import com.yunzhanghu.redpacketsdk.bean.RedPacketInfo;
import com.yunzhanghu.redpacketsdk.bean.TokenData;
import com.yunzhanghu.redpacketsdk.constant.RPConstant;

import cn.leancloud.chatkit.LCChatKit;

/**
 * Created by lzw on 14-5-29.
 */
public class App extends Application {
  public static boolean debug = true;
  public static App ctx;

  @Override
  public void onCreate() {
    super.onCreate();
    ctx = this;
    Utils.fixAsyncTaskBug();

    String appId = "HEfa5v6q8FKSWUh6RC9xhrFs-gzGzoHsz";
    String appKey = "2Yre8PTeMwcxzxUGmkEYuSGR";

    LeanchatUser.alwaysUseSubUserClass(LeanchatUser.class);

    AVObject.registerSubclass(AddRequest.class);
    AVObject.registerSubclass(UpdateInfo.class);

    // 节省流量
    AVOSCloud.setLastModifyEnabled(true);

    AVIMMessageManager.registerAVIMMessageType(LCIMRedPacketMessage.class);
    AVIMMessageManager.registerAVIMMessageType(LCIMRedPacketAckMessage.class);
    AVIMMessageManager.registerAVIMMessageType(LCIMTransferMessage.class);
    LCChatKit.getInstance().setProfileProvider(new LeanchatUserProvider());
    LCChatKit.getInstance().init(this, appId, appKey);

    //保存当前登陆用户信息
    AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
      public void done(AVException e) {
        if (e == null) {
          // 保存成功
          String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
          System.out.println("---  " + installationId);
        } else {
          // 保存失败，输出错误信息
          System.out.println("failed to save installation.");
        }
      }
    });

    // 初始化红包操作
    RedPacket.getInstance().initRedPacket(ctx, RPConstant.AUTH_METHOD_SIGN, new RPInitRedPacketCallback() {
      @Override
      public void initTokenData(final RPValueCallback<TokenData> rpValueCallback) {
        RedPacketUtils.getInstance().getRedPacketSign(ctx, new GetSignInfoCallback() {
          @Override
          public void signInfoSuccess(TokenData tokenData) {
            rpValueCallback.onSuccess(tokenData);
          }

          @Override
          public void signInfoError(String errorMsg) {
          }
        });
      }

      @Override
      public RedPacketInfo initCurrentUserSync() {
        //这里需要同步设置当前用户id、昵称和头像url
        RedPacketInfo redPacketInfo = new RedPacketInfo();
        redPacketInfo.fromUserId = LeanchatUser.getCurrentUserId();
        redPacketInfo.fromAvatarUrl = LeanchatUser.getCurrentUser().getAvatarUrl();
        redPacketInfo.fromNickName = LeanchatUser.getCurrentUser().getUsername();
        return redPacketInfo;
      }
    });
    //控制红包SDK中Log输出
    RedPacket.getInstance().setDebugMode(false);

    PushManager.getInstance().init(ctx);
    //AVOSCloud.initialize(this,"OLoj899IwHYi787ClrImlr3k-gzGzoHsz", "gkz35mRTqTE2aqwp7dEr5uEE");
    AVOSCloud.setDebugLogEnabled(debug);
    AVAnalytics.enableCrashReport(this, !debug);
    if (App.debug) {
      openStrictMode();
    }
  }

  public void openStrictMode() {
    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
            .detectDiskReads()
            .detectDiskWrites()
            .detectNetwork()   // or .detectAll() for all detectable problems
            .penaltyLog()
            .build());
    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
            .detectLeakedSqlLiteObjects()
            .detectLeakedClosableObjects()
            .penaltyLog()
            //.penaltyDeath()
            .build());
  }



}
