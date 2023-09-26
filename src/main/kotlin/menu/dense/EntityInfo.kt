package top.e404.eclean.menu.dense

import org.bukkit.Chunk
import org.bukkit.Material
import org.bukkit.entity.EntityType
import top.e404.eclean.clean.info
import top.e404.eclean.config.Lang
import top.e404.eplugin.EPlugin.Companion.placeholder
import top.e404.eplugin.menu.Displayable
import top.e404.eplugin.util.buildItemStack

class EntityInfo(
    val type: EntityType,
    val amount: Int,
    val chunk: Chunk
) : Displayable {
    private companion object {
        val materials = Material.values().filter { it.name.contains("WOOL") }
    }

    override fun update() {}
    override var needUpdate = false
    override val item = run {
        val placeholder = arrayOf<Pair<String, Any?>>(
            "type" to type.name,
            "amount" to amount,
            "chunk" to chunk.info(),
        )
        buildItemStack(
            materials.random(),
            1,
            Lang.get("menu.dense.item.name", *placeholder),
            Lang["menu.dense.item.lore"].placeholder(*placeholder).lines()
        )
    }
}
