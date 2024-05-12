package top.e404.eclean.test.clean

import be.seeseemelk.mockbukkit.entity.LivingEntityMock
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import top.e404.eclean.clean.cleanDenseEntities
import top.e404.eclean.clean.lastChunk
import top.e404.eclean.config.Config
import top.e404.eclean.test.*

abstract class ChunkCleanTest {

    @BeforeEach
    fun enable() {
        resetConfig()
        Config.config.chunk.enable = true
    }

    @Nested
    @DisplayName("实体名清理设置")
    inner class TestCleanEntityWithCustomName {
        @Test
        @DisplayName("启用")
        fun onEnable() {
            // 设置为true则清理被命名的生物
            Config.config.chunk.settings.name = true
            val limit = 5
            Config.config.chunk.limit[Regex("ZOMBIE")] = limit

            val chunk = world.getChunkAt(0, 0)
            chunk.load()
            val location = Location(world, 8.0, 8.0, 8.0)
            val entities = world.spawnEntities(location, EntityType.ZOMBIE, 16) { _, zombie ->
                @Suppress("DEPRECATION")
                zombie.customName = "custom name"
            }

            cleanDenseEntities()

            val valid = entities.count(Entity::isValid)
            assert(valid == limit) { "启用清理命名的实体时, 命名的实体应当被清理($valid != $limit)\n$consoleOut" }
        }

        @Test
        @DisplayName("禁用")
        fun onDisable() {
            // 设置为true则清理被命名的生物
            Config.config.chunk.settings.name = false
            Config.config.chunk.limit[Regex("ZOMBIE")] = 5

            val chunk = world.getChunkAt(0, 0)
            chunk.load()
            val location = Location(world, 8.0, 8.0, 8.0)
            val entities = world.spawnEntities(location, EntityType.ZOMBIE, 16) { _, zombie ->
                @Suppress("DEPRECATION")
                zombie.customName = "custom name"
            }

            cleanDenseEntities()
            assert(entities.all(Entity::isValid)) { "禁用清理命名的实体时, 命名的实体不应当被清理\n$consoleOut" }
        }
    }

    @Nested
    @DisplayName("拴绳清理设置")
    inner class TestCleanEntityWithLead {
        @Test
        @DisplayName("启用")
        fun onEnable() {
            // 设置为true则清理被拴绳拴住的生物
            Config.config.chunk.settings.lead = true
            val limit = 5
            Config.config.chunk.limit = mutableMapOf(Regex("SHEEP") to limit)

            val chunk = world.getChunkAt(0, 0)
            chunk.load()
            val location = Location(world, 8.0, 8.0, 8.0)
            val entities = world.spawnEntities(location, EntityType.SHEEP, 16) { _, sheep ->
                sheep as LivingEntityMock
                sheep.setLeashHolder(sheep)
            }

            cleanDenseEntities()
            val valid = entities.count(Entity::isValid)
            assert(valid == limit) { "启用清理拴绳拴住的实体时, 拴绳拴住的实体应当被清理($valid != $limit)\n$consoleOut" }
        }

        @Test
        @DisplayName("禁用")
        fun onDisable() {
            // 设置为true则清理被拴绳拴住的生物
            Config.config.chunk.settings.lead = false
            Config.config.chunk.limit = mutableMapOf(Regex("SHEEP") to 5)

            val chunk = world.getChunkAt(0, 0)
            chunk.load()
            val location = Location(world, 8.0, 8.0, 8.0)
            val entities = world.spawnEntities(location, EntityType.SHEEP, 16) { _, sheep ->
                sheep as LivingEntityMock
                sheep.setLeashHolder(sheep)
            }

            cleanDenseEntities()
            assert(entities.all(Entity::isValid)) { "禁用清理拴绳拴住的实体时, 拴绳拴住的实体不应当被清理\n$consoleOut" }
        }
    }

    @Nested
    @DisplayName("乘骑清理设置")
    inner class TestCleanEntityWithMount {
        @Test
        @DisplayName("启用")
        fun onEnable() {
            // 设置为true则清理乘骑中的生物
            Config.config.chunk.settings.mount = true
            val limit = 5
            Config.config.chunk.limit = mutableMapOf(Regex("HORSE") to limit)

            val chunk = world.getChunkAt(0, 0)
            chunk.load()
            val location = Location(world, 8.0, 8.0, 8.0)
            val entities = world.spawnEntities(location, EntityType.HORSE, 16) { index, horse ->
                horse as LivingEntityMock
                if (index < 8) horse.addPassenger(player)
                else player.addPassenger(horse)
            }

            cleanDenseEntities()
            val valid = entities.count(Entity::isValid)
            assert(valid == limit) { "启用清理乘骑中的实体时, 乘骑中的实体应当被清理($valid != $limit)\n$consoleOut" }
        }

        @Test
        @DisplayName("禁用")
        fun onDisable() {
            // 设置为true则清理乘骑中的生物
            Config.config.chunk.settings.mount = false
            Config.config.chunk.limit = mutableMapOf(Regex("HORSE") to 5)

            val chunk = world.getChunkAt(0, 0)
            chunk.load()
            val location = Location(world, 8.0, 8.0, 8.0)
            val entities = world.spawnEntities(location, EntityType.HORSE, 16) { index, horse ->
                horse as LivingEntityMock
                if (index < 8) horse.addPassenger(player)
                else player.addPassenger(horse)
            }

            cleanDenseEntities()
            assert(entities.all(Entity::isValid)) { "禁用清理乘骑中的实体时, 乘骑中的实体不应当被清理\n$consoleOut" }
        }
    }

    @Test
    @DisplayName("papi")
    fun testPapi() {
        val limit = 5
        val count = 16
        Config.config.chunk.limit = mutableMapOf(Regex("ZOMBIE") to limit)

        val chunk = world.getChunkAt(0, 0)
        chunk.load()
        val location = Location(world, 8.0, 8.0, 8.0)
        val entities = world.spawnEntities(location, EntityType.ZOMBIE, count)

        cleanDenseEntities()
        val valid = entities.count(Entity::isValid)
        assert(valid == limit)
        assert(lastChunk == count - limit) { "papi展示最后一次区块清理的实体数时不正确\n$consoleOut" }
    }
}