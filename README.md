# MW3 GD656 Addon

GD656Killicon 的 MW3 战斗反馈附属。

## 安装

从 [Releases](https://github.com/xiwanzi/MW3-GD656-Addon/releases) 下载 JAR，放入客户端的 `mods/`。需要 Minecraft 1.20.1、Java 17、Forge（实测 47.4.0）和 [GD656Killicon 1.3.2 的 Forge 1.20.1 版](https://modrinth.com/mod/gd656killicon/version/zSQIkgKo)。首次启动会自动应用内置预设。TaCZ 可选，已验证 Forge 1.20.1 版 1.2.0 Hotfix。TaCZ 的「一枪一个」在多人游戏中还需服务端支持。

## 构建

使用 JDK 17，把 GD656Killicon 1.3.2 和 TaCZ 1.2.0 的 Forge 1.20.1 JAR 放入 `libs/`，文件名见 [build.gradle](build.gradle)。运行 `./gradlew build`，产物位于 `build/libs/`。

## 许可

附属模组源码采用 [GPL-3.0-only](LICENSE)。MWIII 游戏素材权利归原权利人，Noto 字形遵循 [SIL OFL](NOTICE-Noto.txt)。
