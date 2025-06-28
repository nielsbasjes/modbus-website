#!/usr/bin/env bash

( cd basicuse && mvn clean generate-sources generate-test-sources )
( cd minimal  && mvn clean generate-sources generate-test-sources )
( cd custom   && mvn clean generate-sources )
