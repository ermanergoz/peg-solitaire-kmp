# Localization Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add multi-language support to both Android and iOS using Compose Multiplatform Resources in the shared module with 19 languages and 22 strings.

**Architecture:** String resources in `shared/src/commonMain/composeResources/values-XX/strings.xml`. Android uses `stringResource(Res.string.xxx)`. iOS uses a `StringHelper` bridge in the shared module. Compose multiplatform plugin added to the shared module.

**Tech Stack:** Compose Multiplatform Resources, KMP shared module, Android Compose `stringResource()`, Swift helper bridge.

**Spec:** `docs/superpowers/specs/2026-03-21-localization-design.md`

---

### Task 1: Add compose resources to shared module

**Files:**
- Modify: `shared/build.gradle.kts`
- Create: `shared/src/commonMain/composeResources/values/strings.xml`

- [ ] **Step 1: Add compose plugins to shared/build.gradle.kts**

Add `composeMultiplatform` and `composeCompiler` plugins and `compose-components-resources` dependency:

```kotlin
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}
```

Add to `commonMain.dependencies`:
```kotlin
implementation(libs.compose.components.resources)
```

- [ ] **Step 2: Create default English strings.xml**

Create `shared/src/commonMain/composeResources/values/strings.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_title">Peg Solitaire</string>
    <string name="challenge_mode">Challenge Mode</string>
    <string name="classic_mode">Classic Mode</string>
    <string name="play">Play</string>
    <string name="browse_all_levels">Browse All Levels</string>
    <string name="level_n">Level %1$d</string>
    <string name="settings">Settings</string>
    <string name="sound_effects">Sound Effects</string>
    <string name="haptic_feedback">Haptic Feedback</string>
    <string name="reset_all_scores">Reset All Scores</string>
    <string name="reset_all_scores_confirm">This will permanently delete all your classic mode best scores and challenge mode progress.</string>
    <string name="reset">Reset</string>
    <string name="cancel">Cancel</string>
    <string name="challenge_levels">Challenge Levels</string>
    <string name="game_over">Game Over</string>
    <string name="quit">Quit</string>
    <string name="restart">Restart</string>
    <string name="next">Next</string>
    <string name="retry">Retry</string>
    <string name="not_played">Not played</string>
    <string name="generic_error">Something went wrong. Please try again.</string>
    <string name="score_left">%1$d left</string>
</resources>
```

- [ ] **Step 3: Verify build**

Run: `./gradlew :shared:compileDebugKotlinAndroid`
Expected: BUILD SUCCESSFUL — compose resources plugin generates `Res` class.

- [ ] **Step 4: Commit**

```
Add compose resources to shared module with English strings
```

---

### Task 2: Create all 18 translation files

**Files:**
- Create: 18 `strings.xml` files in `shared/src/commonMain/composeResources/values-XX/`

- [ ] **Step 1: Create all translation files**

Create each file under `shared/src/commonMain/composeResources/`:

Directory names:
- `values-es/strings.xml` — Spanish
- `values-pt-rBR/strings.xml` — Portuguese (Brazil)
- `values-fr/strings.xml` — French
- `values-de/strings.xml` — German
- `values-ja/strings.xml` — Japanese
- `values-ko/strings.xml` — Korean
- `values-zh-rCN/strings.xml` — Chinese Simplified
- `values-tr/strings.xml` — Turkish
- `values-it/strings.xml` — Italian
- `values-ru/strings.xml` — Russian
- `values-ar/strings.xml` — Arabic
- `values-hi/strings.xml` — Hindi
- `values-in/strings.xml` — Indonesian
- `values-az/strings.xml` — Azerbaijani
- `values-kk/strings.xml` — Kazakh
- `values-uz/strings.xml` — Uzbek
- `values-tk/strings.xml` — Turkmen
- `values-ky/strings.xml` — Kyrgyz

Each file has the same 22 `<string>` keys with translated values. Format placeholders (`%1$d`) must be preserved exactly.

Here are all the translations:

**Spanish (es):**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_title">Peg Solitaire</string>
    <string name="challenge_mode">Modo Desafío</string>
    <string name="classic_mode">Modo Clásico</string>
    <string name="play">Jugar</string>
    <string name="browse_all_levels">Ver Todos los Niveles</string>
    <string name="level_n">Nivel %1$d</string>
    <string name="settings">Ajustes</string>
    <string name="sound_effects">Efectos de Sonido</string>
    <string name="haptic_feedback">Retroalimentación Háptica</string>
    <string name="reset_all_scores">Restablecer Puntuaciones</string>
    <string name="reset_all_scores_confirm">Esto eliminará permanentemente todas tus mejores puntuaciones del modo clásico y el progreso del modo desafío.</string>
    <string name="reset">Restablecer</string>
    <string name="cancel">Cancelar</string>
    <string name="challenge_levels">Niveles de Desafío</string>
    <string name="game_over">Fin del Juego</string>
    <string name="quit">Salir</string>
    <string name="restart">Reiniciar</string>
    <string name="next">Siguiente</string>
    <string name="retry">Reintentar</string>
    <string name="not_played">Sin jugar</string>
    <string name="generic_error">Algo salió mal. Inténtalo de nuevo.</string>
    <string name="score_left">%1$d restantes</string>
</resources>
```

**Portuguese Brazil (pt-rBR):**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_title">Peg Solitaire</string>
    <string name="challenge_mode">Modo Desafio</string>
    <string name="classic_mode">Modo Clássico</string>
    <string name="play">Jogar</string>
    <string name="browse_all_levels">Ver Todos os Níveis</string>
    <string name="level_n">Nível %1$d</string>
    <string name="settings">Configurações</string>
    <string name="sound_effects">Efeitos Sonoros</string>
    <string name="haptic_feedback">Feedback Tátil</string>
    <string name="reset_all_scores">Redefinir Pontuações</string>
    <string name="reset_all_scores_confirm">Isso excluirá permanentemente todas as suas melhores pontuações do modo clássico e o progresso do modo desafio.</string>
    <string name="reset">Redefinir</string>
    <string name="cancel">Cancelar</string>
    <string name="challenge_levels">Níveis de Desafio</string>
    <string name="game_over">Fim de Jogo</string>
    <string name="quit">Sair</string>
    <string name="restart">Reiniciar</string>
    <string name="next">Próximo</string>
    <string name="retry">Tentar Novamente</string>
    <string name="not_played">Não jogado</string>
    <string name="generic_error">Algo deu errado. Tente novamente.</string>
    <string name="score_left">%1$d restantes</string>
</resources>
```

**French (fr):**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_title">Peg Solitaire</string>
    <string name="challenge_mode">Mode Défi</string>
    <string name="classic_mode">Mode Classique</string>
    <string name="play">Jouer</string>
    <string name="browse_all_levels">Voir Tous les Niveaux</string>
    <string name="level_n">Niveau %1$d</string>
    <string name="settings">Paramètres</string>
    <string name="sound_effects">Effets Sonores</string>
    <string name="haptic_feedback">Retour Haptique</string>
    <string name="reset_all_scores">Réinitialiser les Scores</string>
    <string name="reset_all_scores_confirm">Cela supprimera définitivement tous vos meilleurs scores du mode classique et la progression du mode défi.</string>
    <string name="reset">Réinitialiser</string>
    <string name="cancel">Annuler</string>
    <string name="challenge_levels">Niveaux de Défi</string>
    <string name="game_over">Partie Terminée</string>
    <string name="quit">Quitter</string>
    <string name="restart">Recommencer</string>
    <string name="next">Suivant</string>
    <string name="retry">Réessayer</string>
    <string name="not_played">Non joué</string>
    <string name="generic_error">Une erreur est survenue. Veuillez réessayer.</string>
    <string name="score_left">%1$d restants</string>
</resources>
```

**German (de):**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_title">Peg Solitaire</string>
    <string name="challenge_mode">Herausforderungsmodus</string>
    <string name="classic_mode">Klassischer Modus</string>
    <string name="play">Spielen</string>
    <string name="browse_all_levels">Alle Level Ansehen</string>
    <string name="level_n">Level %1$d</string>
    <string name="settings">Einstellungen</string>
    <string name="sound_effects">Soundeffekte</string>
    <string name="haptic_feedback">Haptisches Feedback</string>
    <string name="reset_all_scores">Punktzahlen Zurücksetzen</string>
    <string name="reset_all_scores_confirm">Dadurch werden alle Ihre Bestleistungen im klassischen Modus und der Fortschritt im Herausforderungsmodus dauerhaft gelöscht.</string>
    <string name="reset">Zurücksetzen</string>
    <string name="cancel">Abbrechen</string>
    <string name="challenge_levels">Herausforderungslevel</string>
    <string name="game_over">Spiel Vorbei</string>
    <string name="quit">Beenden</string>
    <string name="restart">Neustart</string>
    <string name="next">Weiter</string>
    <string name="retry">Erneut Versuchen</string>
    <string name="not_played">Nicht gespielt</string>
    <string name="generic_error">Etwas ist schiefgelaufen. Bitte versuchen Sie es erneut.</string>
    <string name="score_left">%1$d übrig</string>
</resources>
```

**Japanese (ja):**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_title">Peg Solitaire</string>
    <string name="challenge_mode">チャレンジモード</string>
    <string name="classic_mode">クラシックモード</string>
    <string name="play">プレイ</string>
    <string name="browse_all_levels">全レベルを見る</string>
    <string name="level_n">レベル %1$d</string>
    <string name="settings">設定</string>
    <string name="sound_effects">効果音</string>
    <string name="haptic_feedback">触覚フィードバック</string>
    <string name="reset_all_scores">スコアをリセット</string>
    <string name="reset_all_scores_confirm">クラシックモードのベストスコアとチャレンジモードの進行状況がすべて完全に削除されます。</string>
    <string name="reset">リセット</string>
    <string name="cancel">キャンセル</string>
    <string name="challenge_levels">チャレンジレベル</string>
    <string name="game_over">ゲームオーバー</string>
    <string name="quit">終了</string>
    <string name="restart">再スタート</string>
    <string name="next">次へ</string>
    <string name="retry">再試行</string>
    <string name="not_played">未プレイ</string>
    <string name="generic_error">エラーが発生しました。もう一度お試しください。</string>
    <string name="score_left">残り %1$d</string>
</resources>
```

**Korean (ko):**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_title">Peg Solitaire</string>
    <string name="challenge_mode">챌린지 모드</string>
    <string name="classic_mode">클래식 모드</string>
    <string name="play">플레이</string>
    <string name="browse_all_levels">모든 레벨 보기</string>
    <string name="level_n">레벨 %1$d</string>
    <string name="settings">설정</string>
    <string name="sound_effects">효과음</string>
    <string name="haptic_feedback">햅틱 피드백</string>
    <string name="reset_all_scores">점수 초기화</string>
    <string name="reset_all_scores_confirm">클래식 모드의 모든 최고 점수와 챌린지 모드의 진행 상황이 영구적으로 삭제됩니다.</string>
    <string name="reset">초기화</string>
    <string name="cancel">취소</string>
    <string name="challenge_levels">챌린지 레벨</string>
    <string name="game_over">게임 오버</string>
    <string name="quit">종료</string>
    <string name="restart">다시 시작</string>
    <string name="next">다음</string>
    <string name="retry">재시도</string>
    <string name="not_played">미플레이</string>
    <string name="generic_error">문제가 발생했습니다. 다시 시도해 주세요.</string>
    <string name="score_left">%1$d 남음</string>
</resources>
```

**Chinese Simplified (zh-rCN):**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_title">Peg Solitaire</string>
    <string name="challenge_mode">挑战模式</string>
    <string name="classic_mode">经典模式</string>
    <string name="play">开始</string>
    <string name="browse_all_levels">浏览所有关卡</string>
    <string name="level_n">关卡 %1$d</string>
    <string name="settings">设置</string>
    <string name="sound_effects">音效</string>
    <string name="haptic_feedback">触觉反馈</string>
    <string name="reset_all_scores">重置所有分数</string>
    <string name="reset_all_scores_confirm">这将永久删除您所有经典模式的最高分和挑战模式的进度。</string>
    <string name="reset">重置</string>
    <string name="cancel">取消</string>
    <string name="challenge_levels">挑战关卡</string>
    <string name="game_over">游戏结束</string>
    <string name="quit">退出</string>
    <string name="restart">重新开始</string>
    <string name="next">下一关</string>
    <string name="retry">重试</string>
    <string name="not_played">未游玩</string>
    <string name="generic_error">出了点问题，请重试。</string>
    <string name="score_left">剩余 %1$d</string>
</resources>
```

**Turkish (tr):**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_title">Peg Solitaire</string>
    <string name="challenge_mode">Meydan Okuma Modu</string>
    <string name="classic_mode">Klasik Mod</string>
    <string name="play">Oyna</string>
    <string name="browse_all_levels">Tüm Seviyeleri Gör</string>
    <string name="level_n">Seviye %1$d</string>
    <string name="settings">Ayarlar</string>
    <string name="sound_effects">Ses Efektleri</string>
    <string name="haptic_feedback">Dokunsal Geri Bildirim</string>
    <string name="reset_all_scores">Tüm Skorları Sıfırla</string>
    <string name="reset_all_scores_confirm">Bu işlem klasik moddaki tüm en iyi skorlarınızı ve meydan okuma modundaki ilerlemenizi kalıcı olarak silecektir.</string>
    <string name="reset">Sıfırla</string>
    <string name="cancel">İptal</string>
    <string name="challenge_levels">Meydan Okuma Seviyeleri</string>
    <string name="game_over">Oyun Bitti</string>
    <string name="quit">Çık</string>
    <string name="restart">Yeniden Başla</string>
    <string name="next">Sonraki</string>
    <string name="retry">Tekrar Dene</string>
    <string name="not_played">Oynanmadı</string>
    <string name="generic_error">Bir şeyler ters gitti. Lütfen tekrar deneyin.</string>
    <string name="score_left">%1$d kaldı</string>
</resources>
```

**Italian (it):**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_title">Peg Solitaire</string>
    <string name="challenge_mode">Modalità Sfida</string>
    <string name="classic_mode">Modalità Classica</string>
    <string name="play">Gioca</string>
    <string name="browse_all_levels">Vedi Tutti i Livelli</string>
    <string name="level_n">Livello %1$d</string>
    <string name="settings">Impostazioni</string>
    <string name="sound_effects">Effetti Sonori</string>
    <string name="haptic_feedback">Feedback Aptico</string>
    <string name="reset_all_scores">Reimposta Punteggi</string>
    <string name="reset_all_scores_confirm">Questo eliminerà permanentemente tutti i tuoi migliori punteggi della modalità classica e i progressi della modalità sfida.</string>
    <string name="reset">Reimposta</string>
    <string name="cancel">Annulla</string>
    <string name="challenge_levels">Livelli di Sfida</string>
    <string name="game_over">Partita Finita</string>
    <string name="quit">Esci</string>
    <string name="restart">Ricomincia</string>
    <string name="next">Avanti</string>
    <string name="retry">Riprova</string>
    <string name="not_played">Non giocato</string>
    <string name="generic_error">Qualcosa è andato storto. Riprova.</string>
    <string name="score_left">%1$d rimasti</string>
</resources>
```

**Russian (ru):**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_title">Peg Solitaire</string>
    <string name="challenge_mode">Режим Испытания</string>
    <string name="classic_mode">Классический Режим</string>
    <string name="play">Играть</string>
    <string name="browse_all_levels">Все Уровни</string>
    <string name="level_n">Уровень %1$d</string>
    <string name="settings">Настройки</string>
    <string name="sound_effects">Звуковые Эффекты</string>
    <string name="haptic_feedback">Тактильная Отдача</string>
    <string name="reset_all_scores">Сбросить Все Очки</string>
    <string name="reset_all_scores_confirm">Это навсегда удалит все ваши лучшие результаты в классическом режиме и прогресс в режиме испытания.</string>
    <string name="reset">Сбросить</string>
    <string name="cancel">Отмена</string>
    <string name="challenge_levels">Уровни Испытания</string>
    <string name="game_over">Игра Окончена</string>
    <string name="quit">Выход</string>
    <string name="restart">Заново</string>
    <string name="next">Далее</string>
    <string name="retry">Повторить</string>
    <string name="not_played">Не сыграно</string>
    <string name="generic_error">Что-то пошло не так. Попробуйте ещё раз.</string>
    <string name="score_left">Осталось %1$d</string>
</resources>
```

**Arabic (ar):**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_title">Peg Solitaire</string>
    <string name="challenge_mode">وضع التحدي</string>
    <string name="classic_mode">الوضع الكلاسيكي</string>
    <string name="play">العب</string>
    <string name="browse_all_levels">تصفح جميع المستويات</string>
    <string name="level_n">المستوى %1$d</string>
    <string name="settings">الإعدادات</string>
    <string name="sound_effects">المؤثرات الصوتية</string>
    <string name="haptic_feedback">الاستجابة اللمسية</string>
    <string name="reset_all_scores">إعادة تعيين النتائج</string>
    <string name="reset_all_scores_confirm">سيؤدي هذا إلى حذف جميع أفضل نتائجك في الوضع الكلاسيكي وتقدمك في وضع التحدي بشكل دائم.</string>
    <string name="reset">إعادة تعيين</string>
    <string name="cancel">إلغاء</string>
    <string name="challenge_levels">مستويات التحدي</string>
    <string name="game_over">انتهت اللعبة</string>
    <string name="quit">خروج</string>
    <string name="restart">إعادة</string>
    <string name="next">التالي</string>
    <string name="retry">إعادة المحاولة</string>
    <string name="not_played">لم يُلعب</string>
    <string name="generic_error">حدث خطأ ما. يرجى المحاولة مرة أخرى.</string>
    <string name="score_left">متبقي %1$d</string>
</resources>
```

**Hindi (hi):**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_title">Peg Solitaire</string>
    <string name="challenge_mode">चुनौती मोड</string>
    <string name="classic_mode">क्लासिक मोड</string>
    <string name="play">खेलें</string>
    <string name="browse_all_levels">सभी स्तर देखें</string>
    <string name="level_n">स्तर %1$d</string>
    <string name="settings">सेटिंग्स</string>
    <string name="sound_effects">ध्वनि प्रभाव</string>
    <string name="haptic_feedback">हैप्टिक फीडबैक</string>
    <string name="reset_all_scores">सभी स्कोर रीसेट करें</string>
    <string name="reset_all_scores_confirm">यह आपके क्लासिक मोड के सभी सर्वश्रेष्ठ स्कोर और चुनौती मोड की प्रगति को स्थायी रूप से हटा देगा।</string>
    <string name="reset">रीसेट</string>
    <string name="cancel">रद्द करें</string>
    <string name="challenge_levels">चुनौती स्तर</string>
    <string name="game_over">खेल समाप्त</string>
    <string name="quit">बाहर</string>
    <string name="restart">पुनः आरंभ</string>
    <string name="next">अगला</string>
    <string name="retry">पुनः प्रयास</string>
    <string name="not_played">नहीं खेला</string>
    <string name="generic_error">कुछ गलत हो गया। कृपया पुनः प्रयास करें।</string>
    <string name="score_left">%1$d शेष</string>
</resources>
```

**Indonesian (in):**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_title">Peg Solitaire</string>
    <string name="challenge_mode">Mode Tantangan</string>
    <string name="classic_mode">Mode Klasik</string>
    <string name="play">Main</string>
    <string name="browse_all_levels">Lihat Semua Level</string>
    <string name="level_n">Level %1$d</string>
    <string name="settings">Pengaturan</string>
    <string name="sound_effects">Efek Suara</string>
    <string name="haptic_feedback">Umpan Balik Haptik</string>
    <string name="reset_all_scores">Atur Ulang Semua Skor</string>
    <string name="reset_all_scores_confirm">Ini akan menghapus secara permanen semua skor terbaik mode klasik dan kemajuan mode tantangan Anda.</string>
    <string name="reset">Atur Ulang</string>
    <string name="cancel">Batal</string>
    <string name="challenge_levels">Level Tantangan</string>
    <string name="game_over">Permainan Selesai</string>
    <string name="quit">Keluar</string>
    <string name="restart">Mulai Ulang</string>
    <string name="next">Berikutnya</string>
    <string name="retry">Coba Lagi</string>
    <string name="not_played">Belum dimainkan</string>
    <string name="generic_error">Terjadi kesalahan. Silakan coba lagi.</string>
    <string name="score_left">%1$d tersisa</string>
</resources>
```

**Azerbaijani (az):**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_title">Peg Solitaire</string>
    <string name="challenge_mode">Çağırış Rejimi</string>
    <string name="classic_mode">Klassik Rejim</string>
    <string name="play">Oyna</string>
    <string name="browse_all_levels">Bütün Səviyyələrə Bax</string>
    <string name="level_n">Səviyyə %1$d</string>
    <string name="settings">Tənzimləmələr</string>
    <string name="sound_effects">Səs Effektləri</string>
    <string name="haptic_feedback">Toxunma Əks-əlaqəsi</string>
    <string name="reset_all_scores">Bütün Xalları Sıfırla</string>
    <string name="reset_all_scores_confirm">Bu, klassik rejimdəki bütün ən yaxşı xallarınızı və çağırış rejimindəki irəliləyişinizi həmişəlik siləcək.</string>
    <string name="reset">Sıfırla</string>
    <string name="cancel">Ləğv Et</string>
    <string name="challenge_levels">Çağırış Səviyyələri</string>
    <string name="game_over">Oyun Bitdi</string>
    <string name="quit">Çıx</string>
    <string name="restart">Yenidən Başla</string>
    <string name="next">Növbəti</string>
    <string name="retry">Yenidən Cəhd Et</string>
    <string name="not_played">Oynanılmayıb</string>
    <string name="generic_error">Xəta baş verdi. Zəhmət olmasa yenidən cəhd edin.</string>
    <string name="score_left">%1$d qaldı</string>
</resources>
```

**Kazakh (kk):**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_title">Peg Solitaire</string>
    <string name="challenge_mode">Сынақ Режимі</string>
    <string name="classic_mode">Классикалық Режим</string>
    <string name="play">Ойнау</string>
    <string name="browse_all_levels">Барлық Деңгейлерді Көру</string>
    <string name="level_n">Деңгей %1$d</string>
    <string name="settings">Баптаулар</string>
    <string name="sound_effects">Дыбыс Әсерлері</string>
    <string name="haptic_feedback">Сенсорлық Кері Байланыс</string>
    <string name="reset_all_scores">Барлық Ұпайларды Қалпына Келтіру</string>
    <string name="reset_all_scores_confirm">Бұл классикалық режимдегі барлық жақсы нәтижелеріңізді және сынақ режиміндегі ілгерілеуіңізді біржола жояды.</string>
    <string name="reset">Қалпына Келтіру</string>
    <string name="cancel">Бас Тарту</string>
    <string name="challenge_levels">Сынақ Деңгейлері</string>
    <string name="game_over">Ойын Аяқталды</string>
    <string name="quit">Шығу</string>
    <string name="restart">Қайта Бастау</string>
    <string name="next">Келесі</string>
    <string name="retry">Қайта Қайталау</string>
    <string name="not_played">Ойналмаған</string>
    <string name="generic_error">Бірдеңе дұрыс болмады. Қайта көріңіз.</string>
    <string name="score_left">%1$d қалды</string>
</resources>
```

**Uzbek (uz):**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_title">Peg Solitaire</string>
    <string name="challenge_mode">Sinov Rejimi</string>
    <string name="classic_mode">Klassik Rejim</string>
    <string name="play">O\'ynash</string>
    <string name="browse_all_levels">Barcha Darajalarni Ko\'rish</string>
    <string name="level_n">Daraja %1$d</string>
    <string name="settings">Sozlamalar</string>
    <string name="sound_effects">Tovush Effektlari</string>
    <string name="haptic_feedback">Taktil Qaytaloq</string>
    <string name="reset_all_scores">Barcha Ballarni Tiklash</string>
    <string name="reset_all_scores_confirm">Bu klassik rejimdagi barcha eng yaxshi ballaringizni va sinov rejimidagi taraqqiyotingizni butunlay o\'chiradi.</string>
    <string name="reset">Tiklash</string>
    <string name="cancel">Bekor Qilish</string>
    <string name="challenge_levels">Sinov Darajalari</string>
    <string name="game_over">O\'yin Tugadi</string>
    <string name="quit">Chiqish</string>
    <string name="restart">Qayta Boshlash</string>
    <string name="next">Keyingi</string>
    <string name="retry">Qayta Urinish</string>
    <string name="not_played">O\'ynalmagan</string>
    <string name="generic_error">Xatolik yuz berdi. Iltimos, qayta urinib ko\'ring.</string>
    <string name="score_left">%1$d qoldi</string>
</resources>
```

**Turkmen (tk):**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_title">Peg Solitaire</string>
    <string name="challenge_mode">Synag Rejesi</string>
    <string name="classic_mode">Klassyk Rejem</string>
    <string name="play">Oýna</string>
    <string name="browse_all_levels">Ähli Derejeleri Gör</string>
    <string name="level_n">Dereje %1$d</string>
    <string name="settings">Sazlamalar</string>
    <string name="sound_effects">Ses Effektleri</string>
    <string name="haptic_feedback">Duýgy Jogaby</string>
    <string name="reset_all_scores">Ähli Ballary Täzele</string>
    <string name="reset_all_scores_confirm">Bu klassyk rejemdäki ähli iň gowy ballaryňyzy we synag rejimindäki öňe gidişligiňizi hemişelik öçürer.</string>
    <string name="reset">Täzele</string>
    <string name="cancel">Ýatyr</string>
    <string name="challenge_levels">Synag Derejeleri</string>
    <string name="game_over">Oýun Gutardy</string>
    <string name="quit">Çyk</string>
    <string name="restart">Täzeden Başla</string>
    <string name="next">Indiki</string>
    <string name="retry">Gaýtadan Synanyş</string>
    <string name="not_played">Oýnalmady</string>
    <string name="generic_error">Näsazlyk ýüze çykdy. Gaýtadan synanyşyň.</string>
    <string name="score_left">%1$d galdy</string>
</resources>
```

**Kyrgyz (ky):**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_title">Peg Solitaire</string>
    <string name="challenge_mode">Сыноо Режими</string>
    <string name="classic_mode">Классикалык Режим</string>
    <string name="play">Ойнотуу</string>
    <string name="browse_all_levels">Бардык Деңгээлдерди Көрүү</string>
    <string name="level_n">Деңгээл %1$d</string>
    <string name="settings">Орнотуулар</string>
    <string name="sound_effects">Үн Эффекттери</string>
    <string name="haptic_feedback">Тийүү Пикири</string>
    <string name="reset_all_scores">Бардык Упайларды Баштапкыга Келтирүү</string>
    <string name="reset_all_scores_confirm">Бул классикалык режимдеги бардык мыкты упайларыңызды жана сыноо режиминдеги прогрессиңизди биротоло жок кылат.</string>
    <string name="reset">Баштапкыга Келтирүү</string>
    <string name="cancel">Жокко Чыгаруу</string>
    <string name="challenge_levels">Сыноо Деңгээлдери</string>
    <string name="game_over">Оюн Бүттү</string>
    <string name="quit">Чыгуу</string>
    <string name="restart">Кайра Баштоо</string>
    <string name="next">Кийинки</string>
    <string name="retry">Кайра Аракет</string>
    <string name="not_played">Ойнолгон эмес</string>
    <string name="generic_error">Бир нерсе туура эмес болду. Кайра аракет кылыңыз.</string>
    <string name="score_left">%1$d калды</string>
</resources>
```

- [ ] **Step 2: Verify build**

Run: `./gradlew :shared:compileDebugKotlinAndroid`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```
Add translations for 18 languages
```

---

### Task 3: Create StringHelper bridge for iOS

**Files:**
- Create: `shared/src/commonMain/kotlin/com/erman/pegsolitaire/localization/StringHelper.kt`

- [ ] **Step 1: Create StringHelper**

```kotlin
package com.erman.pegsolitaire.localization

import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

object StringHelper {
    fun get(resource: StringResource): String =
        runBlocking { getString(resource) }

    fun get(resource: StringResource, arg: Int): String =
        runBlocking { getString(resource, arg) }
}
```

This bridges compose resources (suspend-based) to synchronous calls for iOS SwiftUI.

- [ ] **Step 2: Verify build**

Run: `./gradlew :shared:compileDebugKotlinAndroid`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```
Add StringHelper bridge for iOS string resource access
```

---

### Task 4: Wire localized strings into Android UI

**Files:**
- Modify: `composeApp/src/androidMain/kotlin/com/erman/pegsolitaire/ui/screen/MenuScreen.kt`
- Modify: `composeApp/src/androidMain/kotlin/com/erman/pegsolitaire/ui/screen/GameScreen.kt`
- Modify: `composeApp/src/androidMain/kotlin/com/erman/pegsolitaire/ui/screen/SettingsScreen.kt`
- Modify: `composeApp/src/androidMain/kotlin/com/erman/pegsolitaire/ui/screen/ChallengeLevelSelectorScreen.kt`
- Modify: `composeApp/src/androidMain/kotlin/com/erman/pegsolitaire/ui/component/GameOverDialog.kt`

- [ ] **Step 1: Replace all hardcoded strings with stringResource**

In every Android UI file, replace hardcoded strings with `stringResource(Res.string.xxx)`.

Add imports to each file:
```kotlin
import org.jetbrains.compose.resources.stringResource
import pegsolitaire.shared.generated.resources.Res
import pegsolitaire.shared.generated.resources.*
```

Replacements by file:

**MenuScreen.kt:**
- `"Peg Solitaire"` → `stringResource(Res.string.app_title)`
- `"Settings"` content description → `stringResource(Res.string.settings)`
- `"CHALLENGE MODE"` → `stringResource(Res.string.challenge_mode).uppercase()`
- `"Play"` → `stringResource(Res.string.play)`
- `"Browse All Levels →"` → `stringResource(Res.string.browse_all_levels)`
- `"Level $currentChallengeLevel"` → `stringResource(Res.string.level_n, currentChallengeLevel)`
- `"CLASSIC MODE"` → `stringResource(Res.string.classic_mode).uppercase()`
- `NOT_PLAYED_TEXT` constant → use `stringResource(Res.string.not_played)` inline
- Score format: `"${it.remainingPegs} left"` → `stringResource(Res.string.score_left, it.remainingPegs)`

Remove the `NOT_PLAYED_TEXT` constant. Keep `SCORE_MIDDLE_DOT` as-is (not translatable).

**GameScreen.kt:**
- `"Quit"` → `stringResource(Res.string.quit)`
- `"Retry"` → `stringResource(Res.string.retry)`

**SettingsScreen.kt:**
- `"Settings"` → `stringResource(Res.string.settings)`
- `"Sound Effects"` → `stringResource(Res.string.sound_effects)`
- `"Haptic Feedback"` → `stringResource(Res.string.haptic_feedback)`
- `"Reset All Scores"` (button) → `stringResource(Res.string.reset_all_scores)`
- `"Reset All Scores"` (alert title) → `stringResource(Res.string.reset_all_scores)`
- Alert message → `stringResource(Res.string.reset_all_scores_confirm)`
- `"Reset"` → `stringResource(Res.string.reset)`
- `"Cancel"` → `stringResource(Res.string.cancel)`

**ChallengeLevelSelectorScreen.kt:**
- `"Challenge Levels"` → `stringResource(Res.string.challenge_levels)`
- `"Retry"` → `stringResource(Res.string.retry)`

**GameOverDialog.kt:**
- `"Game Over"` → `stringResource(Res.string.game_over)`
- `"Quit"` → `stringResource(Res.string.quit)`
- `"Restart"` → `stringResource(Res.string.restart)`
- `"Next"` → `stringResource(Res.string.next)`

- [ ] **Step 2: Verify build**

Run: `./gradlew :composeApp:compileDebugKotlinAndroid`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```
Wire localized strings into Android UI
```

---

### Task 5: Wire localized strings into iOS UI

**Files:**
- Create: `iosApp/iosApp/Localization/StringHelper.swift`
- Modify: `iosApp/iosApp/Screen/MenuView.swift`
- Modify: `iosApp/iosApp/Screen/GameView.swift`
- Modify: `iosApp/iosApp/Screen/SettingsView.swift`
- Modify: `iosApp/iosApp/Screen/ChallengeLevelSelectorView.swift`

- [ ] **Step 1: Create Swift string helper**

Create `iosApp/iosApp/Localization/StringHelper.swift`:

```swift
import Shared

func str(_ resource: StringResource) -> String {
    return StringHelper.shared.get(resource: resource)
}

func strFormat(_ resource: StringResource, _ arg: Int32) -> String {
    return StringHelper.shared.get(resource: resource, arg: arg)
}
```

- [ ] **Step 2: Replace all hardcoded strings in iOS views**

Same replacements as Android but using `str(Res.shared.string.xxx)`:

**MenuView.swift:**
- `"Peg Solitaire"` → `str(Res.shared.string.app_title)`
- `"CHALLENGE MODE"` → `str(Res.shared.string.challenge_mode).uppercased()`
- `"Play"` → `str(Res.shared.string.play)`
- `"Browse All Levels →"` → `str(Res.shared.string.browse_all_levels)`
- Level text → `strFormat(Res.shared.string.level_n, currentChallengeLevel)`
- `"CLASSIC MODE"` → `str(Res.shared.string.classic_mode).uppercased()`
- `notPlayedText` constant → `str(Res.shared.string.not_played)` inline
- Score: `"\(score.remainingPegs) left"` → `strFormat(Res.shared.string.score_left, score.remainingPegs)`

**GameView.swift:**
- `"Game Over"` → `str(Res.shared.string.game_over)`
- `"Quit"` → `str(Res.shared.string.quit)`
- `"Restart"` → `str(Res.shared.string.restart)`
- `"Next"` → `str(Res.shared.string.next)`

**SettingsView.swift:**
- `"Settings"` → `str(Res.shared.string.settings)`
- `"Sound Effects"` → `str(Res.shared.string.sound_effects)`
- `"Haptic Feedback"` → `str(Res.shared.string.haptic_feedback)`
- `"Reset All Scores"` → `str(Res.shared.string.reset_all_scores)`
- Alert message → `str(Res.shared.string.reset_all_scores_confirm)`
- `"Reset"` → `str(Res.shared.string.reset)`
- `"Cancel"` → `str(Res.shared.string.cancel)`

**ChallengeLevelSelectorView.swift:**
- `"Challenge Levels"` → `str(Res.shared.string.challenge_levels)`
- `"Retry"` → `str(Res.shared.string.retry)`

Remove unused string constants (`notPlayedText`, etc.).

- [ ] **Step 3: Commit**

```
Wire localized strings into iOS UI
```

---

### Task 6: Update shared error message and run tests

**Files:**
- Modify: `shared/src/commonMain/kotlin/com/erman/pegsolitaire/presentation/Constants.kt`

- [ ] **Step 1: Remove hardcoded GENERIC_ERROR_MESSAGE**

The `GENERIC_ERROR_MESSAGE` constant in `Constants.kt` is used by ViewModels. Replace it with a function that reads from compose resources:

```kotlin
// Remove this line:
// internal const val GENERIC_ERROR_MESSAGE = "Something went wrong. Please try again."

// Add:
import com.erman.pegsolitaire.localization.StringHelper
import pegsolitaire.shared.generated.resources.Res
import pegsolitaire.shared.generated.resources.generic_error

internal fun genericErrorMessage(): String = StringHelper.get(Res.string.generic_error)
```

Update all ViewModel usages — change `GENERIC_ERROR_MESSAGE` to `genericErrorMessage()` in:
- `HomeViewModel.kt`
- `GameViewModel.kt`
- `ChallengeLevelSelectorViewModel.kt`
- `SettingsViewModel.kt`

- [ ] **Step 2: Run tests**

Run: `./gradlew :shared:testDebugUnitTest`
Expected: All tests pass

- [ ] **Step 3: Run full build**

Run: `./gradlew :composeApp:compileDebugKotlinAndroid`
Expected: BUILD SUCCESSFUL

Run: `./gradlew :shared:compileKotlinIosSimulatorArm64`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```
Replace hardcoded error message with localized resource
```
