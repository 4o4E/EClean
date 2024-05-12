package top.e404.eclean.test.clean

import be.seeseemelk.mockbukkit.entity.LivingEntityMock
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import top.e404.eclean.clean.cleanLiving
import top.e404.eclean.clean.lastLiving
import top.e404.eclean.config.Config
import top.e404.eclean.test.*

abstract class LivingCleanTest {

    @BeforeEach
    fun enable() {
        resetConfig()
        Config.config.living.enable = true
    }

    @Nested
    @DisplayName("实体名清理设置")
    inner class TestCleanEntityWithCustomName {
        @Test
        @DisplayName("启用")
        fun onEnable() {
            // 设置为true则清理被命名的生物
            Config.config.living.settings.name = true
            Config.config.living.match = mutableListOf(Regex("ZOMBIE"))

            val chunk = world.getChunkAt(0, 0)
            chunk.load()
            val location = Location(world, 8.0, 8.0, 8.0)
            val entities = world.spawnEntities(location, EntityType.ZOMBIE, 16) { _, zombie ->
                @Suppress("DEPRECATION")
                zombie.customName = "custom name"
            }

            cleanLiving()

            assert(entities.none(Entity::isValid)) { "启用清理命名的实体时, 命名的实体应当被全部清理\n$consoleOut" }
        }

        @Test
        @DisplayName("禁用")
        fun onDisable() {
            // 设置为true则清理被命名的生物
            Config.config.living.settings.name = false
            Config.config.living.match = mutableListOf(Regex("ZOMBIE"))

            val chunk = world.getChunkAt(0, 0)
            chunk.load()
            val location = Location(world, 8.0, 8.0, 8.0)
            val entities = world.spawnEntities(location, EntityType.ZOMBIE, 16) { _, zombie ->
                @Suppress("DEPRECATION")
                zombie.customName = "custom name"
            }

            cleanLiving()
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
            Config.config.living.settings.lead = true
            Config.config.living.match = mutableListOf(Regex("SHEEP"))

            val chunk = world.getChunkAt(0, 0)
            chunk.load()
            val location = Location(world, 8.0, 8.0, 8.0)
            val entities = world.spawnEntities(location, EntityType.SHEEP, 16) { _, sheep ->
                sheep as LivingEntityMock
                sheep.setLeashHolder(sheep)
            }

            cleanLiving()
            assert(entities.none(Entity::isValid)) { "启用清理拴绳拴住的实体时, 拴绳拴住的实体应当被清理\n$consoleOut" }
        }

        @Test
        @DisplayName("禁用")
        fun onDisable() {
            // 设置为true则清理被拴绳拴住的生物
            Config.config.living.settings.lead = false
            Config.config.living.match = mutableListOf(Regex("SHEEP"))

            val chunk = world.getChunkAt(0, 0)
            chunk.load()
            val location = Location(world, 8.0, 8.0, 8.0)
            val entities = world.spawnEntities(location, EntityType.SHEEP, 16) { _, sheep ->
                sheep as LivingEntityMock
                sheep.setLeashHolder(sheep)
            }

            cleanLiving()
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
            Config.config.living.settings.mount = true
            Config.config.living.match = mutableListOf(Regex("HORSE"))

            val chunk = world.getChunkAt(0, 0)
            chunk.load()
            val location = Location(world, 8.0, 8.0, 8.0)
            val entities = world.spawnEntities(location, EntityType.HORSE, 16) { index, horse ->
                horse as LivingEntityMock
                if (index < 8) horse.addPassenger(player)
                else player.addPassenger(horse)
            }

            cleanLiving()
            assert(entities.none(Entity::isValid)) { "启用清理乘骑中的实体时, 乘骑中的实体应当被清理\n$consoleOut" }
        }

        @Test
        @DisplayName("禁用")
        fun onDisable() {
            // 设置为true则清理乘骑中的生物
            Config.config.living.settings.mount = false
            Config.config.living.match = mutableListOf(Regex("HORSE"))

            val chunk = world.getChunkAt(0, 0)
            chunk.load()
            val location = Location(world, 8.0, 8.0, 8.0)
            val entities = world.spawnEntities(location, EntityType.HORSE, 16) { index, horse ->
                horse as LivingEntityMock
                if (index < 8) horse.addPassenger(player)
                else player.addPassenger(horse)
            }

            cleanLiving()
            assert(entities.all(Entity::isValid)) { "禁用清理乘骑中的实体时, 乘骑中的实体不应当被清理\n$consoleOut" }
        }
    }

    @Nested
    @DisplayName("黑白名单设置")
    inner class TestBlackWhiteList {
        @Test
        @DisplayName("黑名单")
        fun blackList() {
            // 设置为true则按黑名单匹配(名字匹配的才清理)
            Config.config.living.black = true
            Config.config.living.match = mutableListOf(Regex("ZOMBIE.*"))

            val chunk = world.getChunkAt(0, 0)
            chunk.load()
            val location = Location(world, 8.0, 8.0, 8.0)
            val shouldClean = listOf(
                world.spawnEntity(location, EntityType.ZOMBIE),
                world.spawnEntity(location, EntityType.ZOMBIE_HORSE),
            )
            val shouldNotClean = listOf(
                world.spawnEntity(location, EntityType.SHEEP),
                world.spawnEntity(location, EntityType.COW),
            )

            cleanLiving()

            assert(shouldClean.none(Entity::isValid)) { "黑名单模式中匹配的实体应该全部清理\n$consoleOut" }
            assert(shouldNotClean.all(Entity::isValid)) { "黑名单模式中不匹配的实体应该全部不清理\n$consoleOut" }
        }

        @Test
        @DisplayName("白名单")
        fun whiteList() {
            // 设置为false则按白名单匹配(名字匹配的不清理)
            Config.config.living.black = false
            Config.config.living.match = mutableListOf(Regex("ZOMBIE.*"))

            val chunk = world.getChunkAt(0, 0)
            chunk.load()
            val location = Location(world, 8.0, 8.0, 8.0)
            val shouldClean = listOf(
                world.spawnEntity(location, EntityType.SHEEP),
                world.spawnEntity(location, EntityType.COW),
            )
            val shouldNotClean = listOf(
                world.spawnEntity(location, EntityType.ZOMBIE),
                world.spawnEntity(location, EntityType.ZOMBIE_HORSE),
            )

            cleanLiving()

            assert(shouldClean.none(Entity::isValid)) { "白名单模式中不匹配的实体应该全部清理\n$consoleOut" }
            assert(shouldNotClean.all(Entity::isValid)) { "白名单模式中匹配的实体应该全部不清理\n$consoleOut" }
        }
    }

    @Test
    @DisplayName("papi")
    fun testPapi() {
        val count = 2
        Config.config.living.match = mutableListOf(Regex("ZOMBIE"))

        val chunk = world.getChunkAt(0, 0)
        chunk.load()
        val location = Location(world, 8.0, 8.0, 8.0)
        world.spawnEntities(location, EntityType.ZOMBIE, count)

        cleanLiving()

        assert(lastLiving == count) { "papi展示最后一次生物清理的实体数时不正确\n$consoleOut" }
    }
}