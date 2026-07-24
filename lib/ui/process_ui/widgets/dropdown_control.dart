/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/pigeon/dynamic_response_pigeon.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/utils/app_config.dart';

import '../../../model/field.dart';
import '../../../provider/global_provider.dart';
import 'custom_label.dart';

import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class DropDownControl extends StatefulWidget {
  const DropDownControl({
    super.key,
    required this.field,
    required this.validation,
  });

  final Field field;
  final RegExp validation;

  @override
  State<DropDownControl> createState() => _CustomDropDownState();
}

class _CustomDropDownState extends State<DropDownControl> {
  GenericData? selected;
  late GlobalProvider globalProvider;
  late RegistrationTaskProvider registrationTaskProvider;

  int? index;
  int maxLen = 0;
  List<GenericData?> list = [];
  String? _lastFetchedParentCode;
  bool _hasFetchedOnce = false;

  // 'default' fieldType doesn't necessarily mean the dropdown is part of the
  // location hierarchy — some deployments use `default` for plain flat-list
  // dropdowns too (e.g. maritalStatus, countryOfCitizenship). Membership in
  // hierarchyReverse is the actual signal, mirroring desktop's
  // DropDownFxControl.getSubTypeLangCode check.
  bool get _isHierarchical =>
      widget.field.fieldType != 'dynamic' &&
      globalProvider.hierarchyReverse.contains(widget.field.subType);

  String get _mapKey => _isHierarchical
      ? "${widget.field.group}${widget.field.subType}"
      : (widget.field.id ?? "");

  @override
  void initState() {
    globalProvider = Provider.of<GlobalProvider>(context, listen: false);
    registrationTaskProvider =
        Provider.of<RegistrationTaskProvider>(context, listen: false);
    if (_isHierarchical) {
      setHierarchyReverse();
    }
    // initializeValue();
    super.initState();
  }

  initializeValue() {
    String langCode = globalProvider.selectedLanguage;
    if (_isFieldIdPresent()) {
      GenericData? response;
      if (widget.field.type == 'simpleType') {
        if ((globalProvider.fieldInputValue[_mapKey] as Map<String, dynamic>)
            .containsKey(langCode)) {
          response = globalProvider.fieldInputValue[_mapKey][langCode]
              as GenericData;
        }
      } else {
        response = globalProvider.fieldInputValue[_mapKey] as GenericData;
      }
      setState(() {
        selected = response;
      });
    }
  }

  setHierarchyReverse() {
    maxLen = globalProvider.hierarchyReverse.length;
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    if (_isHierarchical) {
      index = globalProvider.hierarchyReverse.indexOf(widget.field.subType!);
    }
  }

  void saveData(value) async {
    if (_isHierarchical) {
      for (int i = index! + 1; i < maxLen; i++) {
        registrationTaskProvider
            .removeDemographicField(globalProvider.hierarchyReverse[i]);
      }
    }
    if (value != null) {
      if (widget.field.type == 'simpleType') {
        for (var element in globalProvider.chosenLang) {
          List<GenericData?> temp;
          String code = globalProvider.languageToCodeMapper[element]!;
          if (!_isHierarchical) {
            temp = await _getDynamicFieldValues(widget.field.subType!, code);
          } else if (index == 1) {
            temp = await _getLocationValues("$index", code);
          } else if (index != null && index! > 1) {
            var parentCode = context
                .read<GlobalProvider>()
                .groupedHierarchyValues[widget.field.group]![index! - 1];
            temp = await _getLocationValuesBasedOnParent(
                parentCode, widget.field.subType!, code);
          } else {
            temp = [];
          }
          temp.forEach((element) {
            if (element!.code == value.code && element.langCode == code) {
              registrationTaskProvider.addSimpleTypeDemographicField(
                  widget.field.id ?? "", element.name, code);
              registrationTaskProvider.addSelectedCode(widget.field.id ?? "", element.code);
            }
          });
        }
      } else {
        registrationTaskProvider.addDemographicField(
            widget.field.id ?? "", value.name);
        registrationTaskProvider.addSelectedCode(widget.field.id ?? "", value.code);
      }
    }
  }

  void _saveDataToMap(GenericData? value) {
    String lang = globalProvider.mandatoryLanguages[0]!;
    if (_isHierarchical) {
      for (int i = index! + 1; i < maxLen; i++) {
        globalProvider.removeFieldFromMap(
          "${widget.field.group}${globalProvider.hierarchyReverse[i]}",
          globalProvider.fieldInputValue,
        );
      }
    }
    if (value != null) {
      if (widget.field.type == 'simpleType') {
        globalProvider.setLanguageSpecificValue(
          _mapKey,
          value,
          lang,
          globalProvider.fieldInputValue,
        );
      } else {
        globalProvider.setInputMapValue(
          _mapKey,
          value,
          globalProvider.fieldInputValue,
        );
      }
    }
  }

  void _getSelectedValueFromMap(String lang, List<GenericData?> list) {
    GenericData? response;
    if (widget.field.type == 'simpleType') {
      if ((globalProvider.fieldInputValue[_mapKey] as Map<String, dynamic>)
          .containsKey(lang)) {
        response = globalProvider.fieldInputValue[_mapKey][lang] as GenericData;
      }
    } else {
      response = globalProvider.fieldInputValue[_mapKey] as GenericData;
    }
    setState(() {
      for (var element in list) {
        if (element!.name == response!.name) {
          selected = element;
        }
      }
    });
  }

  Future<List<GenericData?>> _getLocationValues(
      String hierarchyLevelName, String langCode) async {
    List<String> languages = [];
    for (var lang in globalProvider.chosenLang) {
      String langCode = globalProvider.langToCode(lang);
      languages.add(langCode);
    }
    return await registrationTaskProvider.getLocationValues(
        hierarchyLevelName, langCode, languages);
  }

  Future<List<GenericData?>> _getLocationValuesBasedOnParent(
      String? parentCode, String hierarchyLevelName, String langCode) async {
    List<String> selectedLang = [];
    for (var lang in globalProvider.chosenLang) {
      String langCode = globalProvider.langToCode(lang);
      selectedLang.add(langCode);
    }
    return await registrationTaskProvider.getLocationValuesBasedOnParent(
        parentCode, hierarchyLevelName, langCode, selectedLang);
  }

  Future<List<GenericData?>> _getDynamicFieldValues(
      String fieldId, String langCode) async {
    List<String> selectedLang = [];
    for (var lang in globalProvider.chosenLang) {
      String code = globalProvider.langToCode(lang);
      selectedLang.add(code);
    }
    List<DynamicFieldData?> temp = await registrationTaskProvider
        .getFieldValues(fieldId, langCode, selectedLang);
    return temp
        .map((e) => e == null
            ? null
            : GenericData(
                name: e.name,
                code: e.code,
                langCode: e.langCode,
                concatenatedName: e.concatenatedName,
              ))
        .toList();
  }

  _isFieldIdPresent() {
    return globalProvider.fieldInputValue.containsKey(_mapKey);
  }

  _getOptionsList(String? parentCode) async {
    List<GenericData?> temp = [];
    String lang = globalProvider.mandatoryLanguages[0]!;
    if (!_isHierarchical) {
      temp = await _getDynamicFieldValues(
          widget.field.subType!, globalProvider.selectedLanguage);
    } else if (index == 1) {
      temp =
          await _getLocationValues("$index", globalProvider.selectedLanguage);
    } else if (index != null && index! > 1) {
      temp = await _getLocationValuesBasedOnParent(
          parentCode, widget.field.subType!, globalProvider.selectedLanguage);
    }
    if (!mounted) return;
    setState(() {
      selected = null;
      list = temp;
    });
    if (_isFieldIdPresent()) {
      _getSelectedValueFromMap(lang, list);
    }
  }

  @override
  Widget build(BuildContext context) {
    bool isPortrait =
        MediaQuery.of(context).orientation == Orientation.portrait;

    String? parentCode;
    if (_isHierarchical && index != null && index! > 1) {
      // Narrow, scoped dependency: only rebuilds this widget (and refetches
      // options) when this one hierarchy slot changes, not on unrelated
      // GlobalProvider changes elsewhere on the form. context.select is only
      // valid here, inside build() — not in didChangeDependencies().
      parentCode = context.select<GlobalProvider, String?>((p) =>
          p.groupedHierarchyValues[widget.field.group]?[index! - 1]);
    }
    if (!_hasFetchedOnce || parentCode != _lastFetchedParentCode) {
      _hasFetchedOnce = true;
      _lastFetchedParentCode = parentCode;
      _getOptionsList(parentCode);
    }

    return Column(
      children: [
        Card(
        elevation: 5,
        margin: EdgeInsets.symmetric(
            vertical: 1.h, horizontal: isPortrait ? 16.w : 0),
        child: Padding(
          padding: EdgeInsets.symmetric(vertical: 24.h, horizontal: 16.w),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              CustomLabel(field: widget.field),
              const SizedBox(
                height: 10,
              ),
              DropdownButtonFormField<GenericData>(
                isExpanded: true,
                icon: const Icon(null),
                decoration: InputDecoration(
                  contentPadding:
                      const EdgeInsets.symmetric(horizontal: 16.0),
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(8.0),
                    borderSide: const BorderSide(
                      color: Colors.grey,
                      width: 1.0,
                    ),
                  ),
                  hintText: AppLocalizations.of(context)!.select_option,
                  hintStyle: const TextStyle(
                    color: appBlackShade3,
                  ),
                  suffixIcon: const Icon(Icons.keyboard_arrow_down,
                      color: Colors.grey),
                ),
                items: list
                    .map((option) => DropdownMenuItem(
                          value: option,
                    child: Text(option!.concatenatedName ?? option.name,
                      overflow: TextOverflow.ellipsis,
                      maxLines: 1,
                      softWrap: false,
                    ),
                        ))
                    .toList(),
                autovalidateMode: AutovalidateMode.onUserInteraction,
                value: selected,
                validator: (value) {
                  if (!widget.field.required!) {
                    if (widget.field.requiredOn == null ||
                        widget.field.requiredOn!.isEmpty ||
                        !(globalProvider
                                .mvelRequiredFields[widget.field.id] ??
                            true)) {
                      return null;
                    }
                  }
                  if (value == null) {
                    log("validation $value");
                    return AppLocalizations.of(context)!.select_value_message;
                  }
                  if (!widget.validation.hasMatch(value.name)) {
                    log("validation match $value");
                    return AppLocalizations.of(context)!.select_value_message;
                  }
                  return null;
                },
                onChanged: (value) {
                  if (value != selected) {
                    saveData(value);
                    _saveDataToMap(value);
                    if (_isHierarchical) {
                      globalProvider.setLocationHierarchy(
                          widget.field.group!, value!.code, index!);
                    }
                    String lang = globalProvider.mandatoryLanguages[0]!;
                    _getSelectedValueFromMap(lang, list);
                  }
                },
              ),
            ],
          ),
        ),
        ),
      ],
    );
  }
}
