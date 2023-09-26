package top.e404.eclean.test

import be.seeseemelk.mockbukkit.MockBukkit
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import top.e404.eclean.EClean
import top.e404.eclean.test.clean.ChunkCleanTest
import top.e404.eclean.test.clean.DropCleanTest
import top.e404.eclean.test.clean.LivingCleanTest
import top.e404.eclean.unit
import trash.TrashcanTest

@DisplayName("清理单元测试")
class ECleanTest {
    companion object {
        @JvmStatic
        @BeforeAll
        fun init() {
            unit = true
            server = MockBukkit.mock()
            plugin = MockBukkit.load(EClean::class.java)
            world = server.addSimpleWorld("world")
            player = server.addPlayer("mock")
            consoleOut
        }

        @JvmStatic
        @BeforeAll
        fun finalize() {
            MockBukkit.unmock()
        }
    }

    @Nested
    @DisplayName("区块清理单元测试")
    inner class TestChunkClean : ChunkCleanTest()

    @Nested
    @DisplayName("掉落物清理单元测试")
    inner class TestDropClean : DropCleanTest()

    @Nested
    @DisplayName("生物清理单元测试")
    inner class TestLivingClean : LivingCleanTest()

    @Nested
    @DisplayName("垃圾桶单元测试")
    inner class TestTrashcan : TrashcanTest()
}