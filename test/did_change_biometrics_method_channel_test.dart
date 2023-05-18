import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:did_change_biometrics/did_change_biometrics_method_channel.dart';

void main() {
  MethodChannelDidChangeBiometrics platform = MethodChannelDidChangeBiometrics();
  const MethodChannel channel = MethodChannel('did_change_biometrics');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
