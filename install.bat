@echo off
title APITools��������
@rem ��������
@call mvn clean
@call mvn compile
@call mvn assembly:single
cmd