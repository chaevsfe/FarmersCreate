# Create Fly: Farmers Create

Runs every Farmer's Delight cutting board recipe on a Create deployer and every cooking pot recipe in a heated mixer.

> [!IMPORTANT]
> **This is an unofficial port.** It is not affiliated with or endorsed by
> ChefExperte.
> Please report problems here.

## What it does

When recipes load, the mod reads every cutting board and cooking pot recipe on the server, including those added by Farmer's Delight add-ons and data packs, and adds a matching Create recipe for each:

- **Cutting board** recipes become deploying recipes. The deployer holds the tool the cutting board recipe asks for (a knife, an axe, a pickaxe and so on), keeps it undamaged, and every output keeps its chance.
- **Cooking pot** recipes become heated mixing recipes with the same ingredients and cooking time. The meal comes out of the basin directly; the bowl or bottle the cooking pot serves it into is not needed.

The generated recipes are named `farmerscreate:cutting/<namespace>/<path>` and `farmerscreate:cooking/<namespace>/<path>`. There are no blocks, items or config.

## Compatibility

| Component | Version |
| --- | --- |
| Minecraft | `26.1`, `26.1.1` or `26.1.2` |
| Mod loader | Fabric Loader `0.19.3` or newer |
| Create Fly | `6.0.9-4` or newer |
| Farmer's Delight Refabricated | `26.1-3.6.26` or newer |
| Fabric API | required |
| Java | `25` or newer |
| Environments | Client and server |

## Download

- [GitHub releases](https://github.com/chaevsfe/FarmersCreate/releases)

## Building

```
git clone https://github.com/chaevsfe/FarmersCreate
cd FarmersCreate
./gradlew build
```

Requires JDK 25.

## Credits

- **FarmersCreate** by ChefExperte
- **Create Fly** by ZurrTum
- **Farmer's Delight Refabricated** by MehVahdJukaar and contributors
- **Farmer's Delight** by vectorwing
- **Create** by the Create Team
- **Create: Slice and Dice** by PssbleTrngle, which inspired the original mod

## Licence

GNU Affero General Public License v3.0 or later, as upstream. See `LICENSE.txt` and `NOTICE`.

## Reporting Bugs
When reporting bugs, always include the version number of the mod. If you're reporting a crash, include your client or server log depending on where the crash occurred.
