# Copy Plus

一个轻量的 IntelliJ 平台插件：在编辑器里选中任意内容后右键 **Copy with Location**，会把所选内容连同**文件名、绝对路径、项目内相对路径、行号**一起拼成一段简洁的提示词拷贝到剪贴板，方便贴进 ChatGPT / Claude / Cursor 等 AI 聊天框。

> Quickly copy any selection — variable, method, if/else branch, YAML block, SQL snippet — together with its file path and line range, ready to paste into an AI chat.

## 为什么需要它

把代码片段复制给 AI 助手时，常常需要手动补充“这段代码来自哪个文件、哪一行”。Copy Plus 一键完成：

- 自动识别 **单行 / 多行选择**，输出对应的行号或行号区间
- 同时给出 **绝对路径** 和 **项目内相对路径**，便于 AI 关联代码结构
- 多行选择自动 **去除公共缩进**，AI 看到的就是干净的代码片段，不再带着 12 个前导空格
- 适用于任意文本文件，不只是 Java

## 安装

### 方式一：从 Release 下载

1. 进入 [GitHub Releases](../../releases)，下载最新的 `copyplus-x.y.z.zip`。
2. IntelliJ IDEA → **Settings / Preferences** → **Plugins** → 齿轮图标 → **Install Plugin from Disk...** → 选中刚才下载的 zip。
3. 重启 IDE 即可。

### 方式二：本地构建

```bash
./gradlew buildPlugin
# 产物在 build/distributions/copyplus-<version>.zip
```

## 使用

1. 在编辑器中选中一段内容（变量、方法、if/else 块、配置项……）。
2. 右键 → **Copy with Location**（默认快捷键 `Ctrl + Alt + C`）。
3. 粘贴到 AI 聊天框即可。

### 输出示例

选中一段 Java if/else 分支后，剪贴板内容如下：

```
请帮我分析下面这段内容：

文件: OrderService.java
绝对路径: /Users/cj/code/demo/src/main/java/com/demo/OrderService.java
项目内路径: src/main/java/com/demo/OrderService.java
行号: 42-58 (共 17 行)

if (order.isPaid()) {
    return shipOrder(order);
} else {
    return holdOrder(order);
}
```

选中一行 YAML 配置：

```
请帮我分析下面这段内容：

文件: application.yml
绝对路径: /Users/cj/code/demo/src/main/resources/application.yml
项目内路径: src/main/resources/application.yml
行号: 12

spring.datasource.url: jdbc:mysql://localhost:3306/demo
```

## 兼容性

- IntelliJ Platform 2023.3+ (`sinceBuild = 233`)
- 适用于 IntelliJ IDEA、PyCharm、WebStorm、GoLand 等所有基于 IntelliJ 平台的 IDE

## 开发

```bash
./gradlew runIde      # 在沙箱 IDE 里调试插件
./gradlew buildPlugin # 打包发布产物
```

## 发布

推送形如 `v1.0.0` 的 tag，GitHub Action 会自动构建并把 zip 上传到 Release：

```bash
git tag v1.0.0
git push origin v1.0.0
```

## License

MIT
