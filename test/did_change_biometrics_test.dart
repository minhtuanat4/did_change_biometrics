import 'package:did_change_biometrics/status_enum.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:did_change_biometrics/did_change_biometrics.dart';
import 'package:did_change_biometrics/did_change_biometrics_platform_interface.dart';
import 'package:did_change_biometrics/did_change_biometrics_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockDidChangeBiometricsPlatform
    with MockPlatformInterfaceMixin
    implements DidChangeBiometricsPlatform {
  @override
  Future<String?> getPlatformVersion() => Future.value('42');

  @override
  Future<String?> onCheckBiometrics() {
    return Future.value('valid_valid_valid');
  }
}

void main() {
  final DidChangeBiometricsPlatform initialPlatform =
      DidChangeBiometricsPlatform.instance;
  late DidChangeBiometrics didChangeBiometricsPlugin;
  late MockDidChangeBiometricsPlatform fakePlatform;
  setUp(
    () {
      didChangeBiometricsPlugin = DidChangeBiometrics();
      fakePlatform = MockDidChangeBiometricsPlatform();
    },
  );

  test('$MethodChannelDidChangeBiometrics is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelDidChangeBiometrics>());
  });

  test('getPlatformVersion', () async {
    DidChangeBiometricsPlatform.instance = fakePlatform;

    expect(await didChangeBiometricsPlugin.getPlatformVersion(), '42');
  });
  test('getCheckBiometrics', () async {
    DidChangeBiometricsPlatform.instance = fakePlatform;
    expect(await didChangeBiometricsPlugin.onCheckBiometrics(),
        'valid_valid_valid');
  });
}
