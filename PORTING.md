# Industrialization Overdrive — порт 26.1.2, round 2

**Java-порт: compileJava, build и клиентский datagen PASS; dedicated loader PASS.**

Продолжение `port/26.1.2` с `e66fb69`, 2026-11-06.
Цель: Minecraft 26.1.2 / NeoForge 26.1.2.126 / Java 25.
Gradle userdev 7.1.38, wrapper 9.2.1. Итоговая версия Overdrive:
`1.14.0-port.2+26.1.2`. MIT LICENSE и существующие copyright сохранены.
Изменяются только Overdrive и новое дерево tesseract-neoforge. Push нет.

## A — Tesseract MI bridge

Создано `../tesseract-neoforge`, ветка `port/26.1.2-mi`.
Базы проверены: 26.1.2 `2a38561ba8278911a8ca4f16af8db8a072748f8b`,
1.21.1 `6a9c981a6e6dc35ea58db3b4c8a919d893dcf744`.
Восстановлены все 139 compat/mi Java, datagen/mi, MICommonCapabitilies,
MI mixin, языковые ресурсы и связанные fluid helpers. Реально собирается
подмножество для Overdrive: исключены 8 material-authoring Java,
MIFluidHolder, TexturesMIHookDatagenProvider и старый FakedMachineModelBuilder.
Они сохранены исходниками и перечислены в Tesseract PORTING.md; не являются
рабочими API 26.1.2. Старый model builder заменён native codec datagen.
Ядро машин, hooks, multiplied crafter и все 20 типов из
`.port-tools/missing-mi-bridge.txt` присутствуют в опубликованном jar.

Опубликовано в MavenLocal:
`net.swedz:tesseract-api-neoforge:1.13.7-26.1.2-fleet.1`.
Версия берётся напрямую из mod_version, без дополнительного суффикса.
Основной jar включает plain Java Tesseract API 1.0.2 через JarJar;
Gradle API/runtime variants указывают на этот self-contained jar.
MI: `aztech:Modern-Industrialization:2.6.0-26.1.2-fleet01`,
range `[2.6.0-,2.7-)`. MI mixin включён, client viewer mixin находится
в client-секции. ValueInput injection, Identifier, transfer resources,
GUI extraction, recipe helpers и casing/state/item codecs перенесены.

Коммиты Tesseract: `2d0e957` (bridge), `b68c05c` (registry lookup для datagen),
`6bed28f` (ItemStackTemplate без создания live stack).
`compile-04`, `build-final`, `build-integration`, `publish` и
`publish-integration`, `build-template`, `publish-template`: PASS, exit 0. Логи: `../tesseract-neoforge/.port-tools/logs/`.
MI исходники и чужие worktrees не изменялись.

## B — GrandPower: замена native API

GrandPower main проверен через GitHub tree и исходники:
`875c7ce0b61dee0a20961c9d93eac0b7397e7b8f`, 8 Java (<30).
Отдельный порт нецелесообразен: его long capability/boolean simulate оборачивают
старый IEnergyStorage, а MI 2.6 использует native EnergyHandler, ItemAccess
и transactions. GrandPower не клонировался и не публиковался.

Два использования Overdrive — Vajra и IOOtherRegistries — заменены на
Tesseract SimpleEnergyItem, регистрируемый через MI ItemEnergyHandler.
Сохранены long charge/capacity, компонент MI ENERGY, creative charged variant,
зарядка и расход энергии инструментом. Native transfer ограничен int на одну
операцию; ёмкость Vajra меньше этого предела. Коммит Overdrive `7d84c01`.

## C — Overdrive

- Все 52 исходных Java обработаны: 50 активных Java компилируются; две
  недоступные интеграции сохранены текстом в `.port-tools/deferred/`.
- EI отключён до порта Extended Industrialization: активных imports/вызовов
  EIIntegration нет. EI-only chainer copy/tear-down удалён из активного builder;
  копирование/постройка/разборка MI multiblock сохранены.
- AlmostUnified отключён, IOAUPlugin вынесен из source set. Старые KubeJS,
  Jade, YAI, Kotlin не подключены. JEI/REI зависимостей нет, viewer — EMI/SEMI.
  Имена Rei* в MI — существующие MI abstractions, не добавленный REI.
- MI и Tesseract fleet, AE2 `26.1.12-SNAPSHOT` и EMI
  `1.1.24-semi151a6c88+26.1.2` подключены из MavenLocal.
- GuideME разрешился по координатам `org.appliedenergistics:guideme:26.1.14-beta`.
  Fallback `/root/home/forks/external/official/guideme-26.1.14-beta.jar`
  не понадобился.
- Identifier, InteractionResult, overlay messages, inventory slot view,
  TooltipDisplay/Consumer, key category и item model property codecs перенесены.
- Component persistence использует ValueInput/ValueOutput; builder settings
  используют TagValueInput/TagValueOutput и registry-aware codecs слотов.
  Пустые placeholders настроек слотов пропускаются при decode.
- Preview renderer использует ExtractLevelRenderStateEvent и
  SubmitCustomGeometryEvent: immutable snapshots baked quads, settings-aware
  model data, translucent зелёный/красный preview. В submit нет чтения world.
- Сохранены все четыре mixin, Java 25 compatibility. Сигнатуры insertion
  используют InteractionResult. Старый AT для RecipeProvider больше не нужен.
- Datagen перенесён на GatherDataEvent.Client, RecipeProvider.Runner,
  registry context, native machine/item definitions и новые tag providers.
  Старые item overrides заменены native range_dispatch/condition definitions.

## Реальные прогоны

Все Gradle команды строго последовательно, с
`JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64`.
Перед тяжёлыми запусками сохраняются pgrep GradleDaemon/free -m; проверенные
чужие daemon простаивали, процессы не останавливались. opcScript в деревьях нет.
Полные логи Overdrive: `.port-tools/logs/round2/`, рядом `.exitcode` и
`.errors.txt` от grep `error:`. Отсутствие error не заменяет exit code.

| Лог | Результат |
|---|---|
| compile-01 | FAIL, exit 1, 160 javac errors |
| compile-02 | FAIL, exit 1, 5 javac errors |
| compile-03 | PASS, exit 0, 3 warnings |
| datagen-01 | FAIL, exit 1: подписка на abstract GatherDataEvent; исправлено |
| datagen-02 | FAIL, exit 1: recipe builder без datagen registry context; исправлено |
| datagen-03 | FAIL, exit 1: ItemStackTemplate.create на unbound datagen holder; исправлено в bridge |
| datagen-04 | PASS, exit 0; все providers, 33 актуальных generated files |
| server-data | PASS, exit 0; dedicated mod loading, 0 providers, без запуска мира |
| compile-final | PASS, exit 0; актуальные классы UP-TO-DATE после успешной компиляции datagen |
| build-final | PASS, exit 0 |
| build-license | PASS, exit 0; итоговые binary/sources jar с оригинальным LICENSE.md |

Внешний EMI в client datagen выдаёт warning о необязательном отсутствующем
JEI PluginCaller. JEI/REI отсутствуют в фактическом Mod List; это не dependency
failure и не ошибка загрузки Overdrive/bridge.

Проверка упаковки дополнительно обнаружила ссылку прежнего Gradle-слоя на
несуществующий `LICENSE`: файл проекта называется `LICENSE.md`. Binary и sources
jar теперь включают оригинальный LICENSE.md без изменений. `build-license`:
PASS, exit 0. ZIP integrity, version, все четыре mixin class, пять item definitions,
ссылки на собственные текстуры и отсутствие EI/AU классов проверены.
Проверка: `.port-tools/logs/round2/artifact-check.txt`.

Итоговый jar:
`build/libs/industrialization_overdrive-1.14.0-port.2+26.1.2.jar`.
SHA256: `6ecd0999a115f03a934cf06db0e06bf1967fe8603322f4eff529a9c427f28ff4`.
Overdrive в MavenLocal не публиковался: запрошена сборка jar.
Tesseract MavenLocal проверен отдельно, binary + POM/module + sources + AT.

Завершено: перенос активного Java, необходимые mixins, новые generated resources,
сборка и проверки загрузчика. Неперенесённые дополнительные API Tesseract и
отключённые интеграции явно перечислены выше.

## Ограничения QA

Javac/build не доказывают игровую корректность. Требуются ручные проверки:
Pyrolyse tiers/crafting, MPA batch/upgrades/GUI, Upgrade Stacker возврат вещей,
Vajra четыре скорости/silk touch/pickup, builder copy/paste/tear-down,
AE2 linking/extraction и визуальная проверка preview на клиенте.
