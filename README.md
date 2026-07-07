# SimpleSpeedView

<div align="center">

一个功能强大、高度可定制的 Android 速度仪表盘自定义视图库

[![](https://jitpack.io/v/DIABLOSER/SimpleSpeedView.svg)](https://jitpack.io/#DIABLOSER/SimpleSpeedView)
![Platform](https://img.shields.io/badge/platform-Android-green.svg)
![Language](https://img.shields.io/badge/language-Kotlin-blue.svg)
![License](https://img.shields.io/badge/license-Apache%202.0-orange.svg)

</div>

## ✨ 功能特性

- 🎨 **高度可定制**：支持自定义圆环颜色、宽度、起始角度、扫过角度等
- 🌈 **进度渐变**：支持圆环进度条起止渐变色，沿弧线平滑过渡
- 🎯 **灵活的指针样式**：支持颜色指针和图片指针，可调节长度、宽度和偏移量
- 📊 **刻度显示**：支持显示刻度值，可选内侧或外侧显示
- 🔢 **速度文本显示**：可自定义速度数值、单位的颜色、大小和位置
- 🎬 **流畅动画**：内置动画效果，速度变化更加平滑自然
- 🖼️ **背景图片支持**：速度文本支持自定义背景图片
- 🎨 **圆环边框**：支持为圆环添加边框，增强视觉效果
- 📱 **易于使用**：简单的 XML 属性配置和代码调用

## 📸 预览
![演示效果](https://github.com/DIABLOSER/SimpleSpeedView/blob/master/demonstrate.gif)

## 📦 安装

### Step 1. 添加 JitPack 仓库到你的构建文件

在项目根目录的 `settings.gradle` 文件中添加 JitPack 仓库：

**settings.gradle (Groovy)**
```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

**settings.gradle.kts (Kotlin DSL)**
```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

### Step 2. 添加依赖

在模块的 `build.gradle` 或 `build.gradle.kts` 文件中添加依赖：

**build.gradle (Groovy)**
```gradle
dependencies {
    implementation 'com.github.DIABLOSER:SimpleSpeedView:Tag'
}
```

**build.gradle.kts (Kotlin DSL)**
```kotlin
dependencies {
    implementation("com.github.DIABLOSER:SimpleSpeedView:Tag")
}
```

> **注意**：请将 `Tag` 替换为最新的版本号，查看 [![](https://jitpack.io/v/DIABLOSER/SimpleSpeedView.svg)](https://jitpack.io/#DIABLOSER/SimpleSpeedView) 获取最新版本

## 🚀 快速开始

### 在 XML 布局中使用

```xml
<io.github.diabloser.SimpleSpeedView
    android:id="@+id/speedView"
    android:layout_width="300dp"
    android:layout_height="300dp"
    app:startAngle="135"
    app:sweepAngle="270"
    app:arcWidth="30dp"
    app:arcBackgroundColor="#CCCCCC"
    app:arcProgressColor="#00BCD4"
    app:arcProgressStartColor="#00BCD4"
    app:arcProgressEndColor="#FF5722"
    app:pointerColor="#FF0000"
    app:pointerLength="150dp"
    app:pointerWidth="8dp"
    app:currentSpeed="0"
    app:maxSpeed="200"
    app:showSpeedText="true"
    app:speedTextSize="48sp"
    app:speedUnit="km/h"
    app:showScaleText="true"
    app:scaleTextOutside="true"
    app:scaleCount="11" />
```

### 在代码中使用

```kotlin
val speedView = findViewById<SimpleSpeedView>(R.id.speedView)

// 设置速度（带动画）
speedView.animateToSpeed(80f)

// 直接设置速度（无动画）
speedView.setSpeed(80f)

// 设置最大速度
speedView.setMaxSpeed(200f)

// 自定义颜色
speedView.setArcProgressColor(Color.parseColor("#00BCD4"))
speedView.setArcProgressStartColor(Color.parseColor("#00BCD4"))
speedView.setArcProgressEndColor(Color.parseColor("#FF5722"))
speedView.setPointerColor(Color.RED)

// 设置指针样式
speedView.setPointerDrawable(R.drawable.pointer_image) // 使用图片
speedView.setPointerDrawable(0) // 使用颜色

// 获取当前速度
val currentSpeed = speedView.getSpeed()
```

## 📝 自定义属性

### 圆环属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `startAngle` | float | 135 | 圆环起始角度（度数） |
| `sweepAngle` | float | 270 | 圆环扫过的角度（度数） |
| `arcBackgroundColor` | color | #CCCCCC | 圆环背景颜色 |
| `arcProgressColor` | color | #00BCD4 | 圆环进度颜色（未设置渐变起止色时作为默认值） |
| `arcProgressStartColor` | color | 同 `arcProgressColor` | 圆环进度渐变起始颜色（弧线起点） |
| `arcProgressEndColor` | color | 同 `arcProgressColor` | 圆环进度渐变结束颜色（弧线终点） |
| `arcWidth` | dimension | 30dp | 圆环宽度 |
| `arcBorderColor` | color | transparent | 圆环边框颜色 |
| `arcBorderWidth` | dimension | 0dp | 圆环边框宽度 |

> **进度渐变说明**：`arcProgressStartColor` 与 `arcProgressEndColor` 相同时显示纯色；设置不同颜色时，进度条沿弧线从起始色渐变到结束色。若未单独设置起止色，将使用 `arcProgressColor` 作为默认值。

### 指针属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `pointerColor` | color | #FF0000 | 指针颜色 |
| `pointerDrawable` | reference | 0 | 指针图片资源ID |
| `pointerLength` | dimension | 150dp | 指针长度 |
| `pointerWidth` | dimension | 8dp | 指针宽度 |
| `pointerOffset` | dimension | 0dp | 指针偏移量（正数远离圆心，负数经过圆心） |

### 速度属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `currentSpeed` | float | 0 | 当前速度值 |
| `maxSpeed` | float | 200 | 最大速度值 |

### 速度文本属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `showSpeedText` | boolean | true | 是否显示速度文本 |
| `speedTextColor` | color | #000000 | 速度数值颜色 |
| `speedTextSize` | dimension | 48sp | 速度数值大小 |
| `speedUnit` | string | "km/h" | 速度单位文本 |
| `speedUnitColor` | color | #808080 | 速度单位颜色 |
| `speedUnitSize` | dimension | 24sp | 速度单位大小 |
| `speedTextBackground` | reference | 0 | 速度文本背景图片资源ID |
| `speedTextBackgroundPadding` | dimension | 12dp | 速度文本背景内边距 |
| `speedTextOffsetY` | dimension | 0dp | 速度文本垂直偏移量（正数向下，负数向上） |

### 刻度属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `showScaleText` | boolean | true | 是否显示刻度值 |
| `scaleTextOutside` | boolean | true | 刻度值显示在外侧（true）或内侧（false） |
| `scaleTextColor` | color | #808080 | 刻度值颜色 |
| `scaleTextSize` | dimension | 28sp | 刻度值大小 |
| `scaleTextStyle` | enum | normal | 刻度值样式：normal, bold, italic, bold_italic |
| `scaleCount` | integer | 11 | 刻度数量 |

## 🔧 API 方法

### 速度控制

```kotlin
// 设置速度（无动画）
fun setSpeed(speed: Float)

// 设置速度（带动画，默认500ms）
fun animateToSpeed(targetSpeed: Float)

// 设置速度（带动画，自定义时长）
fun animateToSpeed(targetSpeed: Float, duration: Long)

// 取消当前动画
fun cancelSpeedAnimation()

// 获取当前速度
fun getSpeed(): Float

// 设置最大速度
fun setMaxSpeed(max: Float)
```

### 外观设置

```kotlin
// 圆环设置
fun setArcBackgroundColor(color: Int)
fun setArcProgressColor(color: Int)
fun setArcProgressStartColor(color: Int)
fun setArcProgressEndColor(color: Int)
fun setArcWidth(width: Float)
fun setArcBorderColor(color: Int)
fun setArcBorderWidth(width: Float)
fun setStartAngle(angle: Float)
fun setSweepAngle(angle: Float)

// 指针设置
fun setPointerColor(color: Int)
fun setPointerDrawable(resId: Int) // 传0则使用颜色绘制
fun setPointerLength(length: Float)
fun setPointerWidth(width: Float)
fun setPointerOffset(offset: Float)

// 速度文本设置
fun setShowSpeedText(show: Boolean)
fun setSpeedTextColor(color: Int)
fun setSpeedTextSize(size: Float)
fun setSpeedUnit(unit: String)
fun setSpeedUnitColor(color: Int)
fun setSpeedUnitSize(size: Float)
fun setSpeedTextBackground(resId: Int)
fun setSpeedTextBackgroundPadding(padding: Float)
fun setSpeedTextOffsetY(offsetY: Float)

// 刻度设置
fun setShowScaleText(show: Boolean)
fun setScaleTextOutside(outside: Boolean)
fun setScaleTextColor(color: Int)
fun setScaleTextSize(size: Float)
fun setScaleTextStyle(style: Int) // 0=normal, 1=bold, 2=italic, 3=bold_italic
fun setScaleCount(count: Int)
```

## 📖 使用示例

### 示例 1：创建一个基础速度仪表盘

```kotlin
val speedView = findViewById<SimpleSpeedView>(R.id.speedView)
speedView.animateToSpeed(60f) // 设置速度为 60
```

### 示例 2：创建一个自定义样式的仪表盘

```kotlin
speedView.apply {
    setArcProgressStartColor(Color.parseColor("#00BCD4"))
    setArcProgressEndColor(Color.parseColor("#FF5722"))
    setPointerColor(Color.parseColor("#FF5722"))
    setArcWidth(40f)
    setPointerLength(180f)
    setSpeedTextSize(60f)
    setSpeedUnit("mph")
    animateToSpeed(120f, 1000) // 1秒动画
}
```

### 示例 3：使用图片作为指针

```kotlin
speedView.setPointerDrawable(R.drawable.custom_pointer)
speedView.setPointerOffset(50f) // 调整指针位置
```

### 示例 4：动态修改圆环角度

```kotlin
// 创建半圆仪表盘
speedView.setStartAngle(180f)
speedView.setSweepAngle(180f)
```

### 示例 5：添加速度文本背景

```kotlin
speedView.setSpeedTextBackground(R.drawable.bg_speed_circle)
speedView.setSpeedTextBackgroundPadding(16f)
```

## 🎨 高级自定义

### 创建一个完全自定义的仪表盘

```xml
<io.github.diabloser.SimpleSpeedView
    android:id="@+id/customSpeedView"
    android:layout_width="350dp"
    android:layout_height="350dp"
    android:padding="20dp"
    app:startAngle="180"
    app:sweepAngle="180"
    app:arcWidth="40dp"
    app:arcBackgroundColor="#E0E0E0"
    app:arcProgressStartColor="#00BCD4"
    app:arcProgressEndColor="#FF5722"
    app:arcBorderColor="#333333"
    app:arcBorderWidth="3dp"
    app:pointerDrawable="@drawable/custom_pointer"
    app:pointerLength="200dp"
    app:pointerOffset="50dp"
    app:currentSpeed="0"
    app:maxSpeed="300"
    app:showSpeedText="true"
    app:speedTextColor="#FF5722"
    app:speedTextSize="64sp"
    app:speedUnit="mph"
    app:speedUnitColor="#666666"
    app:speedUnitSize="28sp"
    app:speedTextBackground="@drawable/bg_speed_rounded"
    app:speedTextBackgroundPadding="20dp"
    app:speedTextOffsetY="-30dp"
    app:showScaleText="true"
    app:scaleTextOutside="false"
    app:scaleTextColor="#FF5722"
    app:scaleTextSize="24sp"
    app:scaleTextStyle="bold"
    app:scaleCount="7" />
```

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

1. Fork 本项目
2. 创建你的特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交你的修改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开一个 Pull Request

## 📄 许可证

本项目采用 Apache License 2.0 许可证。详情请参阅 [LICENSE](LICENSE) 文件。

```
Copyright 2024 DIABLOSER

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## 📧 联系方式

- GitHub: [@DIABLOSER](https://github.com/DIABLOSER)
- 项目链接: [https://github.com/DIABLOSER/SimpleSpeedView](https://github.com/DIABLOSER/SimpleSpeedView)

## ⭐ Star History

如果这个项目对你有帮助，请给它一个 Star ⭐️

---

<div align="center">
Made with ❤️ by DIABLOSER
</div>

