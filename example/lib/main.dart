import 'dart:async';

import 'package:did_change_biometrics/did_change_biometrics.dart';
import 'package:flutter/material.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final _didChangeBiometricsPlugin = DidChangeBiometrics();
  late String platformVersion = 'Unknown';

  @override
  void dispose() {
    super.dispose();
  }

  @override
  void initState() {
    super.initState();

    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.

    try {
      platformVersion =
          await _didChangeBiometricsPlugin.onCheckBiometrics() ?? 'Unknown';
    } catch (e) {
      platformVersion = e.toString();
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;
    setState(() {});
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: SizedBox(
            width: double.maxFinite,
            child: Padding(
              padding: const EdgeInsets.all(24),
              child: Text('Running on: ${platformVersion.toString()}\n ',
                  textAlign: TextAlign.center, softWrap: true),
            ),
          ),
        ),
      ),
    );
  }
}
