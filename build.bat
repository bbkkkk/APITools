@echo off
title APITools构建工具
@rem 构建方法
@call mvn clean
@call mvn compile
@call mvn assembly:single
cmd