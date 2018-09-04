# 빅토리아 2 경제 분석기

원 제작자 소스코드
[Nashetovich](http://oldforum.paradoxplaza.com/forum/showthread.php?715468)
[aekrylov](https://github.com/aekrylov/vic2_economy_analyzer)

## 설치 및 실행법

1. [자바 런타임 환경 8](https://java.com/download/)이 설치되어 있지 않다면 설치합니다.
2. [이 주소](https://github.com/victoria2isgodgame/Victoria-2-Economy-Analyzer/releases)에서 최신버전 JAR 파일을 다운받습니다.
3. ViiEA.JAR 파일을 실행합니다.










# Victoria II Economy Analyzer

Based on old [Nashetovich's code](http://oldforum.paradoxplaza.com/forum/showthread.php?715468) with minor changes, 
 so now it works with Java 8 and higher.

## Usage

1. Install [Java Runtime 8 or newer](https://java.com/download/) if it's not installed
2. Download jar and bat file from [latest release](https://github.com/aekrylov/vic2_economy_analyzer/releases) into one folder
3. Run bat file. You can also run jar file directly, but this can lead to "out of memory" error. 
4. Specify savegame path (also you need to specify game install and mod paths for correct localisation) and press Load. 

Double click on a table row (or single click on a chart item) opens detailed info window.

Check out project wiki for more info.

## Troubleshooting

- **Can't load large late game file**

    `-Xmx` part in the .bat file is responsible for max memory usage.
    Change `-Xmx1024m` to bigger value, e.g. `-Xmx1500m` (should work for most systems).
    
    Please note that setting this value too big can cause errors on some systems.
 
