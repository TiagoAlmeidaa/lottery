#!/usr/bin/env bash
set -e
#    Proccess:
# 1.  Go into app directory
# 2.  Get current versionCode and print it
# 3.  Increment versionCode by one and print it
# 4.  Get current versionName and print it
# 5.  Ask user for new versionName and set it then print it
# 6.  Stage changes to build.gradle
# 7.  Go back one directory to project root
# 8.  Commit version update with message Bumped up version
# 9.  Tag with versionName
# 10. Push changes

echo "Version increment script for Lottery App"

function sedi {
  if [ "$(uname)" == "Linux" ]; then
    sed -i "$@"
  else
    sed -i "" "$@"
  fi
}

cd "buildSrc/src/main/java"

newVersionCode=0
newVersionName=""

currentVersionCode=$(awk '/VERSION_CODE/ {print $5}' ./AppVersions.kt)
echo "Current versionCode is: $currentVersionCode"

# Reading the VERSION_CODE from AppVersions.kt and incrementing its value
while IFS='=' read -r prop val; do
  case $prop in
  *"VERSION_CODE"*)
    val=$((val + 1))
    newVersionCode=$val
    ;;
  *"VERSION_NAME"*)
    val="1.0.4"
    newVersionName=$val
  esac
done < AppVersions.kt > AppVersions.kt.tmp && mv AppVersions.kt.tmp AppVersions.kt

echo "newVersionCode: $newVersionCode"
echo "newVersionName: $newVersionName"
