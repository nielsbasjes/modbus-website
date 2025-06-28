#!/bin/bash

MODBUS_SCHEMA_PARENT_VERSION="$(           wget -q https://repo1.maven.org/maven2/nl/basjes/modbus/modbus-schema-parent/maven-metadata.xml                  -O - | fgrep '<latest>' | cut -d'>' -f2 | cut -d'<' -f1)"
MODBUS_DEVICE_SDM630_VERSION="$(           wget -q https://repo1.maven.org/maven2/nl/basjes/modbus/devices/modbus-device-sdm630/maven-metadata.xml          -O - | fgrep '<latest>' | cut -d'>' -f2 | cut -d'<' -f1)"
MODBUS_DEVICE_THERMIA_GENESIS_VERSION="$(  wget -q https://repo1.maven.org/maven2/nl/basjes/modbus/devices/modbus-device-thermia-genesis/maven-metadata.xml -O - | fgrep '<latest>' | cut -d'>' -f2 | cut -d'<' -f1)"
MODBUS_SCHEMA_MAVEN_PLUGIN_VERSION="$(     wget -q https://repo1.maven.org/maven2/nl/basjes/modbus/modbus-schema-maven-plugin/maven-metadata.xml            -O - | fgrep '<latest>' | cut -d'>' -f2 | cut -d'<' -f1)"
SUNSPEC_DEVICE_VERSION="$(                 wget -q https://repo1.maven.org/maven2/nl/basjes/sunspec/sunspec-device/maven-metadata.xml                       -O - | fgrep '<latest>' | cut -d'>' -f2 | cut -d'<' -f1)"

echo -n "${MODBUS_SCHEMA_PARENT_VERSION}"          > layouts/shortcodes/modbus-schema-parent-version.md
echo -n "${MODBUS_DEVICE_SDM630_VERSION}"          > layouts/shortcodes/modbus-device-sdm630-version.md
echo -n "${MODBUS_DEVICE_THERMIA_GENESIS_VERSION}" > layouts/shortcodes/modbus-device-thermia-genesis-version.md
echo -n "${MODBUS_SCHEMA_MAVEN_PLUGIN_VERSION}"    > layouts/shortcodes/modbus-schema-maven-plugin-version.md
echo -n "${SUNSPEC_DEVICE_VERSION}"                > layouts/shortcodes/sunspec-device-version.md

find content -type f -name '*.xml' -print0 | xargs -0 -n1 sed -i "s@<modbus-schema.version>[^<]\+</modbus-schema.version>@<modbus-schema.version>${MODBUS_SCHEMA_PARENT_VERSION}</modbus-schema.version>@g"

sed -i "s@<version>[^<]\+</version>@<version>${MODBUS_SCHEMA_PARENT_VERSION}</version>@g" content/code/plugin-fragment.pom.xml

find content -type f -name '*.main.kts' -print0 | xargs -0 -n1 sed -i "s@:DependsOn.\"nl.basjes.modbus:modbus-schema-device:[^\"]\+\")@:DependsOn(\"nl.basjes.modbus:modbus-schema-device:${MODBUS_SCHEMA_PARENT_VERSION}\")@g"
find content -type f -name '*.main.kts' -print0 | xargs -0 -n1 sed -i "s@:DependsOn.\"nl.basjes.modbus:modbus-api-plc4j:[^\"]\+\")@:DependsOn(\"nl.basjes.modbus:modbus-api-plc4j:${MODBUS_SCHEMA_PARENT_VERSION}\")@g"
find content -type f -name '*.main.kts' -print0 | xargs -0 -n1 sed -i "s@:DependsOn.\"nl.basjes.modbus:modbus-api-j2mod:[^\"]\+\")@:DependsOn(\"nl.basjes.modbus:modbus-api-j2mod:${MODBUS_SCHEMA_PARENT_VERSION}\")@g"
find content -type f -name '*.main.kts' -print0 | xargs -0 -n1 sed -i "s@:DependsOn.\"nl.basjes.modbus:modbus-api-digitalpetri:[^\"]\+\")@:DependsOn(\"nl.basjes.modbus:modbus-api-digitalpetri:${MODBUS_SCHEMA_PARENT_VERSION}\")@g"
find content -type f -name '*.main.kts' -print0 | xargs -0 -n1 sed -i "s@:DependsOn.\"nl.basjes.modbus.devices:modbus-device-sdm630:[^\"]\+\")@:DependsOn(\"nl.basjes.modbus.devices:modbus-device-sdm630:${MODBUS_DEVICE_SDM630_VERSION}\")@g"
find content -type f -name '*.main.kts' -print0 | xargs -0 -n1 sed -i "s@:DependsOn.\"nl.basjes.modbus.devices:modbus-device-thermia-genesis:[^\"]\+\")@:DependsOn(\"nl.basjes.modbus.devices:modbus-device-thermia-genesis:${MODBUS_DEVICE_THERMIA_GENESIS_VERSION}\")@g"
find content -type f -name '*.main.kts' -print0 | xargs -0 -n1 sed -i "s@:DependsOn.\"nl.basjes.sunspec:sunspec-device:[^\"]\+\")@:DependsOn(\"nl.basjes.sunspec:sunspec-device:${SUNSPEC_DEVICE_VERSION}\")@g"

# Some of the code in the documentation is actually generated using the real software.
( cd content/code && ./update-generated.sh )

git status