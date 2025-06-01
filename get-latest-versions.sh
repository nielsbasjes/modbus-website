#!/bin/bash

MODBUS_SCHEMA_PARENT_VERSION="$(wget https://repo1.maven.org/maven2/nl/basjes/modbus/modbus-schema-parent/maven-metadata.xml                            -O - | fgrep '<latest>' | cut -d'>' -f2 | cut -d'<' -f1)"
MODBUS_DEVICE_SDM630_VERSION="$(wget https://repo1.maven.org/maven2/nl/basjes/modbus/devices/modbus-device-sdm630/maven-metadata.xml                    -O - | fgrep '<latest>' | cut -d'>' -f2 | cut -d'<' -f1)"
MODBUS_DEVICE_THERMIA_GENESIS_VERSION="$(wget https://repo1.maven.org/maven2/nl/basjes/modbus/devices/modbus-device-thermia-genesis/maven-metadata.xml  -O - | fgrep '<latest>' | cut -d'>' -f2 | cut -d'<' -f1)"
MODBUS_SCHEMA_MAVEN_PLUGIN_VERSION="$(wget https://repo1.maven.org/maven2/nl/basjes/modbus/modbus-schema-maven-plugin/maven-metadata.xml                -O - | fgrep '<latest>' | cut -d'>' -f2 | cut -d'<' -f1)"
SUNSPEC_DEVICE_VERSION="$(wget https://repo1.maven.org/maven2/nl/basjes/sunspec/sunspec-device/maven-metadata.xml                                       -O - | fgrep '<latest>' | cut -d'>' -f2 | cut -d'<' -f1)"

echo -n "${MODBUS_SCHEMA_PARENT_VERSION}"          > layouts/shortcodes/modbus-schema-parent-version.md
echo -n "${MODBUS_DEVICE_SDM630_VERSION}"          > layouts/shortcodes/modbus-device-sdm630-version.md
echo -n "${MODBUS_DEVICE_THERMIA_GENESIS_VERSION}" > layouts/shortcodes/modbus-device-thermia-genesis-version.md
echo -n "${MODBUS_SCHEMA_MAVEN_PLUGIN_VERSION}"    > layouts/shortcodes/modbus-schema-maven-plugin-version.md
echo -n "${SUNSPEC_DEVICE_VERSION}"                > layouts/shortcodes/sunspec-device-version.md

find content -type f -print0 | xargs -0 -n1 sed -i "s@<modbus-schema.version>[^<]+</modbus-schema.version>@<modbus-schema.version>${MODBUS_SCHEMA_PARENT_VERSION}</modbus-schema.version>@g"

find content -type f -name '*.main.kts' -print0 | xargs -0 -n1 sed -i "s/@file:DependsOn(\"nl.basjes.modbus:modbus-schema-parent:[^\"]+\")/@file:DependsOn(\"nl.basjes.modbus:modbus-schema-parent:${MODBUS_SCHEMA_PARENT_VERSION}\")/g"
find content -type f -name '*.main.kts' -print0 | xargs -0 -n1 sed -i "s/@file:DependsOn(\"nl.basjes.modbus.devices:modbus-device-sdm630:[^\"]+\")/@file:DependsOn(\"nl.basjes.modbus.devices:modbus-device-sdm630:${MODBUS_DEVICE_SDM630_VERSION}\")/g"
find content -type f -name '*.main.kts' -print0 | xargs -0 -n1 sed -i "s/@file:DependsOn(\"nl.basjes.modbus.devices:modbus-device-thermia-genesis:[^\"]+\")/@file:DependsOn(\"nl.basjes.modbus.devices:modbus-device-thermia-genesis:${MODBUS_DEVICE_THERMIA_GENESIS_VERSION}\")/g"
find content -type f -name '*.main.kts' -print0 | xargs -0 -n1 sed -i "s/@file:DependsOn(\"nl.basjes.sunspec:sunspec-device:[^\"]+\")/@file:DependsOn(\"nl.basjes.sunspec:sunspec-device:${SUNSPEC_DEVICE_VERSION}\")/g"
