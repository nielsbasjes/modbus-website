+++
title = 'Schema Specification'
weight = 30
+++

## Introduction
This is a way of defining a schema for a modbus device.
It includes way of specifying sets of registers and methods that manipulate and interpret these registers.
The interpreted values can then be combined to form the desired end result.

## The parts of a schema
In this toolkit a modbus schema consist of these parts
- Device specific settings
- All the meaningful fields and the mapping on how to obtain them from the underlying modbus values provided by the physical device. 
- Tests that relate a set of raw register/discrete values to the expected meaningful values.
  - This can be used to make it easier to verify the expressions in the fields and to test new implementations of this library (when others want to reimplement it in a different language)

{{% children %}}

<!--
  Modbus Schema Toolkit
  Copyright (C) 2019-2025 Niels Basjes

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  https://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
