# SideEffectsLab — 9-Amaliy mashg'ulot (Side-effects)

Toshkent shahridagi Turin Politexnika Universiteti (TTPU)  
**Fan:** Smart Mobile Device Programming (SMD)  
**Mavzu:** Jetpack Compose Side-effects (LaunchedEffect, rememberCoroutineScope, rememberUpdatedState, DisposableEffect, SideEffect, produceState, derivedStateOf, snapshotFlow)  
**Paket nomi:** `uz.ttpu.sideeffectslab`

---

## B Qism. Amaliy topshiriqlar hisoboti

### 1-topshiriq. Splash ekrani (`LandingScreen`)
- **Tanlangan API:** `LaunchedEffect` va `rememberUpdatedState`
- **Nega bu API tanlandi?**
  - `LaunchedEffect(true)` ekran composition'ga kirganda 2 soniyalik kutishni (`delay(2000L)`) bir marta ishga tushirish uchun zarur.
  - `rememberUpdatedState(onTimeout)` esa eng so'nggi lambdani ushlash uchun tanlandi. Agar ota composable har 500 ms da yangi `onTimeout = { log("v$version") }` lambdasini yaratsa, `rememberUpdatedState` tufayli taymer qayta boshlanmaydi va 2 soniya o'tgach eng yangi versiya (`v4`) chaqiriladi.
- **LaunchedEffect(onTimeout) tahlili:**
  - Agar `onTimeout` effect kaliti qilinsa (`LaunchedEffect(onTimeout)`), har 500 ms da ota versiya oshib yangi lambda yaratilganda, LaunchedEffect kaliti o'zgardi deb hisoblaydi va mavjud coroutine'ni bekor qilib, 2 soniyalik kutishni boshidan boshlaydi. Natijada kutish hech qachon tugamaydi!
- **Logcat natijasi:**
  ```text
  D/Task1Splash: Test boshlandi: version = 0
  D/Task1Splash: Version oshdi: v1
  D/Task1Splash: Version oshdi: v2
  D/Task1Splash: Version oshdi: v3
  D/Task1Splash: Version oshdi: v4
  D/Task1Splash: Variant A (Bad) chaqirdi: v0 (Eski/Captured closure!)
  D/Task1Splash: Variant B (Good) chaqirdi: v4 (rememberUpdatedState orqali eng yangi!)
  ```

---

### 2-topshiriq. Snackbar tugmasi
- **Tanlangan API:** `rememberCoroutineScope`
- **Nega bu API tanlandi?**
  - `showSnackbar` bu `suspend` funksiya bo'lib, uni oddiy callback ichidan chaqirib bo'lmaydi.
  - `LaunchedEffect` esa `@Composable` funksiya bo'lgani sababli `Button(onClick = { ... })` lambdasi ichida ishlatib bo'lmaydi (kompilyatsiya xatosi beradi).
  - Shuning uchun foydalanuvchi hodisalaridan (click) suspend funksiyalarni chaqirish uchun yagona to'g'ri yo'l — `rememberCoroutineScope` orqali olingan `scope.launch { ... }` dir.
- **Kuzatuvlar:**
  1. **Ekran tark etilganda:** `rememberCoroutineScope` joriy composable hayot sikliga bog'langan bo'lib, foydalanuvchi boshqa ekranga o'tganda (tab almashganda) scope avtomatik cancel qilinadi. Shu sababli fonda osilib qolgan jarayonlar bo'lmaydi va ilova qulamaydi.
  2. **Ikkinchi marta bosish:** Snackbar ekranda ko'rinib turganda yana bosilsa, `SnackbarHostState` avvalgi snackbarni darhol yopadi (dismiss) va yangi xabarni navbatsiz ko'rsatadi.

---

### 3-topshiriq. Lifecycle logger
- **Tanlangan API:** `DisposableEffect`
- **Nega bu API tanlandi?**
  - Tashqi listener'larni (`LifecycleEventObserver`) ro'yxatdan o'tkazish va composable ekrandan yo'qolganda ularni xavfsiz tozalash (`removeObserver`) kerak bo'lganda `DisposableEffect` ishlatiladi.
  - U tozalash kafolatini beradi: uning oxirgi qatori `onDispose { ... }` bo'lishi shart.
- **Tekshirish (Switch 5 marta almashtirildi):**
  - Har safar Switch yoqilganda 1 ta `addObserver`, o'chirilganda esa 1 ta `removeObserver (onDispose)` ishladi.
  - Jami: `addObserver = 5`, `removeObserver = 5`. Hech qanday xotira oqishi (leak) kuzatilmadi.
- **Logcat natijasi:**
  ```text
  D/Task3Lifecycle: [1] addObserver chaqirildi: observer qo'shildi
  D/Task3Lifecycle: Lifecycle hodisasi: ON_RESUME
  D/Task3Lifecycle: [1] removeObserver (onDispose) chaqirildi
  D/Task3Lifecycle: [2] addObserver chaqirildi: observer qo'shildi
  D/Task3Lifecycle: [2] removeObserver (onDispose) chaqirildi
  D/Task3Lifecycle: [3] addObserver chaqirildi: observer qo'shildi
  D/Task3Lifecycle: [3] removeObserver (onDispose) chaqirildi
  D/Task3Lifecycle: [4] addObserver chaqirildi: observer qo'shildi
  D/Task3Lifecycle: [4] removeObserver (onDispose) chaqirildi
  D/Task3Lifecycle: [5] addObserver chaqirildi: observer qo'shildi
  D/Task3Lifecycle: [5] removeObserver (onDispose) chaqirildi
  ```

---

### 4-topshiriq. SideEffect va analytics
- **Tanlangan API:** `SideEffect`
- **Nega bu API tanlandi?**
  - Compose boshqarmaydigan tashqi obyektlarga (masalan, `Analytics.userType`) Compose State qiymatini xavfsiz uzatish uchun `SideEffect` kerak.
  - U faqat kompozitsiya muvaffaqiyatli yakunlanib, o'zgarishlar ekranga chizilgandan (commit bo'lgandan) so'ng ishlaydi.
- **Kuzatuv va Attempt modeli:**
  - `userType` o'zgarmasa ham, boshqa biror state yangilanganda recomposition sodir bo'ladi va `SideEffect` yana ishlaydi. Sababi: `SideEffect` kalit qabul qilmaydi, u har bir muvaffaqiyatli chizishdan keyin bajariladi.
  - **Attempt modeli:** Compose recomposition urinishini to'xtatishi yoki bekor qilishi (discard) mumkin. To'g'ridan-to'g'ri composable tanasidagi kod bunday bekor qilingan urinishda ham ishlab xato log qoldirishi mumkin, `SideEffect` esa faqat muvaffaqiyatli commit bo'lgandagina ishga tushadi.
- **Logcat natijasi:**
  ```text
  D/Task4SideEffect: SideEffect ishladi! Jami soni: 1, Analytics.userType = Guest
  D/Task4SideEffect: SideEffect ishladi! Jami soni: 2, Analytics.userType = Standard User
  D/Task4SideEffect: SideEffect ishladi! Jami soni: 3, Analytics.userType = Premium VIP
  D/Task4SideEffect: SideEffect ishladi! Jami soni: 4, Analytics.userType = Premium VIP (boshqa state recompose bo'ldi)
  ```

---

### 5-topshiriq. Scroll: derivedStateOf va snapshotFlow
- **Tanlangan API:** `derivedStateOf` va `snapshotFlow`
- **Nega bu API tanlandi?**
  - `listState.firstVisibleItemIndex` skroll davomida juda tez-tez o'zgaradi (0, 1, 2, 3, 4...).
  - Agar tugma ko'rinishini to'g'ridan-to'g'ri `firstVisibleItemIndex > 0` deb yozsak, har bir element siljiganda tugma keraksiz recompose bo'ladi (20-30 martalab).
  - `derivedStateOf` hisob-kitob natijasini (`true` yoki `false`) keshlaydi va faqat natija o'zgargandagina (`0 -> false`, `>0 -> true`) recomposition chaqiradi.
  - `snapshotFlow` esa Compose State'ni Flow'ga aylantirib, `map { it > 0 }.distinctUntilChanged().filter { it }` orqali faqat chegara bosib o'tilgandagina yagona analitika hodisasini yuboradi.
- **Logcat natijasi:**
  ```text
  D/Task5Scroll: DirectRead recomposition soni: 1 (indeks=1)
  D/Task5Scroll: derivedStateOf recomposition soni: 1
  D/Task5Scroll: Analytics hodisasi yuborildi: Chegara (indeks 0) kesib o'tildi! Jami hodisalar: 1
  D/Task5Scroll: DirectRead recomposition soni: 2 (indeks=2)
  D/Task5Scroll: DirectRead recomposition soni: 3 (indeks=3)
  D/Task5Scroll: DirectRead recomposition soni: 4 (indeks=4)
  D/Task5Scroll: DirectRead recomposition soni: 5 (indeks=5)
  // E'tibor bering: derivedStateOf bu paytda qayta recompose bo'lmadi (1 ta bo'lib qoldi)!
  ```

---

### 6-topshiriq (Bonus). produceState
- **Tanlangan API:** `produceState`
- **Nega bu API tanlandi?**
  - Suspend funksiya natijasini (`FakeImageRepository.load(url)`) Compose `State<ImageLoadResult>` ga aylantirish uchun `produceState` eng qulay va toza usul hisoblanadi.
  - U o'z ichida `remember { mutableStateOf(...) }` va `LaunchedEffect` ni birlashtiradi.
- **Kuzatuvlar:**
  - URL o'zgarganda eski yuklash bekor qilinadi va yangi yuklash (`Loading -> Success/Error`) boshlanadi.
  - Composable ekrandan chiqqanda producer coroutine'i bekor bo'lib, uning `finally` bloki bajariladi (`Log.d(TAG, "finally: bekor qilindi")`).

---

## A Qism. Nazariy savollar va javoblar

### 1-savol: Side-effect nima va nega composable tanasida xavfli?
- **Side-effect ta'rifi:** Funksiya o'zining lokal doirasidan tashqaridagi ilova holatini o'zgartirishi yoki tashqi tizimlar bilan ta'sirlashishi (masalan, tarmoq so'rovi, ma'lumotlar bazasiga yozish, global o'zgaruvchini o'zgartirish, analitika log yozish) side-effect deyiladi.
- **Nega Composable tanasida xavfli?** Composable funksiyalar toza (pure) bo'lishi kerak. Compose dvigateli recomposition jarayonini istalgan vaqtda, har qanday freymda bir necha bor qayta chaqirishi, parallel oqimlarda bajarishi yoki animatsiya sababli to'xtatib, bekor qilishi (discard) mumkin. Agar tanada to'g'ridan-to'g'ri `viewModel.loadProfile()` chaqirilsa, animatsiya vaqtida 60 fps tezlikda yuzlab keraksiz tarmoq so'rovlari ketma-ket yuboriladi.
- **Ma'ruzadagi Effect ta'rifi:** Effect — bu composable'ning bevosita UI chizish jarayoniga daxli bo'lmagan, uning hayot davri (lifecycle) bilan xavfsiz boshqariladigan tashqi amallardir.
- **Unidirectional Data Flow (UDF):** Ma'lumotlarni yuklash va holatni boshqarish ViewModel vazifasidir. ViewModel ma'lumotni yuklaydi va StateFlow orqali UI ga uzatadi. UI esa faqat holatni ko'rsatadi va hodisalarni (events) yuqoriga uzatadi.

---

### 2-savol: Compose Effect API jadvali

| Men xohlayman | API |
| :--- | :--- |
| Composable Composition'da turganda suspend funksiyani chaqirish | `LaunchedEffect` |
| onClick kabi callback ichidan coroutine ishga tushirish | `rememberCoroutineScope` |
| Effect ichida eng so'nggi qiymatni o'qish, lekin effect'ni qayta ishga tushirmaslik | `rememberUpdatedState` |
| Listener qo'shish va keyin uni olib tashlash | `DisposableEffect` |
| Compose state'ni Compose boshqarmaydigan kodga uzatish | `SideEffect` |
| Flow, LiveData yoki RxJava'ni Compose State'ga aylantirish | `produceState` |
| Kirish qiymatidan kamroq recompose qilish | `derivedStateOf` |
| Compose State'ni Flow'ga aylantirish | `snapshotFlow` |

---

### 3-savol: PulseEffect kodi tahlili
1. **Effect qachon ishga tushadi?** Ekran ilk bor Composition'ga kirganda hamda `pulseRateMs` qiymati o'zgarganda.
2. **pulseRateMs 3000 dan 1000 ga o'zgarganda:** Mavjud coroutine darhol bekor qilinadi (cancel) va `pulseRateMs = 1000L` yangi qiymati bilan yangi coroutine boshlanadi.
3. **Composition'ni tark etganda:** Composable ekrandan chiqishi bilan LaunchedEffect scope'i bekor qilinadi. `delay()` funksiyasi `CancellationException` tashlaydi va `while(isActive)` sikli to'xtaydi. `isActive` coroutine Job'i hali faol yoki bekor qilinganligini tekshiradi. MiniComposition modelidagi `effect(100) cancelled` aynan shu bekor qilish mexanizmini ifodalaydi.

---

### 4-savol: LaunchedEffect(true) shubhasi
1. **Nega "while(true) kabi shubhali"?** Chunki `true` doimiy qiymat. Kalit hech qachon o'zgarmasligi sababli, uning ichida ishlatilgan `userId` o'zgargan taqdirda ham effect qayta ishga tushmaydi va yangi foydalanuvchiga so'rov ketmaydi.
2. **Qachon doimiy kalit o'rinli?** Ekran ochilganda faqat va faqat bir martagina ishlashi kerak bo'lgan, hech qanday tashqi o'zgaruvchilarga bog'liq bo'lmagan hollarda (masalan, Splash ekrandagi 2 soniyalik kutish).
3. **To'g'rilangan kod:**
   ```kotlin
   LaunchedEffect(userId) {
       api.sendHello(userId)
   }
   ```

---

### 5-savol: Snackbar va callback doirasi
1. **Nega callback ichidan chaqirib bo'lmaydi?** Chunki `LaunchedEffect` bu `@Composable` funksiya. Uni faqat boshqa Composable funksiya tanasidan chaqirish mumkin, oddiy callback (`onClick: () -> Unit`) ichida chaqirib bo'lmaydi.
2. **rememberCoroutineScope:** U joriy Composition nuqtasiga bog'langan `CoroutineScope` qaytaradi. Uning umri composable hayotiga teng: composable tark etilganda ushbu scope va undagi barcha coroutine'lar avtomatik ravishda bekor qilinadi.
3. **To'g'ri variant:**
   ```kotlin
   val scope = rememberCoroutineScope()
   Button(onClick = {
       scope.launch {
           snackbarHostState.showSnackbar("Hi")
       }
   }) { Text("Hi") }
   ```
4. **showSnackbar suspend funksiya:** Snackbar foydalanuvchi tugmani bosguncha yoki vaqti tugaguncha ekranda turadi. Bu vaqt ichida coroutine to'xtab (suspend bo'lib) turadi.
5. **Qoida:**
   - Composable paydo bo'lgani uchun boshlanadigan ish -> `LaunchedEffect`
   - Foydalanuvchi biror amal qilgani (click) uchun boshlanadigan ish -> `rememberCoroutineScope` (`scope.launch`)

---

### 6-savol: LandingScreen va rememberUpdatedState
1. **Eski lambdani ushlash sababi:** Coroutine 2 soniya `delay` qilib turganda, ota composable recomposition bo'lib yangi `onTimeout` parametrini uzatishi mumkin. Ammo `LaunchedEffect(true)` qayta boshlanmagani uchun dastlabki closure'dagi eski lambdani saqlab qoladi.
2. **rememberUpdatedState yechimi:** U ichida `MutableState` saqlaydi va har recomposition'da uni eng yangi lambdaga yangilab boradi. Effect ichida `currentOnTimeout()` chaqirilganda, u har doim eng yangi lambdani oladi.
3. **Nega onTimeout kalit qilinmaydi?** Agar `LaunchedEffect(onTimeout)` qilinsa, har recompose paytida yangi lambda berilganda kutish bekor qilinib boshidan boshlanadi va 2 soniya hech qachon to'lmaydi.
4. **Model isboti:** `captured -> onTimeout v1` eski qiymat chaqirilishini, `updatedState -> onTimeout v3` esa taymer buzilmasdan eng so'nggi callback ishlashini isbotlaydi.

---

### 7-savol: DisposableEffect va onDispose
1. **Nega tozalash kerak?** Agar listener yoki observer olib tashlanmasa, u composable obyekti va kontekstini xotirada ushlab qoladi, bu esa jiddiy xotira oqishiga (memory leak) olib keladi.
2. **onDispose qoidasi:** U `DisposableEffect` blokining eng oxirgi ifodasi bo'lishi shart. Aks holda kod kompilyatsiya bo'lmaydi.
3. **Bo'sh onDispose { }:** Agar tozalash kodi kerak bo'lmasa, `DisposableEffect` ishlatish xato. Uning o'rniga coroutine uchun `LaunchedEffect`, oddiy holat uchun `SideEffect` ishlatish lozim.
4. **Tuzatilgan kod:**
   ```kotlin
   DisposableEffect(lifecycleOwner) {
       val observer = LifecycleEventObserver { _, event -> /* ... */ }
       lifecycleOwner.lifecycle.addObserver(observer)
       onDispose {
           lifecycleOwner.lifecycle.removeObserver(observer)
       }
   }
   ```

---

### 8-savol: MiniDisposable konsol chiqishi
1. **Konsol chiqishi (to'rt qator):**
   ```text
   A.addObserver(observer)
   A.removeObserver(observer)
   B.addObserver(observer)
   B.removeObserver(observer)
   ```
2. **Nega ikkinchi screen("A") hech narsa chiqarmaydi?** Chunki kalit (`"A"`) o'zgarmagan. Kalit bir xil bo'lsa, DisposableEffect qayta ishlamaydi.
3. **B kelganda nima birinchi bajariladi?** Avval eski owner'ning observer'i olib tashlanadi (`A.removeObserver`), shundan so'ng yangi observer qo'shiladi (`B.addObserver`).

---

### 9-savol: SideEffect va attempt modeli
1. **SideEffect qachon ishlaydi?** Recomposition muvaffaqiyatli yakunlanib, o'zgarishlar ekranga chizilgandan (commit bo'lgandan) so'ng darhol ishlaydi.
2. **Bekor qilingan urinish xavfi:** Compose recomposition'ni to'xtatishi va bekor qilishi (discard) mumkin. Agar kod to'g'ridan-to'g'ri composable tanasida bo'lsa, bekor qilingan urinishda ham tashqi kutubxona chaqirilib ketadi va noaniq ma'lumot yoziladi.
3. **Attempt modeli tahlili:**
   - `direct call, commits=false`: Analytics noto'g'ri chaqirilgan (chunki commit bo'lmadi).
   - `SideEffect, commits=false`: Analytics chaqirilmaydi (chunki bekor qilindi).
   - `SideEffect, commits=true`: Analytics muvaffaqiyatli chaqiriladi.
4. **Kalitlari:** `SideEffect` hech qanday kalit qabul qilmaydi.

---

### 10-savol: produceState xususiyatlari
1. **Vazifasi va hayoti:** Suspend manbani Compose `State<T>` ga aylantiradi. Composition'ga kirganda producer boshlanadi, kalit o'zgarganda yoki composable chiqib ketganda bekor qilinadi.
2. **Conflation:** Agar producer tomonidan berilgan yangi qiymat avvalgisi bilan bir xil bo'lsa (`equals == true`), Compose state o'zgarmadi deb hisoblaydi va recomposition chaqirmaydi.
3. **Kichik harf qoidasi:** Qiymat (State) qaytaruvchi barcha Composable funksiyalar kichik harf bilan boshlanishi standart qoidadir (UI element chizuvchi va Unit qaytaruvchilar esa Bosh harf bilan).

---

### 11-savol: myProduceState implementatsiyasi
```kotlin
@Composable
fun <T> myProduceState(
    initialValue: T,
    key: Any?,
    producer: suspend MutableState<T>.() -> Unit
): State<T> {
    val state = remember { mutableStateOf(initialValue) }
    LaunchedEffect(key) {
        state.producer()
    }
    return state
}
```
- **Xatti-harakati:** Ushbu funksiya o'z ichida `LaunchedEffect(key)` ishlatgani sababli, `key` o'zgarganda eski coroutine bekor qilinadi va yangi kalit bilan `producer` qaytadan ishga tushadi.

---

### 12-savol: derivedStateOf va LazyColumn
1. **Nega keraksiz recomposition?** `firstVisibleItemIndex` har bir element o'tganda (0, 1, 2, 3...) o'zgaradi. To'g'ridan-to'g'ri o'qilganda har safar tugma qayta chiziladi. `derivedStateOf` faqat hisoblangan shart (`>0`) natijasi o'zgargandagina recompose chaqiradi.
2. **Nega remember ichida?** Har recomposition paytida yangi `derivedStateOf` obyekti yaratilmasligi va keshlanishi uchun.
3. **Hisoblash [0, 0, 1, 2, 3, 4, 5, 3, 1, 0]:**
   - **To'g'ridan-to'g'ri o'qish:** Har bir indeks o'zgarishi qayta chizadi: 0(start) -> 1 -> 2 -> 3 -> 4 -> 5 -> 3 -> 1 -> 0 = jami **9 ta** recomposition.
   - **derivedStateOf:**
     - 0 da: `false` (1-boshlang'ich)
     - 1 ga o'tganda: `false -> true` (2-recomposition)
     - 2, 3, 4, 5, 3, 1 da: `true` bo'lib qoladi (recomposition YO'Q!)
     - 0 ga qaytganda: `true -> false` (3-recomposition)
     - Jami: bor-yo'g'i **3 ta** recomposition!

---

### 13-savol: FullName va derivedStateOf xatosi
1. **Nega ortiqcha xarajat?** `firstName` yoki `lastName` o'zgarganda baribir `fullName` ham o'zgarishi shart. Kirish va chiqish chastotasi bir xil (1:1), shuning uchun `derivedStateOf` faqat qo'shimcha xotira va wrapper xarajatini oshiradi.
2. **To'g'ri variant:**
   ```kotlin
   val fullName = "$firstName $lastName"
   ```
3. **Qo'llash mezoni:** Kirish o'zgaruvchisi tez-tez o'zgarib (masalan, scroll piksellari), natija esa kamdan-kam o'zgarganda (masalan, boolean chegara) qo'llaniladi.
4. **Caution ogohlantirishi:** Agar natija har bir kirish o'zgarganda yangilansa yoki oddiy arifmetik amal bo'lsa, `derivedStateOf` ishlatmang.

---

### 14-savol: Waveform va derivedStateOf
1. **Qachon ishlatiladi?** Agar audio progress (float) har millisekundda o'zgarib tursa, lekin waveform faqat 50 ta ustunga bo'lingan bo'lsa va faqat faol ustun indeksi o'zgarganda qayta hisoblash kerak bo'lsa.
2. **Qachon ishlatilmaydi?**
   - *Amplitudalar oldindan hisoblangan:* Ular statik ro'yxat bo'lib, o'zgarmaydi.
   - *Waveform statik:* Hech qanday reaktiv o'zgarish bo'lmagani sababli state kerak emas.
   - *LaunchedEffect yoki animate bilan yangilanadi:* Animatsiya o'zi Compose Canvas orqali freymma-freym eng optimal tarzda chiziladi.

---

### 15-savol: snapshotFlow tahlili
1. **snapshotFlow va Cold Flow:** Compose Snapshot State'larini Flow'ga aylantiradi. Unga `collect` chaqirilmaguncha u hech narsa ishlab chiqarmaydi (shuning uchun cold Flow).
2. **Operatorlar vazifasi:**
   - `map`: indeksni Boolean holatga (`index > 0`) o'tkazadi.
   - `distinctUntilChanged`: ketma-ket bir xil kelgan qiymatlarni (masalan, `true, true, true`) elab, faqat qiymat o'zgarganda o'tkazadi.
   - `filter`: faqat `true` bo'lgan qiymatlarni keyingi qadamga o'tkazadi.
3. **Nega collect LaunchedEffect ichida?** `collect` suspend funksiya bo'lib, coroutine ichida ishlashi va composable hayoti bilan boshqarilishi shart.
4. **Hisoblash [0, 0, 1, 2, 5, 3, 0, 0, 1, 4]:**
   - 0 -> `false` -> distinct: `false` -> filter: elandi (0 ta)
   - 0 -> bir xil (`false`), elandi
   - 1 -> `true` -> distinct: o'zgardi (`true`) -> filter: o'tdi -> **1-hodisa!**
   - 2, 5, 3 -> hammasi `true` -> distinct: elandi
   - 0 -> `false` -> distinct: o'zgardi (`false`) -> filter: elandi
   - 0 -> bir xil (`false`), elandi
   - 1 -> `true` -> distinct: o'zgardi (`true`) -> filter: o'tdi -> **2-hodisa!**
   - 4 -> `true` -> distinct: elandi
   - **Jami: 2 ta hodisa yuboriladi.**

---

### 16-savol: Effect kalitlari jadvali

| Kalitlar | Restart soni (bashorat) | Sababi | Xulosa |
| :--- | :--- | :--- | :--- |
| `keys = []` | 0 marta (faqat 1 ta start) | Kalit berilmagan, hech qachon qayta ishlamaydi. | Xato (eskirgan qiymat bilan qoladi). |
| `keys = [owner]` | 1 marta | Faqat 3-tickda owner "A" dan "B" ga o'zgarganda restart bo'ladi. | **To'g'ri va samarali.** |
| `keys = [owner, tick]` | 3 marta | Har bir tick (1, 2, 3, 4) da keraksiz restart bo'ladi. | Samarasiz (ortiqcha resurs sarfi). |

---

### 17-savol: HomeScreen kalitlari
1. **Effect kaliti bo'lishi kerak:** `lifecycleOwner`.
2. **Kalit bo'lmasligi kerak:** `onStart` va `onStop` (ular `rememberUpdatedState` orqali olinadi).
3. **Nega currentOnStart kalit emas?** Chunki uning maqsadi effect'ni qayta boshlamasdan eng so'nggi lambdani chaqirishdir.
4. **lifecycleOwner kalit qilinmasa:** Oyna yoki fragment o'zgarganda observer eski lifecycle'ga bog'langan holda qoladi va xotira oqishi hamda yangi oynada hodisalar ishlamasligiga olib keladi.
5. **Key Point:** Effect kaliti sifatida faqat effect restart qilishi shart bo'lgan obyektlar beriladi; effect ichida o'qilishi kerak bo'lgan boshqa dinamik parametrlar `rememberUpdatedState` bilan o'raladi.

---

### 18-savol: ChatScreen to'rtta xatosi va tuzatish
1. **1-xato:** `viewModel.markOpened(userId)` to'g'ridan-to'g'ri tanada chaqirilgan. Bu har bir recomposition'da qayta-qayta chaqiriladi.
2. **2-xato:** `LaunchedEffect(true)` doimiy kalit bilan yozilgan va `onClose` lambdasi yangilanmaydi.
3. **3-xato:** `DisposableEffect(owner)` ichida `onDispose` yozilmagan.
4. **4-xato:** `Button(onClick = { LaunchedEffect(Unit) { send() } })` — Composable funksiya callback ichida chaqirilgan.

**Tuzatilgan to'liq kod:**
```kotlin
@Composable
fun ChatScreen(userId: String, onClose: () -> Unit) {
    val scope = rememberCoroutineScope()
    val owner = LocalLifecycleOwner.current
    val currentOnClose by rememberUpdatedState(onClose)

    // (1) To'g'ri: Faqat userId o'zgarganda bir marta chaqiriladi
    LaunchedEffect(userId) {
        viewModel.markOpened(userId)
    }

    // (2) To'g'ri: 5 soniyadan so'ng eng yangi onClose() chaqiriladi
    LaunchedEffect(Unit) {
        delay(5_000)
        currentOnClose()
    }

    // (3) To'g'ri: onDispose bilan tozalash qo'shildi
    DisposableEffect(owner) {
        val observer = LifecycleEventObserver { _, _ -> }
        owner.lifecycle.addObserver(observer)
        onDispose {
            owner.lifecycle.removeObserver(observer)
        }
    }

    // (4) To'g'ri: Callback ichidan scope.launch orqali chaqirildi
    Button(onClick = {
        scope.launch {
            send()
        }
    }) {
        Text("Send")
    }
}
```

---

### 19-savol: Sakkizta API yakuniy jadvali va real misollar

| API | Qachon ishlaydi | Tozalash | Restart sababi | Real loyihadan misol |
| :--- | :--- | :--- | :--- | :--- |
| **LaunchedEffect** | Composition'ga kirganda yoki kalit o'zgarganda | Coroutine bekor qilinadi (`cancel`) | Kalit(lar) qiymati o'zgarganda | Foydalanuvchi ID si bo'yicha profil ma'lumotlarini serverdan yuklash. |
| **rememberCoroutineScope** | Composition paytida yaratiladi, `scope.launch` esa callback chaqirilganda ishlaydi | Composable tark etilganda scope bekor qilinadi | Restart bo'lmaydi | Tugma bosilganda BottomSheet yoki Snackbar ochish. |
| **rememberUpdatedState** | Har recomposition'da qiymatni yangilaydi | Yo'q | Restart bo'lmaydi | Splash taymeri yoki audio player tugaganda callback chaqirish. |
| **DisposableEffect** | Composition'ga kirganda va kalit o'zgarganda | `onDispose` bloki bajariladi | Kalit(lar) qiymati o'zgarganda | `BroadcastReceiver` yoki sensor listener'ini yoqish va o'chirish. |
| **SideEffect** | Har bir muvaffaqiyatli recomposition (commit)dan keyin | Yo'q | Har doim ishlaydi (kalitsiz) | Joriy Compose holatini Firebase Analytics yoki tashqi SDK ga uzatish. |
| **produceState** | Composition'ga kirganda yoki kalit o'zgarganda | Coroutine bekor qilinadi | Kalit(lar) qiymati o'zgarganda | Internet orqali rasm yuklab olish holatlarini (Loading/Success/Error) boshqarish. |
| **derivedStateOf** | Kuzatilayotgan state o'zgarganda hisoblanadi | Yo'q | Kuzatilayotgan state'lar o'zgarganda | Ro'yxat skrolli pastga tushganda "Tepaga qaytish" FAB tugmasini ko'rsatish. |
| **snapshotFlow** | Flow `collect` qilinganda state o'zgarishlarini kuzatadi | Coroutine/collector to'xtatilganda | Kuzatilayotgan State qiymati o'zgarganda | Ro'yxat ma'lum bir elementga yetganda analytics log yuborish. |
