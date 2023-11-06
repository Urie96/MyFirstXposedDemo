install:
	./gradlew build
	adb shell am force-stop me.kyuubiran.xposedapps
	adb install app/build/outputs/apk/debug/app-debug.apk