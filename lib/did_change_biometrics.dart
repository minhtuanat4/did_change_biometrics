import 'did_change_biometrics_platform_interface.dart';
import 'status_enum.dart';

class DidChangeBiometrics {
  Future<String?> getPlatformVersion() {
    return DidChangeBiometricsPlatform.instance.getPlatformVersion();
  }

  Future<String?> onCheckBiometrics() {
    return DidChangeBiometricsPlatform.instance.onCheckBiometrics();
  }
}
