import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'did_change_biometrics_platform_interface.dart';

/// An implementation of [DidChangeBiometricsPlatform] that uses method channels.
class MethodChannelDidChangeBiometrics extends DidChangeBiometricsPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('did_change_biometrics');

  @override
  Future<String?> getPlatformVersion() async {
    final version =
        await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<String?> onCheckBiometrics() async {
    final result = await methodChannel.invokeMethod<String>('check');
    return result;
  }

  @override
  Future<void> registerSecretKey() async {
    await methodChannel.invokeMethod('registerSecretKey');
  }
}
