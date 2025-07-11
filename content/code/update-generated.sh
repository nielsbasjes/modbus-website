#!/usr/bin/env bash

( cd minimal  && mvn clean generate-sources generate-test-sources )
( cd custom   && mvn clean generate-sources )
