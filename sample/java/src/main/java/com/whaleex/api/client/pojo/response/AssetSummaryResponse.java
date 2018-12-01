package com.whaleex.api.client.pojo.response;

public class AssetSummaryResponse {

    private Whale wal;
    private Summary summary;
    private PageResponse<AssetContent> list;


    public Whale getWal() {
        return wal;
    }

    public void setWal(Whale wal) {
        this.wal = wal;
    }

    public Summary getSummary() {
        return summary;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }

    public PageResponse<AssetContent> getList() {
        return list;
    }

    public void setList(PageResponse<AssetContent> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "AssetSummaryResponse{" +
                "wal=" + wal +
                ", summary=" + summary +
                ", list=" + list +
                '}';
    }

    public static class AssetContent {
        private String currency;
        private long currencyId;
        private String totalAmount;
        private String availableAmount;
        private String frozenAmount;
        private String fixedAmount;
        private String privatePlacement;

        @Override
        public String toString() {
            return "AssetContent{" +
                    "currency='" + currency + '\'' +
                    ", currencyId=" + currencyId +
                    ", totalAmount='" + totalAmount + '\'' +
                    ", availableAmount='" + availableAmount + '\'' +
                    ", frozenAmount='" + frozenAmount + '\'' +
                    ", fixedAmount='" + fixedAmount + '\'' +
                    ", privatePlacement='" + privatePlacement + '\'' +
                    '}';
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public long getCurrencyId() {
            return currencyId;
        }

        public void setCurrencyId(long currencyId) {
            this.currencyId = currencyId;
        }

        public String getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(String totalAmount) {
            this.totalAmount = totalAmount;
        }

        public String getAvailableAmount() {
            return availableAmount;
        }

        public void setAvailableAmount(String availableAmount) {
            this.availableAmount = availableAmount;
        }

        public String getFrozenAmount() {
            return frozenAmount;
        }

        public void setFrozenAmount(String frozenAmount) {
            this.frozenAmount = frozenAmount;
        }

        public String getFixedAmount() {
            return fixedAmount;
        }

        public void setFixedAmount(String fixedAmount) {
            this.fixedAmount = fixedAmount;
        }

        public String getPrivatePlacement() {
            return privatePlacement;
        }

        public void setPrivatePlacement(String privatePlacement) {
            this.privatePlacement = privatePlacement;
        }
    }

    public static class Summary {
        private String amount;
        private String shortName;
        private boolean withdrawNeedVerify;

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getShortName() {
            return shortName;
        }

        public void setShortName(String shortName) {
            this.shortName = shortName;
        }

        public boolean isWithdrawNeedVerify() {
            return withdrawNeedVerify;
        }

        public void setWithdrawNeedVerify(boolean withdrawNeedVerify) {
            this.withdrawNeedVerify = withdrawNeedVerify;
        }
    }

    public static class Whale {
        private String freeAmount;
        private String stakeAmount;
        private String unStakingAmount;
        private String privatePlacement;

        public String getFreeAmount() {
            return freeAmount;
        }

        public void setFreeAmount(String freeAmount) {
            this.freeAmount = freeAmount;
        }

        public String getStakeAmount() {
            return stakeAmount;
        }

        public void setStakeAmount(String stakeAmount) {
            this.stakeAmount = stakeAmount;
        }

        public String getUnStakingAmount() {
            return unStakingAmount;
        }

        public void setUnStakingAmount(String unStakingAmount) {
            this.unStakingAmount = unStakingAmount;
        }

        public String getPrivatePlacement() {
            return privatePlacement;
        }

        public void setPrivatePlacement(String privatePlacement) {
            this.privatePlacement = privatePlacement;
        }
    }

}
