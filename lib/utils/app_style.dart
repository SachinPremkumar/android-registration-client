import 'package:flutter/material.dart';
import 'package:registration_client/utils/app_config.dart';

abstract class AppStyle {
  static const Color appWhite = Color(0xFFffffff);
  static const Color appBlack = Color(0xFF000000);
  static const Color appBlackShade1 = Color(0xFF333333);
  static const Color appBlackShade3 = Color(0xFF999999);
  static const Color appGreyShade = Color(0xFF9B9B9F);
  static const Color appSolidPrimary = Color(0xFF1C43A1);
  static const Color appBlueShade1 = Color(0xFF214FBF);
  static const Color appInfoText = Color(0xFFB1C8FF);
  static const Color appHelpText = Color(0xFF1C43A2);
  static const Color appYellow = Color(0xFFFEC401);
  static const Color appOrange = Color(0xFFF97707);
  static const Color appBackButtonBorder = Color(0xFF2A4EA7);
  static const Color buttonDisabled = Color(0XFFCCCCCC);
  static const Color mandatoryField = Color(0XFFD32D2D);
  static const Color previewHeaderColor = Color(0XFF666666);
  static const Color previewHeaderComponentColor = Color(0XFFF5F5F5);
  static const Color authIconBackground = Color(0XFFF8FCFF);
  static const Color authIconBorder = Color(0XFFE1EDF5);
  static const Color dividerColor = Color(0XFFE5EBFA);

  static TextStyle mobileHelpText = TextStyle(
    fontWeight: FontWeight.w500,
    fontSize: 14,
    color: appHelpText,
    letterSpacing: 0.5,
  );

  static TextStyle mobileHeaderText = TextStyle(
    fontWeight: FontWeight.w500,
    fontSize: 28,
    color: appBlackShade1,
    letterSpacing: 0.5,
  );

  static TextStyle mobileTextfieldHeader = TextStyle(
    fontWeight: FontWeight.w500,
    fontSize: 14,
    color: appBlackShade1,
    letterSpacing: 0.5,
  );

  static TextStyle mobileDropdownText = TextStyle(
    fontWeight: FontWeight.normal,
    fontSize: 14,
    color: appBlackShade1,
    letterSpacing: 0.5,
  );

  static TextStyle mobileTextfieldHintText = TextStyle(
    fontWeight: FontWeight.normal,
    fontSize: 14,
    color: appBlackShade3,
    letterSpacing: 0.5,
  );

  static TextStyle mobileButtonText = TextStyle(
    fontWeight: FontWeight.bold,
    fontSize: 14,
    color: appWhite,
    letterSpacing: 0.5,
  );

  static TextStyle mobileWelcomeText = TextStyle(
    fontWeight: FontWeight.normal,
    fontSize: 26,
    color: appWhite,
    letterSpacing: 0.5,
  );
  
  static TextStyle mobileCommunityRegClientText = TextStyle(
    fontWeight: FontWeight.bold,
    fontSize: 26,
    color: appWhite,
    letterSpacing: 0.5,
  );

  static TextStyle mobileInfoText = TextStyle(
    fontWeight: FontWeight.normal,
    fontSize: 16,
    color: appInfoText,
    letterSpacing: 0.5,
  );

  static TextStyle mobileForgotPasswordText = TextStyle(
    fontWeight: FontWeight.w500,
    fontSize: 14,
    color: appSolidPrimary,
    letterSpacing: 0.5,
  );

  static TextStyle mobileBackButtonText = TextStyle(
    fontWeight: FontWeight.bold,
    fontSize: 14,
    color: appHelpText,
    letterSpacing: 0.5,
  );

  static TextStyle tabletHelpText = TextStyle(
    fontWeight: FontWeight.w500,
    fontSize: 14,
    color: appHelpText,
    letterSpacing: 0,
  );

  static TextStyle tabletHeaderText = TextStyle(
    fontWeight: FontWeight.w500,
    fontSize: 28,
    color: appBlackShade1,
    letterSpacing: 0,
  );

  static TextStyle tabletTextfieldHeader = TextStyle(
    fontWeight: FontWeight.w500,
    fontSize: 14,
    color: appBlackShade1,
    letterSpacing: 0,
  );

  static TextStyle tabletDropdownText = TextStyle(
    fontWeight: FontWeight.normal,
    fontSize: 14,
    color: appBlackShade1,
    letterSpacing: 0,
  );

  static TextStyle tabletTextfieldHintText = TextStyle(
    fontWeight: FontWeight.normal,
    fontSize: 14,
    color: appBlackShade3,
    letterSpacing: 0,
  );

  static TextStyle tabletButtonText = TextStyle(
    fontWeight: FontWeight.bold,
    fontSize: 14,
    color: appWhite,
    letterSpacing: 0,
  );

  static TextStyle tabletWelcomeText = TextStyle(
    fontWeight: FontWeight.normal,
    fontSize: 26,
    color: appWhite,
    letterSpacing: 0,
  );

  static TextStyle tabletCommunityRegClientText = TextStyle(
    fontWeight: FontWeight.bold,
    fontSize: 26,
    color: appWhite,
    letterSpacing: 0,
  );

  static TextStyle tabletInfoText = TextStyle(
    fontWeight: FontWeight.normal,
    fontSize: 16,
    color: appInfoText,
    letterSpacing: 0,
  );

  static TextStyle tabletForgotPasswordText = TextStyle(
    fontWeight: FontWeight.w500,
    fontSize: 14,
    color: appSolidPrimary,
    letterSpacing: 0,
  );

  static TextStyle tabletBackButtonText = TextStyle(
    fontWeight: FontWeight.bold,
    fontSize: 14,
    color: appHelpText,
    letterSpacing: 0,
  );

  static TextStyle previewHeaderText = TextStyle(
    fontWeight: regular,
    fontSize: 14,
    color: previewHeaderColor,
  );

  static TextStyle previewHeaderResponseText = TextStyle(
    fontWeight: semiBold,
    fontSize: 16,
    color: appBlackShade1,
  );

  static TextStyle previewComponentHeaderText = TextStyle(
    fontWeight: semiBold,
    fontSize: 20,
    color: appBlack
  );
}