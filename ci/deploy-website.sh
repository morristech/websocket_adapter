#!/bin/bash

set -ex

# Setup script constants.
TEMP_DIR=website-temp
PLUGIN_NAME=websocket_adapter
PLUGIN_ARTIFACT_NAME="${PLUGIN_NAME//_/-}"
PLUGIN_VERSION=1.0.0
PLUGIN_REPO="git@github.com:universum-studios/java_${PLUGIN_NAME}.git"
PLUGIN_DIR_ARTIFACTS=../artifacts/universum/studios/gradle/${PLUGIN_ARTIFACT_NAME}/${PLUGIN_VERSION}/
PLUGIN_JAVADOC_FILE_NAME="${PLUGIN_ARTIFACT_NAME}-${PLUGIN_VERSION}-javadoc.jar"
PLUGIN_DIR_TESTS=../plugin/build/reports/tests/test/
PLUGIN_DIR_COVERAGE=../plugin/build/reports/coverage/
PLUGIN_DIR_BUGS=../plugin/build/reports/findbugs/
WEBSITE_FILES_VERSION="${PLUGIN_VERSION:0:1}".x
WEBSITE_DIR_DOC=doc/
WEBSITE_DIR_DOC_VERSIONED=${WEBSITE_DIR_DOC}${WEBSITE_FILES_VERSION}/
WEBSITE_DIR_TESTS=tests/
WEBSITE_DIR_TESTS_VERSIONED=${WEBSITE_DIR_TESTS}${WEBSITE_FILES_VERSION}/
WEBSITE_DIR_COVERAGE=coverage/
WEBSITE_DIR_COVERAGE_VERSIONED=${WEBSITE_DIR_COVERAGE}${WEBSITE_FILES_VERSION}/
WEBSITE_DIR_BUGS=bugs/
WEBSITE_DIR_BUGS_VERSIONED=${WEBSITE_DIR_BUGS}${WEBSITE_FILES_VERSION}/

# Delete left-over temporary directory (if exists).
rm -rf ${TEMP_DIR}

#  Clone the current repo into temporary directory.
git clone --depth 1 --branch gh-pages ${PLUGIN_REPO} ${TEMP_DIR}

# Move working directory into temporary directory.
cd ${TEMP_DIR}

# Delete all files for the current version.
rm -rf ${WEBSITE_DIR_DOC_VERSIONED}
rm -rf ${WEBSITE_DIR_TESTS_VERSIONED}
rm -rf ${WEBSITE_DIR_COVERAGE_VERSIONED}
# rm -rf ${WEBSITE_DIR_BUGS_VERSIONED}

# Copy files for documentation and reports for Android tests and Coverage from the primary plugin module.
# Documentation:
mkdir -p ${WEBSITE_DIR_DOC_VERSIONED}
cp ${PLUGIN_DIR_ARTIFACTS}${PLUGIN_JAVADOC_FILE_NAME} ${WEBSITE_DIR_DOC_VERSIONED}${PLUGIN_JAVADOC_FILE_NAME}
unzip ${WEBSITE_DIR_DOC_VERSIONED}${PLUGIN_JAVADOC_FILE_NAME} -d ${WEBSITE_DIR_DOC_VERSIONED}
rm ${WEBSITE_DIR_DOC_VERSIONED}${PLUGIN_JAVADOC_FILE_NAME}
# Tests report:
mkdir -p ${WEBSITE_DIR_TESTS_VERSIONED}
cp -R ${PLUGIN_DIR_TESTS}. ${WEBSITE_DIR_TESTS_VERSIONED}
# Coverage report:
mkdir -p ${WEBSITE_DIR_COVERAGE_VERSIONED}
cp -R ${PLUGIN_DIR_COVERAGE}. ${WEBSITE_DIR_COVERAGE_VERSIONED}
# Bugs report:
# mkdir -p ${WEBSITE_DIR_BUGS_VERSIONED}
# cp -R ${PLUGIN_DIR_BUGS}. ${WEBSITE_DIR_BUGS_VERSIONED}

# Stage all files in git and create a commit.
git add . --all
git add -u
git commit -m "Website at $(date)."

# Push the new website files up to the GitHub.
git push origin gh-pages

# Delete temporary directory.
cd ..
rm -rf ${TEMP_DIR}