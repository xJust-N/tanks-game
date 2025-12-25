package ru.itis.tanks.network.util;

import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.itis.tanks.game.model.Direction;
import ru.itis.tanks.game.model.GameObject;
import ru.itis.tanks.game.model.Gun;
import ru.itis.tanks.game.model.impl.Texture;
import ru.itis.tanks.game.model.impl.obstacle.*;
import ru.itis.tanks.game.model.impl.tank.Tank;
import ru.itis.tanks.game.model.impl.weapon.DefaultGun;
import ru.itis.tanks.game.model.impl.weapon.Projectile;
import ru.itis.tanks.game.model.impl.weapon.RocketGun;
import ru.itis.tanks.game.model.map.GameWorld;
import ru.itis.tanks.network.GameObjectType;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static ru.itis.tanks.network.GameObjectType.*;

@Setter
@NoArgsConstructor
public class GameObjectDeserializer {

    private GameWorld world;

    public GameObject deserialize(SocketChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1 + 4 * 5);
        readFully(channel, buffer);
        byte typeCode = buffer.get();
        GameObjectType type;
        try {
           type = GameObjectType.fromCode(typeCode);
        } catch (IllegalArgumentException e) {
            throw new IOException("Unknown gameobject type: " + typeCode);
        }
        int textureCode = buffer.getInt();
        Texture texture = Texture.fromCode(textureCode);
        int x = buffer.getInt();
        int y = buffer.getInt();
        int width = buffer.getInt();
        int height = buffer.getInt();

        switch (type) {
            case BLOCK -> {
                return new Block(x, y, width, height, texture);
            }
            case COLLIDEABLE_BLOCK -> {
                return new CollideableBlock(x, y, width, height, texture);
            }
            case DESTROYABLE_BLOCK -> {
                buffer = ByteBuffer.allocate(4 * 3);
                readFully(channel, buffer);
                int id = buffer.getInt();
                int maxHp = buffer.getInt();
                int hp = buffer.getInt();
                 return new DestroyableBlock(world, id, maxHp, hp, x, y, width, height, texture);
            }
            case TANK -> {
                buffer = ByteBuffer.allocate(6 * 4 + 8 + 1);
                readFully(channel, buffer);
                int id = buffer.getInt();
                int velocity = buffer.getInt();
                int dirX = buffer.getInt();
                int dirY = buffer.getInt();
                Direction direction = Direction.ofValue(dirX, dirY);
                int maxHp = buffer.getInt();
                int hp = buffer.getInt();
                long lastShootTime = buffer.getLong();
                byte gunCode = buffer.get();
                 Tank t = new Tank(world, id, maxHp, hp,
                         lastShootTime, null, velocity, direction, texture, x, y, width, height);
                if (gunCode == DEFAULT_GUN.getCode()) {
                    t.setGun(new DefaultGun(t));
                } else if (gunCode == ROCKET_GUN.getCode()) {
                    t.setGun(new RocketGun(t));
                }
                return t;
            }
            case PROJECTILE -> {
                buffer = ByteBuffer.allocate(6 * 4);
                readFully(channel, buffer);
                int id = buffer.getInt();
                int velocity = buffer.getInt();
                int dirX = buffer.getInt();
                int dirY = buffer.getInt();
                Direction direction = Direction.ofValue(dirX, dirY);
                int tankId = buffer.getInt();
                int damage = buffer.getInt();
                Tank tank = world.getTanks().get(tankId);
                if (tank == null) {
                    throw new IOException("Tank not found for id: " + tankId);
                }
                 Projectile p = new Projectile(id, tank, velocity, damage, texture, x, y, width, height);
                p.setDirection(direction);
                return p;
            }
            case DEFAULT_GUN, ROCKET_GUN ->
                    throw new IOException("Gun should not be deserialized as standalone GameObject");
            case ROCKET_GUN_POWERUP -> {
                buffer = ByteBuffer.allocate(4);
                readFully(channel, buffer);
                return new RocketGunPowerup(world, buffer.getInt(), x, y);
            }
            case HEALTH_POWERUP -> {
                buffer = ByteBuffer.allocate(4);
                readFully(channel, buffer);
                return new HealthPowerup(world, buffer.getInt(), x, y);
            }
            case SPEED_POWERUP -> {
                buffer = ByteBuffer.allocate(4);
                readFully(channel, buffer);
                return new SpeedPowerup(world, buffer.getInt(), x, y);
            }
            default -> throw new IOException("Unknown GameObject type: " + type);
        }
    }

    private void readFully(SocketChannel channel, ByteBuffer buffer) throws IOException {
        while (buffer.hasRemaining()) {
            if (channel.read(buffer) == -1) {
                throw new IOException("Connection closed prematurely");
            }
        }
        buffer.flip();
    }
}