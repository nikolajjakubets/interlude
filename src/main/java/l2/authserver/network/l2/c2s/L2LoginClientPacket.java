package l2.authserver.network.l2.c2s;

import l2.authserver.network.l2.L2LoginClient;
import l2.commons.net.nio.impl.ReceivablePacket;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class L2LoginClientPacket extends ReceivablePacket<L2LoginClient> {

    public L2LoginClientPacket() {
    }

    protected final boolean read() {
        try {
            this.readImpl();
            return true;
        } catch (Exception e) {
            log.error("read: eMessage={}, eClass={}", e.getMessage(), e.getClass());
            return false;
        }
    }

    public void run() {
        try {
            this.runImpl();
        } catch (Exception e) {
            log.error("run: eMessage={}, eClass={}", e.getMessage(), e.getClass());
        }

    }

    protected abstract void readImpl();

    protected abstract void runImpl() throws Exception;
}
