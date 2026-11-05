# Industrialization Overdrive — подготовка порта 26.1.2

**Статус: НЕ ЗАВЕРШЁН. compileJava FAIL. Готового порта и проверенного jar нет.**

Основа: `master` HEAD `8a97e1de84d30d221ea6159f0ebb9de2c9f966b8`.
Ветка: `port/26.1.2`. Цель: Minecraft 26.1.2, NeoForge 26.1.2.126,
Java 25. Изменения выполнялись только в этом дереве, без push и без изменения
соседнего Modern Industrialization. Дата проверки: 2026-11-06.

## Что сделано

- Gradle moddev 1.0.17 заменён на NeoGradle userdev 7.1.38.
- Wrapper закреплён на Gradle 9.2.1; toolchain — Java 25.
- NeoForge подключён обычной implementation-зависимостью, runs переведены на
  userdev DSL; сохранены generated resources, лицензия и guidebook assets.
- Диапазоны Minecraft `[26.1.2,26.2)`, NeoForge `[26.1.2.122,26.2)`.
- Удалены старые runtime-зависимости Minecraft 1.21.1 и плагины публикации
  CurseForge/Modrinth. Публикация и mavenLocal-запись не выполнялись.
- В runtime настроен только EMI (без JEI/REI), AE2 закреплён на
  `org.appliedenergistics:appliedenergistics2:26.1.12-SNAPSHOT`, API — classifier `api`.
- Исправлен executable-бит gradlew.
- Все 52 исходных Java-файла сохранены **без изменений**; их перенос не выполнен.
  Инвентарь областей миграции: [.port-tools/java-inventory.md](.port-tools/java-inventory.md).
  Ядро машин, Vajra, Multiblock Builder, Upgrade Stacker и mixin не удалялось.

## Инвентарь обязательных зависимостей

`~/.m2/repository` является symlink на `/root/build-farm/build-farm/m2/repository`.
Проверка выполнена с разыменованием symlink.

| Зависимость | Фактическое состояние |
|---|---|
| MI | Есть `aztech:Modern-Industrialization:2.6.0-26.1.2-fleet01`, jar/POM/module/sources в Maven Local |
| Соседний MI build/libs | Каталог `../Modern-Industrialization/build/libs` отсутствует; сосед не изменялся и не собирался |
| AE2 | Есть 26.1.12-SNAPSHOT jar/api/sources в Maven Local |
| EMI | Есть `dev.emi:emi-neoforge:1.1.24-semi151a6c88+26.1.2` в Maven Local; выбран этот вариант |
| Tesseract API | Нет в Maven Local/pack-libs; ModMaven отдаёт 1.13.6-26.1.2, но **без MI bridge** |
| GrandPower | Нет локального артефакта для 26.1.2; опубликованный список ModMaven заканчивается на 3.0.2 (2024); старый API ещё импортируется Vajra и IOOtherRegistries |
| GuideME | Локальный jar не найден; MI объявляет `guideme [26.1.10-alpha,)` обязательным |

`pack-libs` прочитан: там есть FD, SilentLib/Gear, Jade 26.1.12, Curios,
Undergarden, DynamicTrees, Regions Unexplored, Refined Storage, Reliquary,
OreBerries, L2 и FruitsDelight. MI/Tesseract/GrandPower/GuideME там не найдены.
Чужие jar не копировались и не изменялись.

## Блокер Tesseract MI

В sources и бинарном jar `net.swedz:tesseract-api-neoforge:1.13.6-26.1.2`
отсутствует весь `net.swedz.tesseract.neoforge.compat.mi`. В версии
1.12.16-1.21.1 этот пакет содержит 138 Java-файлов. Также отсутствуют
`datagen.mi.MIDatagenHooks` и `registry.common.MICommonCapabitilies`.
Точный список импортов Overdrive, которых нет в новом артефакте:
[.port-tools/missing-mi-bridge.txt](.port-tools/missing-mi-bridge.txt).

Внутри нового jar, `META-INF/neoforge.mods.toml`:

```toml
#TODO wait for MI
#[[mixins]]
#config = "tesseract_api_mi.mixins.json"
```

Его optional MI range — `[2.5.0-dev-, 2.6-)`, несовместимый с локальным MI 2.6.0.
Поэтому просто изменить Gradle version/range недостаточно даже для runtime.
Отсутствуют реальные базовые классы двух машин, multiplied crafter, регистрация
машин/рецептов/GUI через hooks и вспомогательные capability/tooltip/datagen API.
Ни stubs, ни старый 1.21 jar вместо этих реализаций не добавлены.
Нужен совместимый порт MI bridge Tesseract или его полноценный перенос в этот
проект с сохранением лицензии, событий регистрации, mixin и поведения машин.

Проверенные первичные артефакты:

- https://modmaven.dev/net/swedz/tesseract-api-neoforge/maven-metadata.xml
- https://modmaven.dev/net/swedz/tesseract-api-neoforge/1.13.6-26.1.2/tesseract-api-neoforge-1.13.6-26.1.2.jar
- https://modmaven.dev/net/swedz/tesseract-api-neoforge/1.13.6-26.1.2/tesseract-api-neoforge-1.13.6-26.1.2-sources.jar

Загруженные материалы и извлечённые mods.toml сохранены локально в
`.port-tools/deps/` (игнорируются Git).

## Необязательные интеграции и runtime

Ниже перечислены **все удаления из Gradle**. Удаление зависимости не означает
завершённого переноса Java-кода интеграции; это явно указано отдельно.

| Интеграция | Состояние |
|---|---|
| GuideME Overdrive | Прямая зависимость и optional запись Overdrive в mods.toml удалены; guidebook assets сохранены. Сам MI требует GuideME для runtime, это требование не изменено |
| Extended Industrialization | compileOnly/runtime зависимости удалены; ModMaven содержит только 1.21.1. EIIntegration и вызовы ещё исходные, значит **отключение Java-интеграции НЕ завершено** |
| AlmostUnified | compileOnly API удалён; IOAUPlugin ещё исходный и пока не исключён из compilation/discovery. **Отключение Java-интеграции НЕ завершено** |
| GrandPower | Старое подключение удалено; native MI/NeoForge energy capability для Vajra ещё нужно реализовать, энергия не заменена заглушкой |
| Cloth Config | Старое подключение 15.0.127 удалено; прямых импортов в Overdrive нет, новый конфигурационный стек требует проверки |
| Kotlin for Forge | Удалена старая runtime-зависимость CurseMaven 8335665 |
| Yet Another Industrialization | Удалена старая runtime-зависимость CurseMaven 8657774 |
| KubeJS | Удалена старая runtime-зависимость 2101.7.2-build.336; новой нет в конфигурации |
| Jade | Удалена старая runtime-зависимость CurseMaven 5639932; имеющийся Jade 26.1.12 из pack-libs автоматически не подключался |
| MI Tweaks | Уже был закомментирован upstream; не включён |
| AE2 | Настроен новый API/runtime 26.1.12-SNAPSHOT; AE2Integration ещё не портирован и не проверен |
| EMI | Настроен runtime 26.1.2 из Maven Local, запуск клиента не проверен |
| JEI / REI | Не подключены |

MI подключён с `transitive = false` для отдельной диагностики Overdrive.
Это **не готовый runtime-набор**: в частности, обязательный для MI GuideME нужно
будет добавить отдельно. Не следует запускать этот WIP как рабочий порт.

## Mixin и access transformer

Один JSON содержит **четыре** mixin-класса:

| Mixin | Проверка цели |
|---|---|
| AbstractElectricMultipliedCraftingMultiblockMixin | Класс Tesseract отсутствует; `getMaxRecipeEuBonus` и `useItemOn` проверить невозможно. Возвращаемый ItemInteractionResult требует переноса на InteractionResult |
| AbstractMultipliedCraftingMultiblockAccessor | Класс Tesseract отсутствует; поле crafter проверить невозможно |
| SlotPanelAccessor | В локальном MI есть private final MachineBlockEntity machine |
| SlotPanelUpgradeStackerMixin | javap подтвердил SlotPanel$1, this$0, val$type и mayPlace(ItemStack). Проверка instanceof всё ещё зависит от отсутствующего Tesseract-класса |

JSON пока исходный (`JAVA_21`); регистрация mixin не отключена. После восстановления
MI bridge нужны перенос и фактическая проверка применения mixin на Java 25.
Старый access transformer `RecipeProvider.getName()` ещё требует проверки/замены
в новом datagen API; userdev пока не настроен на его применение.

## Остатки переноса Java и данных

Кроме блокера Tesseract, javac обнаруживает ResourceLocation → Identifier,
ItemInteractionResult/InteractionResultHolder → InteractionResult, изменения
ItemProperties, client renderer/RenderType/ModelData и datagen API.
У MI изменились сериализация компонентов (ValueInput/ValueOutput) и transfer API.
Нужны native energy capability и сохранение charging/разряда/energy bar Vajra.
Необходим перенос item definitions/models и проверка рецептов/loot/tag JSON.

Ни один Java-файл не объявляется портированным. Полный список всех 52 файлов
и статически обнаруженных областей работы находится в java-inventory.md.

## Проверки

Java: `/usr/lib/jvm/java-25-openjdk-amd64`.

```sh
JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64 ./gradlew compileJava --console=plain
JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64 ./gradlew build --console=plain
```

`opcScript` в дереве отсутствует; `-PopcScript` не требуется.
Перед тяжёлыми запусками проверялись `pgrep -af GradleDaemon`, `free -m`,
cmd/cwd и логи daemon. Проверенные чужие daemon простаивали. Чужие процессы
не останавливались. Запуски выполнялись последовательно.

Первый запуск: exit 126, gradlew без executable-бита; исправлено.
Следующий compileJava: exit 1, 342 ошибки javac (Gradle повторяет диагностику
в итоговом failure summary, поэтому число строк `error:` в логе больше).
NeoForm/NeoForge подготовка и компиляция Minecraft завершились; это не успех
компиляции Overdrive.

Итоговые прогоны:

| Команда с JAVA_HOME Java 25 | Exit code | Результат | Полный лог |
|---|---|---|---|
| `./gradlew compileJava --console=plain` | 1 | FAIL, 342 ошибки javac | `.port-tools/logs/compile-final.log` |
| `./gradlew build --console=plain` | 1 | FAIL на compileJava, 342 ошибки javac | `.port-tools/logs/build-final.log` |
| `./gradlew processResources --console=plain` | 0 | PASS; фактический output TOML разобран, версия и NeoForge range проверены | `.port-tools/logs/resources-final.log` |

Соответствующие `.exitcode` и `.errors.txt` лежат рядом с каждым логом.
`rg 'error:'` выполнен для обоих итоговых логов; Gradle дублирует диагностику
в summary. Дополнительно синтаксический разбор TOML с подстановками и JSON mixin
прошёл; это не проверка применения mixin и не runtime QA.
`git diff --check` проходит. Логи/скачанные jar игнорируются Git, но остаются
в этом дереве. В Git сохраняются этот отчёт и оба текстовых инвентаря.

## Runtime QA — НЕ выполнялась

После настоящего compileJava/build PASS обязательны:

1. Отдельный server boot с точными MI/Tesseract/GuideME и проверка применения
   всех четырёх mixin, datamap и регистрации recipe types/машин.
2. Pyrolyse Oven: tiers, питание, fluid/item IO, multiplied crafting и EU cost.
3. Multi Processing Array: insertion/removal машин, лимиты, upgrades, crafter,
   GUI и сохранение состояния после перезапуска.
4. Upgrade Stacker: insertion через GUI/ПКМ, EU bonus, batch cap и возврат вещей.
5. Vajra: заряд, четыре скорости, silk touch, pickup, рекурсивное разрушение,
   unbreakable blocks, creative/survival, синхронизация компонентов.
6. Multiblock Builder: copy/build/tear-down, настройки, AE2 simulation/extraction,
   права доступа и измерения, отображение preview и переключение режимов.
7. Клиент: item definitions/models, tooltips, key mappings, previews, GUI, EMI.
8. Datagen, рецепты, loot, теги и совместимость сохранений компонентов.

Эта ветка — воспроизводимая отправная точка с документированной неудачной
сборкой, **не полный порт Industrialization Overdrive**.

## Round 2 — B: GrandPower decision

GrandPower main inspected via GitHub tree and source at
`875c7ce0b61dee0a20961c9d93eac0b7397e7b8f`: 8 Java files (<30).
A separate port was judged unnecessary: its long capability and boolean simulation
API wrap the old IEnergyStorage, while MI 2.6 now directly uses native NeoForge
EnergyHandler, ItemAccess and transactions. The two Overdrive uses (Vajra and
IOOtherRegistries) now use Tesseract SimpleEnergyItem backed by native MI
ItemEnergyHandler. Long charge/capacity, MI ENERGY component, creative charged
variant and tool energy accounting are retained. No GrandPower clone/artifact
was created or published. Compilation is verified together with stage C.
