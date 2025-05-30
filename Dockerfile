#
# Modbus Schema Toolkit
# Copyright (C) 2019-2025 Niels Basjes
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
FROM ubuntu:24.04

WORKDIR /root

ENV INSIDE_DOCKER=Yes

ARG DEBIAN_FRONTEND=noninteractive

# Suppress an apt-key warning about standard out not being a terminal. Use in this script is safe.
ENV APT_KEY_DONT_WARN_ON_DANGEROUS_USAGE=DontWarn

WORKDIR /root

SHELL ["/bin/bash", "-o", "pipefail", "-c"]

#####
# Disable suggests/recommends
#####
RUN echo APT::Install-Recommends "0"\; > /etc/apt/apt.conf.d/10disableextras
RUN echo APT::Install-Suggests "0"\; >>  /etc/apt/apt.conf.d/10disableextras

ENV DEBIAN_FRONTEND="noninteractive"
ENV DEBCONF_TERSE="true"

###
# Update and install common packages
###
RUN apt clean cache \
   && apt -q update \
   && apt install -y software-properties-common apt-utils apt-transport-https ca-certificates \
   && apt clean cache

RUN apt-get -q install -y --no-install-recommends \
    golang-go \
    bzip2 \
    wget \
    curl \
    git \
    vim \
     && apt clean cache

# --------------------------------
# Install Hugo ( https://github.com/gohugoio/hugo/releases )
ENV HUGO_VERSION=0.147.6
WORKDIR /usr/local/bin
RUN wget https://github.com/gohugoio/hugo/releases/download/v${HUGO_VERSION}/hugo_${HUGO_VERSION}_Linux-64bit.tar.gz && \
    tar xzf hugo_${HUGO_VERSION}_Linux-64bit.tar.gz && \
    rm -f hugo_${HUGO_VERSION}_Linux-64bit.tar.gz

# --------------------------------
# For serving the documentation site
EXPOSE 1313

