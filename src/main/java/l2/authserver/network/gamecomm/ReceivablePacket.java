package l2.authserver.network.gamecomm;


import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;


@Slf4j
public abstract class ReceivablePacket extends l2.commons.net.nio.ReceivablePacket<GameServer> {
    private GameServer _gs;
    private ByteBuffer _buf;

    public ReceivablePacket() {
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

    public final boolean read() {
        try {
            this.readImpl();
        } catch (Exception e) {
            log.error("read: eMessage={}, eClass={}", e.getMessage(), e.getClass());
        }

        return true;
    }

    public final void run() {
        try {
            this.runImpl();
        } catch (Exception e) {
            log.error("run: eMessage={}, eClass={}", e.getMessage(), e.getClass());
        }

    }

    protected abstract void readImpl();

    protected abstract void runImpl();

    public void sendPacket(SendablePacket packet) {
        this.getGameServer().sendPacket(packet);
    }
}
