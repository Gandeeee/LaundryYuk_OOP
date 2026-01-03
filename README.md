# LaundryYuk - Sistem Manajemen Laundry

Project ini adalah aplikasi web berbasis Java (JSP & Servlet) untuk manajemen operasional laundry, mencakup fitur pelanggan (pemesanan, pelacakan) dan admin (manajemen order, driver, laporan).

## Arsitektur & Teknologi
- **Bahasa:** Java (JDK 17/21)
- **Framework:** Native JSP & Servlet (MVC Architecture)
- **Database:** MySQL
- **IDE:** Apache NetBeans
- **Server:** Apache Tomcat (Built-in NetBeans)
- **Frontend:** HTML, CSS, Bootstrap 5, JavaScript

## Prasyarat Software
Pastikan software berikut sudah terinstall di laptop masing-masing:
1.  **Apache NetBeans** (Versi 12 ke atas).
2.  **Java Development Kit (JDK)** (Minimal versi 17).
3.  **Local Server / Database:**
    -   **Windows:** XAMPP (Apache + MySQL).
    -   **Mac:** MAMP (Apache + MySQL).

---

## Panduan Instalasi (Cara Clone & Run)
Ikuti langkah-langkah ini secara berurutan agar tidak terjadi error koneksi database.

### 1. Clone Repository
Buka terminal atau Git Bash, lalu jalankan perintah:
git clone https://github.com/Gandeeee/LaundryYuk_OOP.git

### 2. Setup Database
Agar aplikasi berjalan, database lokal harus sesuai dengan struktur project.
1.  Buka **phpMyAdmin** (biasanya `http://localhost/phpmyadmin`).
2.  Buat database baru dengan nama: `db_laundry_yuk`
3.  Buka folder project hasil clone, cari folder bernama **`buatDBnyaDulu`**.
4.  Di dalam folder tersebut ada file `db_laundry_yuk.sql`.
5.  **Import** file `.sql` tersebut ke dalam database `db_laundry_yuk` yang baru dibuat.

### 3. Konfigurasi Koneksi (PENTING!)
Setiap laptop memiliki konfigurasi port dan password database yang berbeda (Mac vs Windows). Anda harus menyesuaikannya.

1.  Buka NetBeans, buka file:
    `src/java/com/laundryyuk/config/DatabaseConnection.java`
2.  Perhatikan bagian variabel `URL`, `USER`, dan `PASSWORD`.
3.  Sesuaikan dengan konfigurasi laptop Anda:

**JIKA PENGGUNA WINDOWS (XAMPP):**
Gunakan konfigurasi berikut (Port 3306, Password kosong):
private static final String URL = "jdbc:mysql://localhost:3306/db_laundry_yuk?useSSL=false&serverTimezone=UTC";
private static final String USER = "root";
private static final String PASSWORD = "";

**JIKA PENGGUNA MAC (MAMP):**
Gunakan konfigurasi berikut (Port 8889, Password root):
private static final String URL = "jdbc:mysql://localhost:8889/db_laundry_yuk?useSSL=false&serverTimezone=UTC";
private static final String USER = "root";
private static final String PASSWORD = "root";

### 4. Build & Run
1.  Di NetBeans, klik kanan pada nama project **LaundryYuk**.
2.  Pilih **Clean and Build**.
3.  Pastikan tidak ada error di output.
4.  Klik tombol **Run** (Play Hijau) atau tekan F6.
5.  Browser akan terbuka otomatis mengarah ke halaman login.

---

## Troubleshooting Umum

**Error: "Driver JDBC tidak ditemukan"**
- Pastikan library `mysql-connector-j-x.x.x.jar` sudah ada di folder `Libraries` pada NetBeans. Jika hilang, klik kanan `Libraries` -> `Add JAR/Folder` -> Pilih file jar MySQL Connector.

**Error: "Communication Link Failure"**
- Cek apakah XAMPP/MAMP MySQL sudah dinyalakan?
- Cek apakah PORT di `DatabaseConnection.java` sudah sesuai (3306 vs 8889)?

**Error: "Unknown Database"**
- Pastikan Anda sudah membuat database dengan nama persis `db_laundry_yuk` (huruf kecil semua).

---

## Akun Demo
(Cek tabel `users` dan `admins` di database untuk data terbaru)
- **Admin:** Cek tabel `admins`
- **Customer:** Cek tabel `users` dengan role `CUSTOMER`
