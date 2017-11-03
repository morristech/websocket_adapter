#!/usr/bin/env bash

./gradlew :library:createCoverageReport
./gradlew :library:uploadCoverageToCodacy
bash <(curl -s https://codecov.io/bash)