package com.cygnati.social_share_plugin;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareMediaContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import androidx.core.content.FileProvider;

import java.io.File;
import java.util.Locale;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * SocialSharePlugin
 */
public class SocialSharePlugin implements MethodCallHandler {
  private final static String INSTAGRAM_PACKAGE_NAME = "com.instagram.android";
  private final static String WHATSAPP_PACKAGE_NAME = "com.whatsapp";
  private final static String FACEBOOK_PACKAGE_NAME = "com.facebook.katana";
  private final Registrar registrar;

  private SocialSharePlugin(Registrar registrar) {
    this.registrar = registrar;
  }

  /**
   * Plugin registration.
   */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "social_share_plugin");
    channel.setMethodCallHandler(new SocialSharePlugin(registrar));
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    final PackageManager pm = registrar.activeContext().getPackageManager();
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else if (call.method.equals("shareToFeedInstagram")) {
      try {
        pm.getPackageInfo(INSTAGRAM_PACKAGE_NAME, PackageManager.GET_ACTIVITIES);
        instagramShare(call.<String>argument("type"), call.<String>argument("path"));
      } catch (PackageManager.NameNotFoundException e) {
        openPlayStore(INSTAGRAM_PACKAGE_NAME);
      }

      result.success(null);
    } else if (call.method.equals("shareToFeedFacebook")) {
      try {
        pm.getPackageInfo(FACEBOOK_PACKAGE_NAME, PackageManager.GET_ACTIVITIES);
        facebookShare(call.<String>argument("caption"), call.<String>argument("path"));
      } catch (PackageManager.NameNotFoundException e) {
        openPlayStore(FACEBOOK_PACKAGE_NAME);
      }

      result.success(null);
    } else if (call.method.equals("shareToWhatsapp")) {
      try {
        pm.getPackageInfo(WHATSAPP_PACKAGE_NAME, PackageManager.GET_ACTIVITIES);
        whatsappShare(call.<String>argument("caption"), call.<String>argument("path"));
      } catch (PackageManager.NameNotFoundException e) {
        openPlayStore(WHATSAPP_PACKAGE_NAME);
      }

      result.success(null);
    } else if (call.method.equals("share")) {
      try {
        share(call.<String>argument("caption"), call.<String>argument("path"));
      } catch (PackageManager.NameNotFoundException e) {
      }

      result.success(null);
    } else {
      result.notImplemented();
    }
  }

  private void openPlayStore(String packageName) {
    final Context context = registrar.activeContext();
    try {
      final Uri playStoreUri = Uri.parse("market://details?id=" + packageName);
      final Intent intent = new Intent(Intent.ACTION_VIEW, playStoreUri);
      context.startActivity(intent);
    } catch (ActivityNotFoundException e) {
      final Uri playStoreUri = Uri.parse("https://play.google.com/store/apps/details?id=" + packageName);
      final Intent intent = new Intent(Intent.ACTION_VIEW, playStoreUri);
      context.startActivity(intent);
    }
  }

  public String getFileProviderAuthority(Context context) {
    return String.format(Locale.ENGLISH, "%s.fileprovider", context.getPackageName());
  }

  private void instagramShare(String type, String imagePath) {
    final Context context = registrar.activeContext();
    final File image = new File(imagePath);
    Uri fileUri = FileProvider.getUriForFile(context, getFileProviderAuthority(context), image);
    final Intent share = new Intent(Intent.ACTION_SEND);
    share.setType(type);
    share.putExtra(Intent.EXTRA_STREAM, fileUri);
    share.setPackage(INSTAGRAM_PACKAGE_NAME);
    context.startActivity(Intent.createChooser(share, "Share to"));
  }

  private void facebookShare(String caption, String mediaPath) {
    final Context context = registrar.activeContext();
    final File media = new File(mediaPath);
    Uri fileUri = FileProvider.getUriForFile(context, getFileProviderAuthority(context), media);
    final SharePhoto photo = new SharePhoto.Builder().setImageUrl(fileUri).setCaption(caption).build();
    final SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();
    final ShareDialog shareDialog = new ShareDialog(registrar.activity());
    shareDialog.show(content);
  }

  private void whatsappShare(String text, String imagePath) {
    final Context context = registrar.activeContext();
    final File image = new File(imagePath);
    Uri fileUri = FileProvider.getUriForFile(context, getFileProviderAuthority(context), image);
    final Intent share = new Intent(Intent.ACTION_SEND);
    share.setType("image/*");
    share.putExtra(Intent.EXTRA_STREAM, fileUri);
    share.putExtra(Intent.EXTRA_TEXT, text);
    share.setPackage(WHATSAPP_PACKAGE_NAME);
    share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    context.startActivity(Intent.createChooser(share, "Share to"));
  }

  private void share(String text, String imagePath) {
    final Context context = registrar.activeContext();
    final File image = new File(imagePath);
    Uri fileUri = FileProvider.getUriForFile(context, getFileProviderAuthority(context), image);
    final Intent share = new Intent(Intent.ACTION_SEND);
    share.setType("image/*");
    share.putExtra(Intent.EXTRA_STREAM, fileUri);
    share.putExtra(Intent.EXTRA_TEXT, text);
    share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    context.startActivity(Intent.createChooser(share, "Share to"));
  }

}
