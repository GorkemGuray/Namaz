# R8 / ProGuard Configuration for Namaz Wear OS App

# 1. Native methods: preserve native methods for JNI
-keepclasseswithmembernames class * {
    native <methods>;
}

# 2. Gson / Data Client: Keep fields of wear data models deserialized via reflection
-keepclassmembers class co.gorkem.namaz.wear.data.WearPrayerData** {
    <fields>;
}
-keepclassmembers class co.gorkem.namaz.wear.data.WearPrayerItem {
    <fields>;
}
-keepclassmembers class co.gorkem.namaz.wear.data.WearNextPrayer {
    <fields>;
}
-keepclassmembers class co.gorkem.namaz.wear.data.WearKerahatInfo {
    <fields>;
}
