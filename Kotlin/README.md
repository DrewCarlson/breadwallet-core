[![Build Status](https://travis-ci.com/DrewCarlson/breadwallet-core.svg?branch=drew%2Fkotlin)](https://travis-ci.com/DrewCarlson/breadwallet-core)

# walletkit-kotlin
[Kotlin Multiplatform](https://kotl.in/multiplatform) walletkit implementation

### Structure

[src/commonMain/kotlin](src/commonMain/kotlin): Contains common Kotlin that does not use any platform specific APIs.
Code with the `expect` keyword is replaced by code prefixed with `actual` in a platform specific source directory (`src/<platform>Main/kotlin`).
This is the multiplatform API definition matching the Swift and Java/corecrypto APIs.
Mutliplatform Kotlin applications can use this API to target any/all supported platforms.

[src/commonTest/kotlin](src/commonTest/kotlin): Contains common Kotlin test sources.
These tests are compiled and run for every supported platform.

[src/jvmMain/kotlin](src/jvmMain/kotlin): Contains JVM `actual`s and other specific APIs using the Java/corenative module.
This code can be used directly in Kotlin/JVM code or Java.

[src/macosMain/kotlin](src/macosMain/kotlin): Contains (obj-c generic) macOS `actual`s and other platform specific APIs using the walletkit C API.
This code can be used directly in Kotlin/Native code or Obj-C/Swift.

### Build and Test

Running any gradle command will download all of the necessary tooling, note the first run takes some time.

* Run tests: `./gradlew allTest`, `./gradlew macosTest`, `./gradlew jvmTest`
* Build outputs: `./gradlew assemble`
* Clean build dirs: `./gradlew clean`

### Notes

- The macOS source set can be used for iOS/watchOS/tvOS. This is skipped because of the shorter testing loop without the simulator and core build setup.
- Compilation of the native core library is not automated, for now a static `libCoreMacOS.a` is used.
- While possible, the current iteration of the JVM implementation and Java/corecrypto are not binary compatible.
- Android can be added atop the JVM implementation but requires a little bit of build configuration to support dev environments without the Android SDK installed.
