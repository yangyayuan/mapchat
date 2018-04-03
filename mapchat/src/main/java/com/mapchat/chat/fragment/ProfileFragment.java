package com.mapchat.chat.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avoscloud.chat.R;
import com.mapchat.chat.activity.LoginActivity;
import com.mapchat.chat.activity.ProfileNotifySettingActivity;
import com.mapchat.chat.model.LeanchatUser;
import com.mapchat.chat.service.PushManager;
import com.mapchat.chat.service.UpdateService;
import com.mapchat.chat.util.PathUtils;
import com.mapchat.chat.util.Utils;
import com.squareup.picasso.Picasso;
import com.yunzhanghu.redpacketui.utils.RPRedPacketUtil;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.leancloud.chatkit.LCChatKit;

/**
 * Created by lzw on 14-9-17.我的页面
 */
public class ProfileFragment extends BaseFragment {
  private static final int IMAGE_PICK_REQUEST = 1;
  private static final int CROP_REQUEST = 2;

  @Bind(R.id.profile_avatar_view)
  ImageView avatarView;//初始化头像布局

  @Bind(R.id.profile_username_view)
  TextView userNameView;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.profile_fragment, container, false);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    headerLayout.showTitle(R.string.profile_title);
  }

  @Override
  public void onResume() {
    super.onResume();
    refresh();
  }

  private void refresh() {
    LeanchatUser curUser = LeanchatUser.getCurrentUser();
    userNameView.setText(curUser.getUsername());
    Picasso.with(getContext()).load(curUser.getAvatarUrl()).into(avatarView);
  }

  @OnClick(R.id.profile_checkupdate_view)
  public void onCheckUpdateClick() {
    UpdateService updateService = UpdateService.getInstance(getActivity());
    updateService.showSureUpdateDialog();
  }

  @OnClick(R.id.profile_notifysetting_view)
  public void onNotifySettingClick() {
    Intent intent = new Intent(ctx, ProfileNotifySettingActivity.class);
    ctx.startActivity(intent);
  }


  @OnClick(R.id.profile_redpacket_view)
  public void onRPClick() {
    RPRedPacketUtil.getInstance().startChangeActivity(getActivity());
  }

  @OnClick(R.id.profile_logout_btn)
  public void onLogoutClick() {
    LCChatKit.getInstance().close(new AVIMClientCallback() {
      @Override
      public void done(AVIMClient avimClient, AVIMException e) {
      }
    });
    PushManager.getInstance().unsubscribeCurrentUserChannel();
    LeanchatUser.logOut();
    getActivity().finish();
    Intent intent = new Intent(ctx, LoginActivity.class);
    ctx.startActivity(intent);
  }

  @OnClick(R.id.profile_avatar_layout)
  public void onAvatarClick() {
    Intent intent = new Intent(Intent.ACTION_PICK, null);
    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
    startActivityForResult(intent, IMAGE_PICK_REQUEST);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == Activity.RESULT_OK) {
      if (requestCode == IMAGE_PICK_REQUEST) {
        Uri uri = data.getData();
        startImageCrop(uri, 200, 200, CROP_REQUEST);
      } else if (requestCode == CROP_REQUEST) {
        final String path = saveCropAvatar(data);
        LeanchatUser user = LeanchatUser.getCurrentUser();
        user.saveAvatar(path,null);
      }
    }
  }

  public Uri startImageCrop(Uri uri, int outputX, int outputY, int requestCode) {//剪裁头像
    Intent intent = null;
    intent = new Intent("com.android.camera.action.CROP");
    intent.setDataAndType(uri, "image/*");
    intent.putExtra("crop", "true");
    intent.putExtra("aspectX", 1);
    intent.putExtra("aspectY", 1);
    intent.putExtra("outputX", outputX);
    intent.putExtra("outputY", outputY);
    intent.putExtra("scale", true);
    String outputPath = PathUtils.getAvatarTmpPath();
    Uri outputUri = Uri.fromFile(new File(outputPath));
    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
    intent.putExtra("return-data", true);
    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
    intent.putExtra("noFaceDetection", false); // face detection
    startActivityForResult(intent, requestCode);
    return outputUri;
  }

  private String saveCropAvatar(Intent data) {//保存剪裁头像
    Bundle extras = data.getExtras();
    String path = null;
    if (extras != null) {
      Bitmap bitmap = extras.getParcelable("data");
      if (bitmap != null) {
        path = PathUtils.getAvatarCropPath();
        Utils.saveBitmap(path, bitmap);
        if (bitmap != null && bitmap.isRecycled() == false) {
          bitmap.recycle();
        }
      }
    }
    return path;
  }

}



























