# اپلیکیشن دنیای سرگرمی (Entertainment World)

اپلیکیشن اندرویدی «دنیای سرگرمی» یک برنامه جامع و جذاب ساخته‌شده با **Kotlin** و **Jetpack Compose** است که شامل مجموعه‌ای از داستان‌های آموزنده (از جمله داستان‌های سایت ناهید جلالی و هزار و یک شب)، داستان‌های حماسی شاهنامه، داستان‌های طنز ملانصرالدین، چیستان‌ها، لطیفه‌ها و دانستنی‌های علمی است.

---

## 🌟 ویژگی‌های کلیدی برنامه (Features)

- 📖 **پایگاه داده محلی (Room Database)**: ذخیره‌سازی آفلاین تمامی داستان‌ها، چیستان‌ها و لطیفه‌ها.
- 📚 **دسته‌بندی داستان‌های آموزنده**: شامل مجموعه داستان‌های آموزنده و پندآموز سایت ناهید جلالی، هزار و یک شب و حکایات کهن.
- 🎨 **رابط کاربری مدرن Material 3**: طراحی شده با Jetpack Compose، همراه با تم‌های شیشه‌ای (Glassmorphic) و پشتیبانی از حالت شب و روز.
- 🔍 **جستجوی پیشرفته**: قابلیت جستجوی سریع در عنوان و متن کلیه داستان‌ها.
- ❤️ **لیست علاقه‌مندی‌ها**: امکان علامت‌گذاری داستان‌های محبوب و دسترسی سریع به آن‌ها.
- ⚙️ **تنظیمات فونت و اندازه متن**: قابلیت تغییر فونت (وزیر، ایران‌سنس و...) و سایز قلم جهت خوانایی بهتر.
- 📲 **اشتراک‌گذاری و کپی متن**: امکان کپی دکمه‌ای متن داخل کادر و اشتراک‌گذاری در شبکه‌های اجتماعی.
- 📱 **پشتیبانی کامل از حالت عمودی (Portrait)**.

---

## 🛠️ تکنولوژی‌ها و معماری (Tech Stack)

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: MVVM (Model-View-ViewModel) + Repository Pattern
- **Local Database**: Room Persistence Library with KSP
- **Async & Reactive Data**: Kotlin Coroutines & Flow / StateFlow
- **Navigation**: Jetpack Navigation Compose

---

## 🚀 راهنمای ساخت و اجرا (Build & Run)

### پیش‌نیازها
- Android Studio Hedgehog یا نسخه‌های جدیدتر
- JDK 17 یا بالاتر
- Android SDK 34 (Android 14)

### نحوه خروجی گرفتن و ثبت در گیت‌هاب (GitHub Export)

1. **کلون کردن یا آپلود پروژه روی گیت‌هاب**:
   ```bash
   git init
   git add .
   git commit -m "Initial commit - Entertainment World App with Room DB"
   git branch -M main
   git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPO_NAME.git
   git push -u origin main
   ```

2. **ساخت فایل APK نهایی در اندروید استودیو**:
   - در Android Studio از منوی بالا گزینه **Build > Build Bundle(s) / APK(s) > Build APK(s)** را انتخاب کنید.
   - فایل APK در مسیر `app/build/outputs/apk/debug/app-debug.apk` تولید می‌شود.

---

## 📜 مجوز (License)
توسعه داده شده برای اپلیکیشن «دنیای سرگرمی».
