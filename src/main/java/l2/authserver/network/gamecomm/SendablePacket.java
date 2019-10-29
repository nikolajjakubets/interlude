package l2.authserver.network.gamecomm;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

@Slf4j
public abstract class SendablePacket extends l2.commons.net.nio.SendablePacket<GameServer> {
    protected GameServer _gs;
    protected ByteBuffer _buf;

    public SendablePacket() {
    }

    protected void setByteBuffer(ByteBuffer buf) {
        this._buf = buf;
    }

    protected ByteBuffer getByteBuffer() {
        return this._buf;
    }

    protected void setClient(GameServer gs) {
        this._gs = gs;
    }

    public GameServer getClient() {
        return this._gs;
    }

    public GameServer getGameServer() {
        return this.getClient();
    }

    public boolean write() {
        try {
            this.writeImpl();
        } catch (Exception e) {
          log.error("write: eMessage={}, eClass={}", e.getMessage(), e.getClass());
        }

        return true;
    }

    protected abstract void writeImpl();
}
