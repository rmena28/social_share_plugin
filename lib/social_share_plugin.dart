import 'dart:async';

import 'package:flutter/services.dart';

class SocialSharePlugin {
  static const MethodChannel _channel =
      const MethodChannel('social_share_plugin');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<void> shareToFeedInstagram(String type, String path) async {
    await _channel.invokeMethod('shareToFeedInstagram', <String, dynamic>{
      'type': type,
      'path': path,
    });
    return Future.value();
  }

  static Future<void> shareToFeedFacebook(String caption, String path) async {
    await _channel.invokeMethod('shareToFeedFacebook', <String, dynamic>{
      'caption': caption,
      'path': path,
    });
    return Future.value();
  }

  static Future<void> shareToWhatsapp(String caption, String path) async {
    await _channel.invokeMethod('shareToWhatsapp', <String, dynamic>{
      'caption': caption,
      'path': path,
    });
    return Future.value();
  }

  static Future<void> share(String caption, String path) async {
    await _channel.invokeMethod('share', <String, dynamic>{
      'caption': caption,
      'path': path,
    });
    return Future.value();
  }

  static Future<void> shareText(String caption) async {
    await _channel.invokeMethod('shareText', <String, dynamic>{
      'caption': caption,
    });
    return Future.value();
  }
}
