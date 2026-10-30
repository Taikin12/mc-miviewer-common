# mc-miviewer-common

Shared library for `mc-cif-viewer` and `mc-mol-viewer`.

- Build: use either plugin's Gradle wrapper, e.g.
  `../mc-cif-viewer/gradlew -p . test`
- Consumers use `includeBuild("../common")` and
  `implementation("dev.taikin.miviewer:mc-miviewer-common:0.1.0-SNAPSHOT")`.
- Classes are shaded into each plugin JAR so each plugin still works alone.
- Package: `dev.taikin.miviewer.common`.

Shared so far:

- `WandItems` — wand item / PDC keys / `miviewer:wand_*` item models and
  cross-plugin target (`CIF` / `MOL`)
- `ElementInfo` — element colors / radii
- `MolecularGeometry` — distance / angle / dihedral

Next candidates: a generic `ModelRegistry<T extends SessionLike>`, display
spawning helpers, and common parts of `ViewSession`.
