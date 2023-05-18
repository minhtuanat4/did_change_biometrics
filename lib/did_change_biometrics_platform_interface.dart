import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'did_change_biometrics_method_channel.dart';
import 'status_enum.dart';

abstract class DidChangeBiometricsPlatform extends PlatformInterface {
  /// Constructs a DidChangeBiometricsPlatform.
  DidChangeBiometricsPlatform() : super(token: _token);

  static final Object _token = Object();

  static DidChangeBiometricsPlatform _instance =
      MethodChannelDidChangeBiometrics();

  /// The default instance of [DidChangeBiometricsPlatform] to use.
  ///
  /// Defaults to [MethodChannelDidChangeBiometrics].
  static DidChangeBiometricsPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [DidChangeBiometricsPlatform] when
  /// they register themselves.
  static set instance(DidChangeBiometricsPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<String?> onCheckBiometrics() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
