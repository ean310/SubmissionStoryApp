# 📱 StoriIn — Submission Story App

[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-34-brightgreen?style=for-the-badge&logo=android)](https://developer.android.com/about/versions/14)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-24-blue?style=for-the-badge&logo=android)](https://developer.android.com/)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM-orange?style=for-the-badge)](https://developer.android.com/topic/architecture)
[![Paging](https://img.shields.io/badge/Jetpack-Paging%203-blueviolet?style=for-the-badge)](https://developer.android.com/topic/libraries/architecture/paging/v3-overview)

**StoriIn** adalah aplikasi media sosial berbagi cerita (*story-sharing app*) berbasis Android yang dikembangkan sebagai proyek submission pada program **Bangkit Academy** (Learning Path Mobile Development - Dicoding). 

Aplikasi ini mengintegrasikan konsumsi RESTful API, otentikasi token sesi, upload media (kamera & galeri) dengan kompresi gambar, penandaan lokasi (Google Maps API & Fused Location), paginasi data otomatis (**Paging 3**), animasi interaktif, serta pengujian unit (*Unit Testing*).

---

## 📑 Daftar Isi

- [Fitur Utama](#-fitur-utama)
- [Teknologi & Library (Tech Stack)](#-teknologi--library-tech-stack)
- [Arsitektur Aplikasi](#-arsitektur-aplikasi)
- [Struktur Direktori](#-struktur-direktori)
- [Prasyarat & Konfigurasi](#-prasyarat--konfigurasi)
  - [1. Konfigurasi Google Maps API Key](#1-konfigurasi-google-maps-api-key)
  - [2. Base URL API](#2-base-url-api)
- [Cara Menjalankan Aplikasi](#-cara-menjalankan-aplikasi)
- [Pengujian (Unit Testing)](#-pengujian-unit-testing)
- [Daftar Endpoint API yang Digunakan](#-daftar-endpoint-api-yang-digunakan)
- [Kontributor & Lisensi](#-kontributor--lisensi)

---

## ✨ Fitur Utama

### 1. 🔐 Autentikasi & Manajemen Sesi
- **Registrasi Akun:** Validasi masukan data pengguna sebelum registrasi ke server.
- **Login Pengguna:** Autentikasi akun dan penyimpanan *Bearer Token* secara lokal.
- **Session Management:** Menggunakan **Jetpack DataStore Preferences** secara asynchronous untuk mengelola status login dan auto-login saat aplikasi dibuka kembali.
- **Logout:** Menghapus sesi dan mengembalikan pengguna ke halaman selamat datang (*Welcome Screen*).

### 2. 📜 Feed Cerita dengan Jetpack Paging 3
- Menampilkan daftar cerita dari seluruh pengguna dengan konsumsi memori dan data yang efisien melalui **Paging 3** (`StoryPagingSource` & `PagingDataAdapter`).
- Smooth image loading & caching menggunakan **Glide** lengkap dengan transisi *cross-fade*.
- Indikator loading (*ProgressBar*) dan pembaruan item reaktif via `DiffUtil`.

### 3. 🔍 Detail Cerita
- Menampilkan informasi detail cerita lengkap (gambar resolusi penuh, nama pembuat, dan deskripsi cerita).

### 4. 📸 Upload Cerita (Media & Lokasi)
- **Kamera Kustom / Intent:** Mengambil foto langsung dari kamera perangkat dengan `FileProvider`.
- **Galeri Foto:** Memilih gambar dari galeri perangkat menggunakan `ActivityResultContracts.PickVisualMedia`.
- **Kompresi Gambar Otomatis:** Reduksi ukuran file gambar hingga di bawah 1 MB (`reduceFileImage`) dan auto-rotate sesuai orientasi Exif.
- **Location Tagging (Opsional):** Opsi melampirkan koordinat lintang & bujur (*latitude* & *longitude*) lokasi terkini pengguna menggunakan `FusedLocationProviderClient`.

### 5. 🗺️ Integrasi Google Maps
- **Peta Lokasi Cerita:** Menampilkan persebaran cerita pengguna di peta interaktif Google Maps.
- **Custom Map Style:** Desain peta yang dikustomisasi melalui styling JSON (`map_style.json`).
- **Interactive Marker & Bounds:** Marker informatif dengan info window nama dan deskripsi, serta auto-zoom/fit boundary kamera (`LatLngBounds`).
- **My Location Support:** Mengaktifkan titik lokasi terkini pengguna dengan runtime permission handling.

### 6. 🎨 Kustom UI, Validasi & Animasi
- **Custom View EditText:**
  - `EditTextEmail`: Validasi format email secara *real-time* via Regex.
  - `EditTextPassword`: Validasi minimal 8 karakter dengan error helper dinamis.
- **Rich Animations:** Menggunakan `ObjectAnimator` dan `AnimatorSet` untuk transisi elemen UI (efek *fade-in* berurutan pada form dan animasi ilustrasi bergerak).

---

## 🛠️ Teknologi & Library (Tech Stack)

| Komponen | Teknologi / Library | Keterangan |
| :--- | :--- | :--- |
| **Bahasa Pemrograman** | [Kotlin](https://kotlinlang.org/) | Bahasa utama pengembangan aplikasi Android modern |
| **Arsitektur** | MVVM (Model-View-ViewModel) | Pemisahan concern bisnis, data, dan UI |
| **Networking** | [Retrofit 2](https://square.github.io/retrofit/) + [OkHttp3](https://square.github.io/okhttp/) | HTTP Client & Logging Interceptor |
| **Serialization** | Gson Converter | JSON parser ke Kotlin Data Class |
| **Asynchronous** | Kotlin Coroutines & Flow | Operasi latar belakang non-blocking dan reaktif |
| **State & Lifecycle** | Android Jetpack LiveData & ViewModel | State holder yang sadar siklus hidup Android |
| **Paginasi** | Jetpack Paging 3 | Infinite scrolling & pagination otomatis |
| **Penyimpanan Lokal** | Jetpack DataStore Preferences | Penyimpanan preferensi sesi pengguna (Token & Info Login) |
| **Pemuatan Gambar** | [Glide 4](https://github.com/bumptech/glide) | Image loading, caching, dan image transformation |
| **Lokasi & Peta** | Google Maps SDK & Google Location Services | `play-services-maps` & `play-services-location` |
| **View Binding** | Android View Binding | Mengakses komponen UI XML dengan aman (null-safe & type-safe) |
| **Testing** | JUnit4, Mockito, kotlinx-coroutines-test, Arch Core Testing | Pengujian unit ViewModel dan PagingSource |

---

## 🏛️ Arsitektur Aplikasi

Proyek ini menerapkan pola arsitektur **MVVM (Model - View - ViewModel)** dengan **Repository Pattern**:

```
 ┌────────────────────────────────────────┐
 │            UI Layer (View)             │
 │  Activity / Fragment / Custom Views   │
 └───────────────────▲────────────────────┘
                     │ (Observes LiveData / State)
 ┌───────────────────┴────────────────────┐
 │            ViewModel Layer             │
 │  MainViewModel, MapsViewModel, dll     │
 └───────────────────▲────────────────────┘
                     │ (Calls Repositories)
 ┌───────────────────┴────────────────────┐
 │            Repository Layer            │
 │             UserRepository             │
 └─────────▲────────────────────▲─────────┘
           │                    │
┌──────────┴──────────┐  ┌──────┴───────────────┐
│     Data Source     │  │     Data Source      │
│ Remote: ApiService  │  │ Local: UserPreference│
│    (Retrofit)       │  │ (DataStore Prefs)    │
└─────────────────────┘  └──────────────────────┘
```

- **UI State Wrapper (`Hasil<T>`):** Representasi status eksekusi data yang terdiri dari `Loading`, `Success<T>`, dan `Error` untuk memudahkan visualisasi status pada antarmuka pengguna.
- **Dependency Injection Sederhana:** Modul `Injection.kt` dan `ViewModelFactory.kt` digunakan untuk instansiasi dependency repositori secara konsisten.

---

## 📂 Struktur Direktori

```text
SubmissionStoryApp/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── java/com/dicoding/storiin/
│   │   │   │   ├── response/          # Data class respons Retrofit (Story, Login, Reg, dll)
│   │   │   │   ├── service/           # Konfigurasi API, Service Interface, & Injection
│   │   │   │   │   ├── ApiConfig.kt
│   │   │   │   │   ├── ApiService.kt
│   │   │   │   │   └── Injection.kt
│   │   │   │   ├── user/              # Repositori, DataStore, PagingSource, & Utils
│   │   │   │   │   ├── Hasil.kt
│   │   │   │   │   ├── StoryPagingSource.kt
│   │   │   │   │   ├── UserModel.kt
│   │   │   │   │   ├── UserPreference.kt
│   │   │   │   │   ├── UserRepository.kt
│   │   │   │   │   └── Utils.kt
│   │   │   │   └── ui/                # Tampilan & ViewModel per fitur
│   │   │   │       ├── ViewModelFactory.kt
│   │   │   │       ├── detail/        # Halaman Detail Cerita
│   │   │   │       ├── main/          # Halaman Utama (Feed & PagingAdapter)
│   │   │   │       ├── maps/          # Halaman Peta Google Maps
│   │   │   │       ├── sign/          # Sign In, Sign Up, & Custom Edit Text
│   │   │   │       │   └── customview/
│   │   │   │       ├── upload/        # Halaman Upload Cerita (Kamera & Galeri)
│   │   │   │       └── welcome/       # Halaman Pembuka / Onboarding
│   │   │   └── res/
│   │   │       ├── layout/            # Layout XML (Activity & Item List)
│   │   │       ├── menu/              # Menu Toolbar
│   │   │       ├── raw/map_style.json # Kustomisasi Gaya Google Maps
│   │   │       └── values/            # String, Colors, Themes
│   │   └── test/                      # Unit Test
│   │       └── java/com/dicoding/storiin/
│   │           ├── Dummy.kt
│   │           ├── LiveDataTestUtil.kt
│   │           ├── MainDispatcherRule.kt
│   │           └── ui/main/MainViewModelTest.kt
│   └── build.gradle.kts
├── gradle/
├── build.gradle.kts
├── gradle.properties
├── settings.gradle.kts
└── README.md
```

---

## ⚙️ Prasyarat & Konfigurasi

### Kebutuhan Perangkat Lunak:
- **Android Studio** (Hedgehog 2023.1.1 atau versi lebih baru direkomendasikan)
- **JDK 17** atau **JDK 1.8+**
- **Android SDK** API Level 34 (Android 14)
- Koneksi Internet untuk sinkronisasi dependencies Gradle & akses REST API

### 1. Konfigurasi Google Maps API Key
Aplikasi ini memanfaatkan Google Maps Platform. Agar peta dapat dimuat dengan baik:

1. Buat API Key di [Google Cloud Console](https://console.cloud.google.com/).
2. Aktifkan **Maps SDK for Android**.
3. Buka file `local.properties` pada root proyek, lalu tambahkan API Key Anda:
   ```properties
   MAPS_API_KEY=YOUR_GOOGLE_MAPS_API_KEY
   ```
   *Catatan: Pastikan juga metadata API Key pada `AndroidManifest.xml` telah sesuai dengan konfigurasi Secrets Gradle Plugin.*

### 2. Base URL API
Base URL API didefinisikan secara default di `app/build.gradle.kts`:
```kotlin
buildConfigField("String", "BASEURL", "\"https://story-api.dicoding.dev/v1/\"")
```

---

## 🚀 Cara Menjalankan Aplikasi

1. **Clone repositori ini:**
   ```bash
   git clone https://github.com/ean310/SubmissionStoryApp.git
   cd SubmissionStoryApp
   ```

2. **Buka proyek di Android Studio:**
   - Pilih menu **File** > **Open...**
   - Arahkan ke folder repositori yang telah di-clone.

3. **Sinkronisasi Gradle:**
   - Tunggu hingga Android Studio menyelesaikan proses *Gradle Sync*.

4. **Jalankan Aplikasi:**
   - Pilih perangkat target (Emulator atau Device Fisik dengan Developer Options aktif).
   - Klik tombol **Run** (`Shift + F10`) atau panah hijau di toolbar atas.

---

## 🧪 Pengujian (Unit Testing)

Proyek ini telah dilengkapi dengan Unit Test untuk menguji fungsionalitas pengambilan data pada `MainViewModel` dengan **Paging 3**:

- **Kasus Uji 1:** Memastikan saat data cerita berhasil dimuat, data tidak bernilai `null`, jumlah item sesuai dummy, dan data pertama sesuai data yang diharapkan.
- **Kasus Uji 2:** Memastikan saat data cerita kosong, Paging Data mengembalikan 0 item tanpa error.

### Menjalankan Unit Test via Gradle CLI:
```bash
./gradlew testDebugUnitTest
```
Atau melalui Android Studio dengan klik kanan pada direktori `app/src/test` lalu pilih **Run 'Tests in 'test''**.

---

## 🌐 Daftar Endpoint API yang Digunakan

Aplikasi terhubung ke **Dicoding Story API v1**:

| Method | Endpoint | Deskripsi | Auth |
| :---: | :--- | :--- | :---: |
| `POST` | `/v1/register` | Mendaftarkan akun baru | ❌ |
| `POST` | `/v1/login` | Masuk dan memperoleh token autentikasi | ❌ |
| `GET` | `/v1/stories` | Mengambil daftar cerita (Mendukung query `page` & `size`) | ✅ Bearer |
| `GET` | `/v1/stories/{id}` | Mengambil data detail cerita berdasarkan ID | ✅ Bearer |
| `POST` | `/v1/stories` | Mengunggah cerita baru (`Multipart`: foto, deskripsi, lat, lon) | ✅ Bearer |
| `GET` | `/v1/stories?location=1` | Mengambil daftar cerita yang memiliki data koordinat lokasi | ✅ Bearer |

---

## 👨‍💻 Kontributor & Lisensi

- **Pengembang:** [Justean / ean310](https://github.com/ean310)
- **Program:** Bangkit Academy — Google, GoTo, Traveloka
- **Course:** Belajar Pengembangan Aplikasi Android Intermediate (Dicoding Indonesia)

---
*Dibuat dengan dedikasi untuk pembelajaran pengembangan aplikasi Android berstandar industri.*
