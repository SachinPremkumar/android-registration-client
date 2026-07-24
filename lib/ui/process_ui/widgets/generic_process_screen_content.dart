/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'dart:convert';

import 'package:flutter/material.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/model/field.dart';
import 'package:registration_client/model/process.dart';
import 'package:registration_client/model/screen.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/ui/process_ui/process_type.dart';
import 'package:registration_client/ui/process_ui/widgets/age_date_control.dart';
import 'package:registration_client/ui/process_ui/widgets/biometric_capture_control.dart';

import 'package:registration_client/ui/process_ui/widgets/checkbox_control.dart';
import 'package:registration_client/ui/process_ui/widgets/date_control.dart';
import 'package:registration_client/ui/process_ui/widgets/document_upload_control.dart';
import 'package:registration_client/ui/process_ui/widgets/dropdown_control.dart';
// import 'package:registration_client/ui/process_ui/widgets/dynamic_dropdown_control.dart';
import 'package:registration_client/ui/process_ui/widgets/gender_control.dart';
import 'package:registration_client/ui/process_ui/widgets/html_box_control.dart';

import 'package:registration_client/ui/process_ui/widgets/button_control.dart';
import 'package:registration_client/ui/process_ui/widgets/terms_and_conditions.dart';
import 'package:registration_client/ui/process_ui/widgets/pre_reg_data_control.dart';
import 'package:registration_client/ui/process_ui/widgets/additional_Info_ReqId_control.dart';
import 'package:registration_client/ui/process_ui/widgets/textbox_control.dart';

import 'radio_button_control.dart';

class GenericProcessScreenContent extends StatefulWidget {
  const GenericProcessScreenContent({
    super.key,
    required this.context,
    required this.screen,
    required this.processType,
    required this.process,
  });
  
  final BuildContext context;
  final Screen screen;
  final ProcessType processType;
  final Process process;

  @override
  State<GenericProcessScreenContent> createState() =>
      _GenericProcessScreenContentState();
}

class _GenericProcessScreenContentState extends State<GenericProcessScreenContent> {
  late GlobalProvider globalProvider;
  late RegistrationTaskProvider registrationTaskProvider;
  int refreshValue = 0;

  // Guards against overlapping native MVEL evaluations for the same field.
  final Set<String> _mvelEvaluationInFlight = {};

  // _checkMvelVisible is invoked for every conditional field on every
  // rebuild of this widget. Evaluating each field's MVEL condition is only
  // ever done once per screen instance (tracked here) instead of once per
  // rebuild: whatever triggers this widget to rebuild (unrelated provider
  // changes, keyboard, other fields' input, etc.) must not re-fire native
  // MVEL calls — each call can itself cause a rebuild via
  // GlobalProvider.notifyListeners(), and without this guard that becomes
  // a self-sustaining loop regardless of how many rebuilds are throttled
  // elsewhere.
  final Set<String> _mvelCheckedFieldIds = {};

  // Conditions like introducer name/RID visibility depend on age group
  // (e.g. "infant"), which is only known once DOB is entered — after the
  // fields have already done their one-time MVEL check above. When age
  // group actually changes (scoped via context.select, so this doesn't
  // fire on unrelated rebuilds), clear the cache so every conditional
  // field gets exactly one fresh re-check against the new age group.
  String? _lastAgeGroupForMvelCheck;

  @override
  void initState() {
    globalProvider = Provider.of<GlobalProvider>(context, listen: false);
    registrationTaskProvider =
        Provider.of<RegistrationTaskProvider>(context, listen: false);
    super.initState();
  }

  Widget widgetType(Field e) {
    RegExp regexPattern = RegExp(r'^.*$');

    if (e.validators != null && e.validators!.isNotEmpty) {
      final validation = e.validators?.first?.validator;
      if (validation != null) {
        regexPattern = RegExp(validation);
      }
    }

    if (e.id == "preferredLang") {
      return const SizedBox.shrink();
    }
    if ((widget.processType == ProcessType.newProcess ||
            widget.processType == ProcessType.correctionProcess) &&
        e.inputRequired == false) {
      return const SizedBox.shrink();
    }

    switch (e.controlType) {
      case "checkbox":
        if (e.subType == "gender") {
          return RadioButtonControl(field: e);
        }
        if (e.group!.toLowerCase() == "consent") {
          return TermsAndConditions(field: e);
        }
        return CheckboxControl(field: e);
      case "html":
        return HtmlBoxControl(field: e);
      case "biometrics":
        final mvelRequired = context
            .select<GlobalProvider, bool?>((p) => p.mvelRequiredFields[e.id]);
        if (mvelRequired ?? _getDefaultBiometricVisibility()) {
          return BiometricCaptureControl(e: e);
        }
        return Container();
      case "button":
        if (e.subType == "preferredLang") {
          return ButtonControl(field: e);
        }
        if (e.subType == "gender" || e.subType == "residenceStatus") {
          return RadioButtonControl(field: e);
        }
        //feature will implement
        if (e.subType == "selectedHandles") {
          return const SizedBox.shrink();
        }
        return Text("${e.controlType}");
      case "textbox":
        return TextBoxControl(e: e, validation: regexPattern);
      case "dropdown":
        if (e.id == "gender") {
          return GenderControl(field: e, validation: regexPattern);
        }
        return DropDownControl(
          validation: regexPattern,
          field: e,
        );

      case "ageDate":
        return AgeDateControl(
          field: e,
          validation: regexPattern,
        );
      case "date":
        return DateControl(
          validation: regexPattern,
          field: e,
        );
      case "fileupload":
        return DocumentUploadControl(
          field: e,
          validation: regexPattern,
        );
      default:
        return (e.controlType != null) ? Text("${e.controlType}") : const SizedBox.shrink();
    }
  }

  bool _getDefaultBiometricVisibility() {
    if (widget.processType == ProcessType.updateProcess) {
      return false;
    }
    return true;
  }

  evaluateMVELVisible(String fieldData, Field e) async {
    final key = '${e.id}#visible';
    if (_mvelEvaluationInFlight.contains(key)) return;
    _mvelEvaluationInFlight.add(key);
    try {
      final value = await registrationTaskProvider.evaluateMVELVisible(fieldData);
      if (!mounted) return;
      if (!value) {
        globalProvider.removeFieldFromMap(
            e.id!, globalProvider.fieldInputValue);
        registrationTaskProvider.removeDemographicField(e.id!);
      }
      globalProvider.setMvelVisibleFields(e.id!, value);
    } finally {
      _mvelEvaluationInFlight.remove(key);
    }
  }

  evaluateMVELRequired(String fieldData, Field e) async {
    final key = '${e.id}#required';
    if (_mvelEvaluationInFlight.contains(key)) return;
    _mvelEvaluationInFlight.add(key);
    try {
      final value = await registrationTaskProvider.evaluateMVELRequired(fieldData);
      if (!mounted) return;
      globalProvider.setMvelRequiredFields(e.id!, value);
    } finally {
      _mvelEvaluationInFlight.remove(key);
    }
  }

  void _checkMvelVisible(Field e) {
    final id = e.id;
    if (id == null || _mvelCheckedFieldIds.contains(id)) return;

    final bool needsCheck = widget.processType == ProcessType.updateProcess
        ? (e.requiredOn != null && e.requiredOn!.isNotEmpty)
        : (e.required == false &&
            e.requiredOn != null &&
            e.requiredOn!.isNotEmpty);
    if (!needsCheck) return;

    _mvelCheckedFieldIds.add(id);
    final fieldData = jsonEncode(e.toJson());
    evaluateMVELVisible(fieldData, e);
    evaluateMVELRequired(fieldData, e);
  }

  bool _shouldShowField(Field e) {

    if (widget.processType == ProcessType.updateProcess) {
      if (widget.process.autoSelectedGroups!.contains(e.group)) {
        return true;
      } else if (globalProvider.selectedUpdateFields[e.group] != null) {
        return true;
      }
      return false;
    }

    final mvelVisible =
        context.select<GlobalProvider, bool?>((p) => p.mvelVisibleFields[e.id]);
    return mvelVisible ?? true;
  }

  @override
  Widget build(BuildContext context) {
    final preRegControllerRefresh = context
        .select<GlobalProvider, bool>((p) => p.preRegControllerRefresh);
    final formKey =
        context.select<GlobalProvider, GlobalKey<FormState>>((p) => p.formKey);

    final ageGroup = context.select<GlobalProvider, String>((p) => p.ageGroup);
    if (ageGroup != _lastAgeGroupForMvelCheck) {
      _lastAgeGroupForMvelCheck = ageGroup;
      _mvelCheckedFieldIds.clear();
    }

    return Column(
      children: [
        if (widget.screen.preRegFetchRequired == true) ...[
          PreRegDataControl(
              screen: widget.screen,
              onFetched: () {
                setState(() {
                  refreshValue = 1;
                });
              }),
        ],

        if (widget.screen.additionalInfoRequestIdRequired == true) ...[
          const AdditionalInfoReqIdControl(),
        ],

        preRegControllerRefresh
            ? const CircularProgressIndicator()
            : Form(
                key: formKey,
                child: Column(
                  children: [
                    ...widget.screen.fields!.map((e) {
                      _checkMvelVisible(e!);
                      if (_shouldShowField(e)) {
                        return widgetType(e);
                      }
                      return Container();
                    }).toList(),
                  ],
                ),
              ),
      ],
    );
  }
}
