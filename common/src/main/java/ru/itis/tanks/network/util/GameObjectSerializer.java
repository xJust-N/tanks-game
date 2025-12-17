package ru.itis.tanks.network.util;

import lombok.NoArgsConstructor;
import ru.itis.tanks.game.model.Collectable;
import ru.itis.tanks.game.model.GameObject;
import ru.itis.tanks.game.model.Gun;
import ru.itis.tanks.game.model.impl.obstacle.*;
import ru.itis.tanks.game.model.impl.tank.Tank;
import ru.itis.tanks.game.model.impl.weapon.DefaultGun;
import ru.itis.tanks.game.model.impl.weapon.Projectile;
import ru.itis.tanks.game.model.impl.weapon.RocketGun;

import java.io.IOException;
import java.nio.ByteBuffer;

import static ru.itis.tanks.network.util.GameObjectType.*;

//TODO пересылка isMoving, команды и дельты или только координат с отдельным клиенским миром
@NoArgsConstructor
public class GameObjectSerializer {

    public byte[] serialize(GameObject object) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(6 * 4);
        int i = 1;
        buffer.putInt(i++, object.getTexture().getCode());
        buffer.putInt(i++, object.getX());
        buffer.putInt(i++, object.getY());
        buffer.putInt(i++, object.getWidth());
        buffer.putInt(i++, object.getHeight());
        switch (object) {
            case Block obj -> serializeBlock(obj, buffer, i);
            case Projectile obj -> {
                buffer.putInt(0, PROJECTILE.getCode());
                buffer.putInt(i++, obj.getTank().getId());
                buffer.putInt(i++, obj.getVelocity());
                buffer.putInt(i++, obj.getDamage());
            }
            case Tank obj -> {
                buffer.putInt(0, TANK.getCode());
                buffer.putInt(i++, obj.getId());
                buffer.putInt(obj.getMaxHp());
                buffer.putInt(i++, obj.getHp());
                buffer.putLong(i++, obj.getLastShootTime());
                serializeGun(obj.getGun(), buffer, i);
            }
            case Gun obj -> serializeGun(obj, buffer, i);
            case Collectable col -> serializeCollectable(col, buffer);
            default -> throw new IOException("Unexpected value: " + object.getClass().getSimpleName());
        }
        return buffer.array();
    }

    private void serializeBlock(Block block, ByteBuffer buffer, int i) {
        switch (block) {
            case DestroyableBlock obj -> {
                buffer.putInt(0, DESTROYABLE_BLOCK.getCode());
                buffer.putInt(i++, obj.getMaxHp());
                buffer.putInt(i++, obj.getHp());
            }
            case CollideableBlock _ -> buffer.putInt(0, COLLIDEABLE_BLOCK.getCode());
            case Block _ -> buffer.putInt(0, BLOCK.getCode());
        }
    }

    private void serializeGun(Gun gun, ByteBuffer buffer, int i) throws IOException {
        switch (gun){
            case DefaultGun _ ->{
                buffer.putInt(0, DEFAULT_GUN.getCode());
                buffer.putInt(i++, gun.getTank().getId());
            }
            case RocketGun _ ->{
                buffer.putInt(0, ROCKET_GUN.getCode());
                buffer.putInt(i++, gun.getTank().getId());
            }
            default -> throw new IOException("Unexpected value: " + gun.getClass().getSimpleName());
        }
    }

    private void serializeCollectable(Collectable col, ByteBuffer buffer) throws IOException {
        switch (col){
            case RocketGunPowerup _ -> buffer.putInt(0, ROCKET_GUN_POWERUP.getCode());
            case HealthPowerup _ -> buffer.putInt(0, HEALTH_POWERUP.getCode());
            case SpeedPowerup _ -> buffer.putInt(0, SPEED_POWERUP.getCode());
            default -> throw new IOException("Unexpected value: " + col.getClass().getSimpleName());
        }
    }

}
