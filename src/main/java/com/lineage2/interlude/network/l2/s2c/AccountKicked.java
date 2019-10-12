package com.lineage2.interlude.network.l2.s2c;

public final class AccountKicked extends L2LoginServerPacket {
    private int reason;

    public AccountKicked(AccountKicked.AccountKickedReason reason) {
        this.reason = reason.getCode();
    }

    protected void writeImpl() {
        this.writeC(2);
        this.writeD(this.reason);
    }

    public static enum AccountKickedReason {
        REASON_FALSE_DATA_STEALER_REPORT(0),
        REASON_DATA_STEALER(1),
        REASON_SOUSPICION_DATA_STEALER(3),
        REASON_NON_PAYEMENT_CELL_PHONE(4),
        REASON_30_DAYS_SUSPENDED_CASH(8),
        REASON_PERMANENTLY_SUSPENDED_CASH(16),
        REASON_PERMANENTLY_BANNED(32),
        REASON_ACCOUNT_MUST_BE_VERIFIED(64);

        private final int _code;

        private AccountKickedReason(int code) {
            this._code = code;
        }

        public final int getCode() {
            return this._code;
        }
    }
}
