@echo off
title APITools构建工具
@rem 执行构建前先clean下
@call mvn clean
@rem 构建方法
@call mvn assembly:single

cmd