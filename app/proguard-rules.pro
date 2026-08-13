# WorkManager uses Room internally via reflection - keep generated impl classes
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Database class * { *; }
-dontwarn androidx.room.paging.**
