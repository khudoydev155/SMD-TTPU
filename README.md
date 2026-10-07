# MovieShelf (9-laboratoriya)

Android'da Clean Architecture: `presentation` -> `domain` <- `data`. Paket: `uz.ttpu.movieshelf`.

```
:domain  (sof Kotlin moduli)  model, repository (interfeys), usecase
:app
  data/remote, data/local, data/mapper, data/repository
  presentation/movies         MovieListViewModel, MovieListScreen
  di/AppContainer.kt          composition root
```

Branch'lar: `main` (bitta commit) va `lab-9` (har bir vazifa alohida commit).

Testlar: `app/src/test/java/uz/ttpu/movieshelf/` -> o'ng tugma -> Run Tests.
15-vazifa loglari: `git apply ../extras/task15-cleanflow-logs.patch` (qaytarish: `git apply -R ...`).
