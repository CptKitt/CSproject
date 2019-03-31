package Tests;

import Model.*;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Includes tests for all subclasses of Entity, including
 * Player, Enemy, Obstacle, and Stairs.
 */
public class EntityTest {
    @Test
    public void test_entity_polymorphicCopy_player() {
        Entity entity = new Player(0, 0, 0, 0, Position.NONE, 0, 0, 0, 0, "");
        Entity copy = entity.copy();
        assertTrue("Copy of Player should be Player", copy instanceof Player);
        assertNotSame("Entity of copy should not be the same reference", copy, entity);
    }
    
    @Test
    public void test_entity_polymorphicCopy_enemy() {
        Entity entity = new Enemy(0, 0, 0, 0, Position.NONE, 0, "");
        Entity copy = entity.copy();
        assertTrue("Copy of Enemy should be Enemy", copy instanceof Enemy);
        assertNotSame("Entity of copy should not be the same reference", copy, entity);
    }
    
    @Test
    public void test_entity_polymorphicCopy_obstacle() {
        Entity entity = new Obstacle();
        Entity copy = entity.copy();
        assertTrue("Copy of Obstacle should be Obstacle", copy instanceof Obstacle);
        assertNotSame("Entity of copy should not be the same reference", copy, entity);
    }
    
    @Test
    public void test_entity_polymorphicCopy_Stairs() {
        Entity entity = new Stairs();
        Entity copy = entity.copy();
        assertTrue("Copy of Stairs should be Stairs", copy instanceof Stairs);
        assertNotSame("Entity of copy should not be the same reference", copy, entity);
    }
    
    @Test
    public void test_player_random() {
        Player player = Player.randomPlayer();
        assertEquals("Random player should have LVL of 1", 1, player.getLVL());
        assertTrue("Random player should have HP above 0", player.getHP() > 0);
        assertTrue("Random player should have ATK above 0", player.getATK() > 0);
        assertTrue("Random player should have SPD above 0", player.getSPD() > 0);
        assertTrue("Random player should have DEF above 0", player.getDEF() > 0);
    }
    
    @Test
    public void test_player_LVLup() {
        Player player = Player.randomPlayer();
        Player copy = (Player) player.copy();
        copy.addEXP(1000);
        assertTrue("Adding EXP should level up Player", copy.getLVL() > player.getLVL());
    }
    
    @Test
    public void test_player_copyVariables() {
        Player player1 = Player.randomPlayer();
        Player player2 = (Player) player1.copy();
        assertEquals("Player copy should have equal stats", player1.getLVL(), player2.getLVL());
        assertEquals("Player copy should have equal stats", player1.getHP(), player2.getHP(), 0.0001);
        assertEquals("Player copy should have equal stats", player1.getmaxHP(), player2.getmaxHP(), 0.0001);
        assertEquals("Player copy should have equal stats", player1.getSPD(), player2.getSPD());
        assertEquals("Player copy should have equal stats", player1.getATK(), player2.getATK(), 0.0001);
        assertEquals("Player copy should have equal stats", player1.getPOS(), player2.getPOS());
        assertEquals("Player copy should have equal stats", player1.getSTM(), player2.getSTM());
    }
    
    @Test
    public void test_player_attackEnemyLVLup() {
        Player player = Player.randomPlayer();
        player.setATK(10); // make sure damage is viable
        Player copy = (Player) player.copy();
        // kill a bunch of enemies
        for (int i = 0; i < 10; i++) {
            Enemy enemy = Enemy.randomEnemy(1);
            enemy.setDEF(0);
            enemy.setHP(1);
            copy.attack(enemy);
        }
        assertTrue("Defeating enemies should level up Player", copy.getLVL() > player.getLVL());
    }
    
    @Test
    public void test_enemy_random() {
        Enemy enemy = Enemy.randomEnemy(1);
        assertEquals("Random enemy should have LVL of 1", 1, enemy.getLVL());
        assertTrue("Random enemy should have HP above 0", enemy.getHP() > 0);
        assertTrue("Random enemy should have ATK above 0", enemy.getATK() > 0);
        assertTrue("Random enemy should have SPD above 0", enemy.getSPD() > 0);
        assertTrue("Random enemy should have DEF above 0", enemy.getDEF() > 0);
    }
    
    @Test
    public void test_enemy_randomFloor_10() {
        Enemy enemy1 = Enemy.randomEnemy(1);
        Enemy enemy10 = Enemy.randomEnemy(10);
        assertTrue("Floor 10 Enemy should be stronger than Floor 1 enemy", enemy10.getHP() > enemy1.getHP());
        assertTrue("Floor 10 Enemy should be stronger than Floor 1 enemy", enemy10.getATK() > enemy1.getATK());
        assertTrue("Floor 10 Enemy should be stronger than Floor 1 enemy", enemy10.getDEF() > enemy1.getDEF());
    }
    
    @Test
    public void test_enemy_copyVariables() {
        Enemy enemy1 = Enemy.randomEnemy(1);
        Enemy enemy2 = (Enemy) enemy1.copy();
        assertEquals("Enemy copy should have equal stats", enemy1.getLVL(), enemy2.getLVL());
        assertEquals("Enemy copy should have equal stats", enemy1.getHP(), enemy2.getHP(), 0.0001);
        assertEquals("Enemy copy should have equal stats", enemy1.getmaxHP(), enemy2.getmaxHP(), 0.0001);
        assertEquals("Enemy copy should have equal stats", enemy1.getSPD(), enemy2.getSPD());
        assertEquals("Enemy copy should have equal stats", enemy1.getATK(), enemy2.getATK(), 0.0001);
        assertEquals("Enemy copy should have equal stats", enemy1.getPOS(), enemy2.getPOS());
    }
    
    @Test
    public void test_enemy_attackPlayer() {
        Enemy enemy = Enemy.randomEnemy(5);
        Player player = Player.randomPlayer();
        Player copy = (Player) player.copy();
        enemy.attack(player);
        assertTrue("Enemy attacking Player should reduce HP", player.getHP() < copy.getHP());
    }
}
