# Bayt al-Hikmah

**Bayt al-Hikmah** (House of Wisdom) is an offline-first vocabulary trainer for **Ancient Greek** and **Classical Latin**. It is built for students and researchers who want a compact core vocabulary, systematic repetition, and grammar-aware testing without an internet connection.

Current release: **V1.2 Dogfood**.

## Features

- **Core dictionaries** — 1,030 Ancient Greek words and 3,000 Latin words, with 12,090 constructed example sentences.
- **Daily learning** — random-order new-word cards, examples, group quizzes, and a post-learning test.
- **Two-part test pages**
  - A form/word is shown; choose its meaning, then identify its case/number/gender or tense/person/voice/mood.
  - Cloze questions present a sentence with a blank; the options are different forms of the target word (or same-part-of-speech words for indeclinables).
- **Reference** — search by headword, meaning, or inflected form; view forms, examples, and source attribution.
- **Saved words and random tests** — build a personal test set or review learned vocabulary.
- **Offline storage** — Room database and DataStore keep all vocabulary and progress on the device.
- **Appearance** — light/dark theme and selectable light/dark launcher icon.
- **Import/export** — JSON vocabulary packs and full progress backup/restore.

## A normal study session

1. Open **Learn** and choose today's target (10–50 words).
2. Study each card: headword, meaning, forms, and three examples.
3. Complete the group quiz and the post-learning test.
4. Finish the daily review or take an optional random test.
5. Use **Reference** for lookup and **Saved Words** for focused practice.

A short in-app guide is shown on first launch and can be reopened from **Profile → Library and settings → How to use this app**.

## Data sources

- Greek core: Wilfred E. Major, *CPL Online* 4 (2008), 80% vocabulary list; high-frequency LXX/New Testament lemmas; CATSS Septuagint morphology and MorphGNT SBLGNT for forms.
- Latin core: Paul B. Diederich, *The Frequency of Latin Words and Their Endings* (1939); Dickinson College Commentaries Latin Core Vocabulary; OCR GCSE and Leaving Certificate lists; Lewis & Short for glosses.
- Greek glosses: Liddell–Scott–Jones via the Perseus Project / LSJLogeion.
- Example sentences are constructed from the core vocabulary. They are not quotations unless marked as authentic.
- See **Profile → Dictionary information** for the sources, licences, and coverage shipped with the app.

The application stores no user data online. Learning records are stored in the app's private Room database.

## Building

Requirements:

- JDK 21
- Android SDK 35
- Gradle 8.11.1 (the repository includes the Gradle wrapper)

```bash
export JAVA_HOME=/path/to/jdk-21
./gradlew :app:assembleDebug
```

The debug APK is written to:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Install on a connected device or emulator:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Project structure

```text
app/src/main/java/com/baytalhikmah/
  data/        Room entities, DAOs, repositories, seeding, import/export
  domain/      morphology, question generation, search normalisation
  ui/          Compose screens, view models, navigation, theme
app/src/main/assets/dictionary/
  manifest.json
  greek.json
  latin.json
```

## Versions and source

Source code and APK releases:

<https://github.com/WenxuanXu-source/Bayt-al-Hikmah>

You are welcome to adapt the code and data for your own teaching, research, or personal learning needs.

## Contact

Wenxuan Xu  
E-mail: <bunkenjo@gmail.com>  
Website: <https://www.wenxuanxu.uk>

## License

The project is released under the **GNU General Public License v3.0 (GPL-3.0)**. See [LICENSE](LICENSE). Third-party dictionary and font data retain their own licences; see the in-app Dictionary information screen.

## Notes and limitations

- Imported core forms and constructed examples are under continuous review; corrections are welcome.
- Greek coverage is intentionally restricted to lemmas for which several reliable inflected forms could be sourced.
- The bundled EB Garamond font is used under the SIL Open Font License 1.1.
