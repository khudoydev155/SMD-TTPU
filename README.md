
Bu loyiha TTPU SMD kursining **8-Amaliy mashg'uloti (Permissions)** topshiriqlarini to'liq bajarish uchun Jetpack Compose va Kotlin tilida yaratilgan.

- **Paket nomi:** `uz.ttpu.permissionslab`
- **Sinalgan API darajalari:** API 30 (Android 11), API 32 (Android 12L), API 34 (Android 14)

---

## Amaliy topshiriqlar bo'yicha hisobot (B Qism)

### 1-topshiriq. Kotlin modeli: rad etish holatlari (`PermissionModel`)
- `PermissionModel.kt` klassi va unit testlari yozildi (`PermissionModelTest.kt`).
- Model 4 bosqichli so'rov jarayonini to'liq simulyatsiya qiladi:
  1. **1-rad etish:** `granted = false`, `rationale = true`, `dialogShows = true`
  2. **2-rad etish:** `granted = false`, `rationale = false`, `dialogShows = true`
  3. **3-so'rov:** `granted = false`, `rationale = false`, `dialogShows = false` (Dialog chiqmaydi, darhol rad etiladi).
  4. **4-so'rov:** `granted = false`, `rationale = false`, `dialogShows = false` (Darhol rad etiladi).

### 2-topshiriq. Loyiha va normal ruxsatnomalar
- Manifestda `INTERNET` va `ACCESS_NETWORK_STATE` e'lon qilingan.
- `ConnectivityManager` orqali real vaqt rejimida tarmoq holati ("Online" / "Offline") ko'rsatiladi.
- **Izoh:** Normal ruxsatnomalar foydalanuvchi maxfiyligiga xavf solmagani uchun o'rnatish vaqtida (install-time) avtomatik beriladi va runtime dialog yozish talab etilmaydi.

### 3-topshiriq. Kamera ekrani: uch holat
- Manifestda `CAMERA` va `<uses-feature android:name="android.hardware.camera" android:required="false" />` e'lon qilindi.
- Uchta tarmoq boshqariladi:
  1. `isGranted == true`: Kamera ruxsati berilganligi ko'rsatiladi.
  2. `shouldShowRationale == true`: Tushuntirish kartasi va "Ruxsat berish" tugmasi chiqadi.
  3. Ikki marta rad etilganda: "Sozlamalarni ochish" tugmasi ko'rsatilib, `Settings.ACTION_APPLICATION_DETAILS_SETTINGS` orqali Sozlamalarga yo'naltiradi.

### 4-topshiriq. Special ruxsatnoma: overlay
- Manifestda `SYSTEM_ALERT_WINDOW` e'lon qilindi.
- `Switch` bosilganda `Settings.ACTION_MANAGE_OVERLAY_PERMISSION` orqali Sozlamalar ekrani ochiladi.
- `LifecycleEventEffect(Lifecycle.Event.ON_RESUME)` yordamida Sozlamalardan qaytgan zahoti `Settings.canDrawOverlays(context)` qayta o'qilib, Switch holati avtomatik yangilanadi.

### 5-topshiriq. Bir martalik joylashuv (One-time location) va kuzatuv
- `ACCESS_FINE_LOCATION` va `ACCESS_COARSE_LOCATION` ruxsatnomalari `RequestMultiplePermissions` orqali so'raladi va `Map<String, Boolean>` sifatida o'qiladi.
- **Kuzatuv natijasi ( README uchun bir paragraf ):**
  > Foydalanuvchi joylashuv dialogida "Only this time" (Faqat bu safar) ni tanlaganda, ruxsatnoma faqat ilova aktiv (foreground) bo'lib turganda vaqtincha taqdim etiladi. Ilova fonga o'tkazilib (background) biroz vaqt o'tgach, OS ushbu ruxsatnomani avtomatik bekor qiladi (revoke). Ilovaga qaytilganda `LifecycleEventEffect(Lifecycle.Event.ON_RESUME)` har safar ruxsat holatini qayta tekshirgani sababli, ekranda ruxsat bekor qilinganligi ("Ruxsat berilmagan") to'g'ri aks etadi va qayta so'rash imkoniyati beriladi.

### 6-topshiriq (Bonus). Bildirishnoma ruxsatnomasi (POST_NOTIFICATIONS)
- Android 13+ (API 33+) qurilmalarida `POST_NOTIFICATIONS` runtime permission so'raladi.
- API 33 dan past versiyalarda runtime so'rov o'tkazib yuboriladi (chunki install-time permission hisoblanadi).
- **Taqqoslama natijasi (API 32 vs API 34):**
  > • **API 32 (Android 12L):** `POST_NOTIFICATIONS` runtime ruxsatnomasi mavjud emas. Ilova o'rnatilishi bilan bildirishnomalar yuborish huquqiga ega bo'ladi va test bildirishnomasi dialogsiz yuboriladi.  
  > • **API 34 (Android 14):** Bildirishnoma yuborishdan oldin runtime dialog chiqadi. Foydalanuvchi "Allow" tugmasini bosmaguncha bildirishnoma yuborilmaydi va tizim panelida ko'rinmaydi.

---

## A Qism. Nazariy savollar javoblari

Full nazariy javoblar va tushuntirishlar quyida keltirilgan.
