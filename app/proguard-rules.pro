# R8 / ProGuard Configuration for Namaz Mobile App

# 1. Native methods: preserve native methods for JNI
-keepclasseswithmembernames class * {
    native <methods>;
}

# 2. Gson / Serialization: Keep fields of DTO models serialized/deserialized via reflection
-keepclassmembers class co.gorkem.namaz.data.remote.dto.** {
    <fields>;
}
-keepclassmembers class co.gorkem.namaz.service.WearPrayerData** {
    <fields>;
}
-keepclassmembers class co.gorkem.namaz.service.WearPrayerItem {
    <fields>;
}
-keepclassmembers class co.gorkem.namaz.service.WearNextPrayer {
    <fields>;
}
-keepclassmembers class co.gorkem.namaz.service.WearKerahatInfo {
    <fields>;
}

# 3. Glance AppWidget: preserve Glance widget classes
-keep class * extends androidx.glance.appwidget.GlanceAppWidget {
    public <init>(...);
}

# 4. MapLibre: JNI reflection and NativeMapView
-keepclassmembers class org.maplibre.android.maps.NativeMapView {
    native <methods>;
}
-dontwarn org.maplibre.geojson.**
