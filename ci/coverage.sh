#!/usr/bin/env bash

./gradlew :library:createCoverageReport
bash <(curl -s https://codecov.io/bash)