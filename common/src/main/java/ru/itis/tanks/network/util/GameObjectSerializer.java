package ru.itis.tanks.network.util;

import lombok.NoArgsConstructor;
import ru.itis.tanks.game.model.Collectable;
import ru.itis.tanks.game.model.GameObject;
import ru.itis.tanks.game.model.Gun;
import ru.itis.tanks.game.model.MovingObject;
import ru.itis.tanks.game.model.impl.obstacle.*;
import ru.itis.tanks.game.model.impl.tank.Tank;
import ru.itis.tanks.game.model.impl.weapon.DefaultGun;
import ru.itis.tanks.game.model.impl.weapon.Projectile;
import ru.itis.tanks.game.model.impl.weapon.RocketGun;

import java.io.IOException;
import java.nio.ByteBuffer;

import static ru.itis.tanks.network.GameObjectType.*;

//TODO пересылка isMoving, команды и дельты или только координат с отдельным клиенским миром
/* Общее
* [type-code][texture][x][y][w][h]
*   Для двигающегося
*   [id][velocity][direction-x][direction-y] + ...
*       Танк:
*           [max-hp][hp][last-shoot-time][gun-code]
*       Пуля:
*           [tank-id][velocity][damage]
*/

@NoArgsConstructor
public class GameObjectSerializer {

    public ByteBuffer serialize(GameObject object) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024 * 4);
        buffer.put((byte) -1);//Тут будет тип объекта
        buffer.putInt(object.getTexture().getCode());
        buffer.putInt(object.getX());
        buffer.putInt(object.getY());
        buffer.putInt(object.getWidth());
        buffer.putInt(object.getHeight());
        switch (object) {
            case Block obj -> serializeBlock(obj, buffer);
            case MovingObject obj -> serializeMovingObject(obj, buffer);
            case Gun obj -> serializeGun(obj, buffer);
            case Collectable col -> serializeCollectable(col, buffer);
            default -> throw new IOException("Unexpected value: " + object);
        }
        buffer.flip();
        return buffer;
    }

    private void serializeMovingObject(MovingObject object, ByteBuffer buffer) throws IOException {
        buffer.putInt(object.getId());
        buffer.putInt(object.getVelocity());
        buffer.putInt(object.getDirection().getX());
        buffer.putInt(object.getDirection().getY());
        switch (object){
            case Tank tank ->{
                buffer.put(0, (byte) TANK.getCode());
                buffer.putInt(tank.getMaxHp());
                buffer.putInt(tank.getHp());
                buffer.putLong(tank.getLastShootTime());
                serializeGun(tank.getGun(), buffer);
            }

            case Projectile projectile ->{
                buffer.put(0, (byte) PROJECTILE.getCode());
                buffer.putInt(projectile.getTank().getId());
                buffer.putInt(projectile.getVelocity());
                buffer.putInt(projectile.getDamage());
            }
            default -> throw new IOException("Unexpected value: " + object);
        }
    }

    private void serializeBlock(Block block, ByteBuffer buffer) {
        switch (block) {
            case DestroyableBlock obj -> {
                buffer.put(0, (byte) DESTROYABLE_BLOCK.getCode());
                buffer.putInt(obj.getMaxHp());
                buffer.putInt(obj.getHp());
            }
            case CollideableBlock _ -> buffer.put(0, (byte) COLLIDEABLE_BLOCK.getCode());
            case Block _ -> buffer.put(0, (byte) BLOCK.getCode());
        }
    }

    private void serializeGun(Gun gun, ByteBuffer buffer) throws IOException {
        switch (gun){
            case DefaultGun _ ->{
                buffer.put(0, (byte) DEFAULT_GUN.getCode());
            }
            case RocketGun _ ->{
                buffer.put(0, (byte) ROCKET_GUN.getCode());
            }
            default -> throw new IOException("Unexpected value: " + gun);
        }
    }

    private void serializeCollectable(Collectable col, ByteBuffer buffer) throws IOException {
        switch (col){
            case RocketGunPowerup _ -> buffer.put(0, (byte) ROCKET_GUN_POWERUP.getCode());
            case HealthPowerup _ -> buffer.put(0, (byte) HEALTH_POWERUP.getCode());
            case SpeedPowerup _ -> buffer.put(0, (byte) SPEED_POWERUP.getCode());
            default -> throw new IOException("Unexpected value: " + col);
        }
    }

}
